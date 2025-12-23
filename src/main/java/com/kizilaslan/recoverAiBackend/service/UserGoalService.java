package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.dto.UserGoalDTO;
import com.kizilaslan.recoverAiBackend.exception.UserGoalAlreadyExists;
import com.kizilaslan.recoverAiBackend.exception.UserGoalNotFound;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserGoal;
import com.kizilaslan.recoverAiBackend.model.UserGoalProgressLog;
import com.kizilaslan.recoverAiBackend.repository.UserGoalProgressLogRepository;
import com.kizilaslan.recoverAiBackend.repository.UserGoalRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserGoalService {

    private final UserGoalRepository userGoalRepository;
    private final UserGoalProgressLogRepository userGoalProgressLogRepository;
    private final ModelMapper modelMapper;

    public Optional<UserGoal> getUserGoal(UUID userId) {
        return userGoalRepository.findByUserId(userId);

    }

    @Transactional
    public UserGoal create(UserGoal userGoal) {
        boolean exists = userGoalRepository.existsByUserIdAndIsCompleted(userGoal.getId(), false);
        if (exists) {
            throw new UserGoalAlreadyExists("User can only have one goal");
        }
        return userGoalRepository.save(userGoal);
    }

    @Transactional
    public UserGoal update(UserGoal userGoal) {
        boolean exists = userGoalRepository.existsById(userGoal.getId());
        if (!exists) {
            throw new UserGoalNotFound("User goal not found");
        }
        return userGoalRepository.save(userGoal);
    }

    @Transactional
    public UserGoalProgressLog addGoalProgressLog(UserGoalProgressLog userGoalProgressLog) {
        return userGoalProgressLogRepository.save(userGoalProgressLog);
    }

    public Long getCompletedCountByUserId(UUID userId) {
        return userGoalRepository.getCompletedCountByUserId(userId);
    }

    public List<UserGoalProgressLog> getUserGoalProgressLogsOfTheWeek(UUID userId) {
        LocalDate today = LocalDate.now();
        Optional<UserGoal> userGoal = getUserGoal(userId);
        if (userGoal.isEmpty()) {
            throw new UserGoalNotFound("User goal not found");
        }
        LocalDate startDateLocal = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endDateLocal = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        LocalDateTime startOfWeek = startDateLocal.atStartOfDay();
        LocalDateTime endOfWeek = endDateLocal.atTime(23, 59, 59);

        return userGoalProgressLogRepository.findUserGoalProgressLogByStartEndDateAndGoal(userId, startOfWeek,
                endOfWeek, userGoal.get().getId());
    }

    public List<UserGoalProgressLog> getUserProgressLogsFromStartToEndDate(UUID userId, LocalDate startDate,
            LocalDate endDate, UUID goalId) {
        return userGoalProgressLogRepository.findUserGoalProgressLogByStartEndDateAndGoal(userId,
                startDate.atStartOfDay(), endDate.atTime(23, 59, 59), goalId);
    }

    @Transactional
    public void deleteUserGoalById(UUID goalId, AppUser user) {
        userGoalProgressLogRepository.deleteAllByGoalId(goalId);
        userGoalRepository.deleteUserGoalByIdAndUser(goalId, user);
    }

    @Transactional
    public Long deleteAllLogsOfGoal(UUID goalId) {
        return userGoalProgressLogRepository.deleteAllByGoalId(goalId);
    }

    @Transactional
    public List<UserGoalDTO> getAllUserGoals() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserGoal> userGoals = userGoalRepository.findAllByUserId(user.getId());
        List<UserGoalDTO> userGoalDTOList = Arrays.asList(modelMapper.map(userGoals, UserGoalDTO[].class));
        return userGoalDTOList.stream()
                .peek(userGoalDTO -> {
                    Float progress = userGoalDTO.getProgress();
                    Float goal = userGoalDTO.getGoal();
                    if (userGoalDTO.getIsCompleted()) {
                        userGoalDTO.setProgressInPercent((float) 100);
                    } else if (goal != 0) {
                        userGoalDTO.setProgressInPercent((progress / goal) * 100);
                    } else {
                        userGoalDTO.setProgressInPercent((float) 0);
                    }
                })
                .toList();
    }
}