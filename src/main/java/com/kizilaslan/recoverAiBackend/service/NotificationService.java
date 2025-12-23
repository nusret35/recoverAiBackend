package com.kizilaslan.recoverAiBackend.service;

import com.niamedtech.expo.exposerversdk.ExpoPushNotificationClient;
import com.niamedtech.expo.exposerversdk.request.PushNotification;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final ExpoPushNotificationClient pushNotificationClient;

    public void sendNotification(String deviceId, String title, String body) throws IOException {
        PushNotification pushNotification = new PushNotification();
        List<String> to = List.of(deviceId);
        pushNotification.setTo(to);
        pushNotification.setTitle(title);
        pushNotification.setBody(body);
        pushNotification.setSound("sound.wav");
        List<PushNotification> notifications = new ArrayList<>();
        notifications.add(pushNotification);
        pushNotificationClient.sendPushNotifications(notifications);

    }
}
