package com.kizilaslan.recoverAiBackend.request;

import lombok.Data;

import java.util.UUID;

@Data
public class NotificationSeenStatusUpdateRequest {
    private UUID notificationId;
}
