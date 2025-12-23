package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.model.UserNotification;
import com.kizilaslan.recoverAiBackend.request.NotificationSeenStatusUpdateRequest;
import com.kizilaslan.recoverAiBackend.service.UserNotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final UserNotificationService userNotificationService;

    @GetMapping("/")
    public ResponseEntity<List<UserNotification>> getAllUserNotifications() {
        List<UserNotification> userNotifications = userNotificationService.getUserNotifications();
        return ResponseEntity.ok(userNotifications);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserNotification> getUserNotificationById(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(userNotificationService.getNotificationByIdAndUser(id));
    }

    @PostMapping("/update-seen")
    public ResponseEntity<?> updateSeen(@RequestBody NotificationSeenStatusUpdateRequest request) {
        userNotificationService.updateNotificationSeen(request.getNotificationId());
        return ResponseEntity.ok().build();
    }
}
