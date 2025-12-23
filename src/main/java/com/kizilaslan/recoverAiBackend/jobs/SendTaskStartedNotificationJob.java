package com.kizilaslan.recoverAiBackend.jobs;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserNotification;
import com.kizilaslan.recoverAiBackend.service.AiService;
import com.kizilaslan.recoverAiBackend.service.NotificationService;
import com.kizilaslan.recoverAiBackend.service.UserNotificationService;
import com.kizilaslan.recoverAiBackend.service.UserService;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class SendTaskStartedNotificationJob implements Job {

    private NotificationService notificationService;
    private UserNotificationService userNotificationService;
    private UserService userService;
    private AiService aiService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String deviceId = (String) context.getJobDetail().getJobDataMap().get("deviceId");
        String taskName = (String) context.getJobDetail().getJobDataMap().get("taskName");
        UUID userId = (UUID) context.getJobDetail().getJobDataMap().get("userId");
        AppUser user = userService.findById(userId);
        String body = aiService.getRoutineTaskNotification(user, taskName);
        try {
            if (taskName != null) {
                UserNotification userNotification = new UserNotification(user, "Rutin görevin başladı!", body);
                userNotificationService.save(userNotification);
                notificationService.sendNotification(deviceId, userNotification.getTitle(), userNotification.getBody());

            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
