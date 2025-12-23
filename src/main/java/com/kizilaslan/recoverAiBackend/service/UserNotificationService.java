package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserNotification;
import com.kizilaslan.recoverAiBackend.repository.UserNotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserNotificationService {

    private final UserNotificationRepository userNotificationRepository;

    public void save(UserNotification userNotification) {
        UUID userId = userNotification.getUser().getId();
        List<UserNotification> notifications = userNotificationRepository
                .findByUserIdOrderByCreatedAtAsc(userId);

        if (notifications.size() >= 20) {
            UserNotification oldest = notifications.get(0);
            userNotificationRepository.delete(oldest);
        }

        userNotificationRepository.save(userNotification);
    }

    public List<UserNotification> getUserNotifications() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        return userNotificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
    }

    public UserNotification getNotificationByIdAndUser(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        return userNotificationRepository.findByUserIdAndId(user.getId(), id);
    }

    public void updateNotificationSeen(UUID id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserNotification notification = userNotificationRepository.findByUserIdAndId(user.getId(), id);
        notification.setSeen(true);
        userNotificationRepository.save(notification);
    }

}
