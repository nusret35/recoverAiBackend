package com.kizilaslan.recoverAiBackend.jobs;

import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.repository.SobrietyAchievementRepository;
import com.kizilaslan.recoverAiBackend.response.SobrietyAchievementNotificationResponse;
import com.kizilaslan.recoverAiBackend.scheduler.ScheduledAddictionService;
import com.kizilaslan.recoverAiBackend.service.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@AllArgsConstructor
public class SetAddictionAchievementJob implements Job {

    private final UserService userService;
    private final AiService aiService;
    private UserAddictionService userAddictionService;
    private SobrietyAchievementRepository sobrietyAchievementRepository;
    private ScheduledAddictionService scheduledAddictionService;
    private NotificationService notificationService;
    private UserNotificationService userNotificationService;

    @Override
    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UserAddictionId userAddictionId = (UserAddictionId) context.getJobDetail().getJobDataMap()
                .get("userAddictionId");
        try {
            UserAddiction userAddiction = userAddictionService.findById(userAddictionId.getUser(),
                    userAddictionId.getAddiction());
            SobrietyAchievement nextAchievement = userAddiction.getNextAchievement();
            userAddictionService.addSobrietyAchievement(userAddictionId, nextAchievement.getId());
            Optional<SobrietyAchievement> newAchievement = sobrietyAchievementRepository
                    .findById(nextAchievement.getNextAchievementId());
            newAchievement.ifPresent(userAddiction::setNextAchievement);
            userAddictionService.update(userAddiction);
            AppUser user = userService.findById(userAddictionId.getUser());
            scheduledAddictionService.scheduleAddictionAchievementTask(userAddiction);
            if (!user.getNotificationDeviceId().isEmpty()) {
                SobrietyAchievementNotificationResponse notificationResponse = aiService
                        .getAddictionAchievementNotification(userAddiction.getAddiction(), nextAchievement, user);
                UserNotification userNotification = new UserNotification(user, notificationResponse.getTitle(),
                        notificationResponse.getBody());
                userNotificationService.save(userNotification);
                notificationService.sendNotification(
                        user.getNotificationDeviceId(),
                        userNotification.getTitle(),
                        userNotification.getBody());

            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
