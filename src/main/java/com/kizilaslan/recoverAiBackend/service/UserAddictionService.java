package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.exception.AddictionNotFoundException;
import com.kizilaslan.recoverAiBackend.exception.UserAddictionAlreadyExistsException;
import com.kizilaslan.recoverAiBackend.exception.UserAddictionNotFoundException;
import com.kizilaslan.recoverAiBackend.exception.UserNotFoundException;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.repository.*;
import com.kizilaslan.recoverAiBackend.response.ResetTimerResponse;
import com.kizilaslan.recoverAiBackend.response.SobrietyAchievementNotificationResponse;
import com.kizilaslan.recoverAiBackend.scheduler.ScheduledAddictionService;
import com.kizilaslan.recoverAiBackend.util.TimeUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class UserAddictionService {

    private final UserAddictionRepository userAddictionRepository;
    private final UserRepository userRepository;
    private final AddictionRepository addictionRepository;
    private final SobrietyAchievementRepository sobrietyAchievementRepository;
    private final ScheduledAddictionService userAddictionScheduledService;
    private final AppConfigRepository appConfigRepository;
    private final LocalizationService localizationService;
    private final AiService aiService;
    private final UserNotificationService userNotificationService;
    private final NotificationService notificationService;

    @Transactional
    public UserAddiction create(UserAddiction userAddiction) {
        UserAddictionId userAddictionId = userAddiction.getId();
        UUID userId = userAddictionId.getUser();
        UUID addictionId = userAddictionId.getAddiction();
        boolean userExists = userRepository.existsById(userId);
        if (!userExists) {
            throw new UserNotFoundException("User with id " + userId + " does not exist");
        }
        boolean addictionExists = addictionRepository.existsById(addictionId);
        if (!addictionExists) {
            throw new AddictionNotFoundException("Addiction with id " + addictionId + " does not exist");
        }
        boolean userAddictionExists = userAddictionRepository.existsById(userAddiction.getId());
        if (userAddictionExists) {
            throw new UserAddictionAlreadyExistsException("User addiction already exists");
        }
        return userAddictionRepository.save(userAddiction);
    }

    @Transactional
    public UserAddiction update(UserAddiction userAddiction) {
        boolean userAddictionExists = userAddictionRepository.existsById(userAddiction.getId());
        if (!userAddictionExists) {
            throw new UserAddictionNotFoundException("User addiction not found");
        }
        return userAddictionRepository.save(userAddiction);
    }

    @Transactional
    public void addSobrietyAchievement(UserAddictionId userAddictionId, UUID achievementId) {
        UserAddiction userAddiction = userAddictionRepository.findById(userAddictionId)
                .orElseThrow(() -> new ResourceNotFoundException("UserAddiction not found"));

        SobrietyAchievement achievement = sobrietyAchievementRepository.findById(achievementId)
                .orElseThrow(() -> new ResourceNotFoundException("SobrietyAchievement not found"));

        if (userAddiction.getAchievements() == null) {
            userAddiction.setAchievements(new ArrayList<>());
        }

        if (!userAddiction.getAchievements().contains(achievement)) {
            userAddiction.getAchievements().add(achievement);
        }

        userAddictionRepository.save(userAddiction);
    }

    public List<UserAddiction> findAllByUserId(UUID id) {
        return userAddictionRepository.findAllByUserId(id);
    }

    public UserAddiction findById(UUID userId, UUID addictionId) {
        UserAddictionId userAddictionId = new UserAddictionId(userId, addictionId);
        return userAddictionRepository.findById(userAddictionId)
                .orElseThrow(() -> new UserAddictionNotFoundException(("User addiction not found")));
    }

    @Transactional
    public void deleteById(UUID addictionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserAddictionId id = new UserAddictionId(user.getId(), addictionId);
        Optional<UserAddiction> userAddiction = userAddictionRepository.findById(id);
        userAddiction.ifPresent(addiction -> userAddictionScheduledService.cancelScheduledAddictionAchievementTask(
                user,
                id.getAddiction()));
        userAddictionRepository.deleteById(id);
    }

    @Transactional
    public ResetTimerResponse resetTimer(UUID addictionId) {
        AppConfig appConfig = appConfigRepository.getAppConfig();
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserAddictionId id = new UserAddictionId(user.getId(), addictionId);
        Optional<UserAddiction> optionalUserAddiction = userAddictionRepository.findById(id);
        if (optionalUserAddiction.isEmpty()) {
            throw new UserAddictionNotFoundException(("User addiction not found"));
        }
        UserAddiction userAddiction = optionalUserAddiction.get();
        userAddictionScheduledService.cancelScheduledAddictionAchievementTask(user,
                userAddiction.getAddiction().getId());
        String duration = TimeUtils.getDurationString(userAddiction.getStartDate(), LocalDateTime.now());
        String relapsedComment = aiService.getRelapsedComment(userAddiction, duration, user.getLanguage().name());
        SobrietyAchievement firstAchievement = sobrietyAchievementRepository.getFirstMinuteAchievement();
        userAddiction.setLastRelapseDate(LocalDateTime.now());
        userAddiction.setUpdatedAt(LocalDateTime.now());
        userAddiction.setNextAchievement(firstAchievement);
        userAddictionRepository.save(userAddiction);
        Long resetTimerCount = appConfig.getResetTimerMessageCount();
        Long userResetTimerMessageIndex = user.getResetTimerMessageIndex();
        Localization localization = localizationService.getResetTimerMessage(user.getLanguage(),
                user.getResetTimerMessageIndex());
        userAddictionScheduledService.scheduleAddictionAchievementTask(userAddiction);
        if (userResetTimerMessageIndex + 1 == resetTimerCount) {
            appConfig.setResetTimerMessageCount(0L);
            appConfigRepository.save(appConfig);
        }
        return new ResetTimerResponse(localization.getValue(), relapsedComment);
    }

    @Transactional
    public UserAddiction updateSobrietyAchievement(AppUser user, UserAddiction userAddiction) {
        SobrietyAchievement nextAchievement = userAddiction.getNextAchievement();
        addSobrietyAchievement(userAddiction.getId(), nextAchievement.getId());
        Optional<SobrietyAchievement> newAchievement = sobrietyAchievementRepository
                .findById(nextAchievement.getNextAchievementId());
        newAchievement.ifPresent(userAddiction::setNextAchievement);
        UserAddiction updatedUserAddiction = update(userAddiction);
        userAddictionScheduledService.scheduleAddictionAchievementTask(userAddiction);
        if (!user.getNotificationDeviceId().isEmpty()) {
            SobrietyAchievementNotificationResponse notificationResponse = aiService
                    .getAddictionAchievementNotification(userAddiction.getAddiction(), nextAchievement, user);
            UserNotification userNotification = new UserNotification(user, notificationResponse.getTitle(),
                    notificationResponse.getBody());
            userNotificationService.save(userNotification);
            try {
                notificationService.sendNotification(
                        user.getNotificationDeviceId(),
                        userNotification.getTitle(),
                        userNotification.getBody());
            } catch (IOException exception) {
                throw new RuntimeException("Failed to send notification");
            }
        }
        return updatedUserAddiction;
    }

    public boolean checkUserAddictionPassedAchievementBySeconds(UserAddiction userAddiction, int seconds) {
        SobrietyAchievement nextAchievement = userAddiction.getNextAchievement();
        if (nextAchievement == null) {
            return false;
        }

        long achievementDurationSeconds = nextAchievement.getMinuteDuration() * 60L;
        long secondsSinceRelapse = ChronoUnit.SECONDS.between(
                userAddiction.getLastRelapseDate(),
                LocalDateTime.now()
        );

        return secondsSinceRelapse >= achievementDurationSeconds + seconds;
    }
}
