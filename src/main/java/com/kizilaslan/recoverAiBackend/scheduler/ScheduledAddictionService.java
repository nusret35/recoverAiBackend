package com.kizilaslan.recoverAiBackend.scheduler;

import com.kizilaslan.recoverAiBackend.jobs.SetAddictionAchievementJob;
import com.kizilaslan.recoverAiBackend.model.AddictionNotificationScheduledJob;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserAddiction;
import com.kizilaslan.recoverAiBackend.repository.AddictionNotificationScheduledJobRepository;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ScheduledAddictionService {

    private final QuartzTaskSchedulerService quartzTaskSchedulerService;
    private final AddictionNotificationScheduledJobRepository addictionNotificationScheduledJobRepository;

    public void scheduleAddictionAchievementTask(UserAddiction userAddiction) {
        String jobName = "scheduleAddictionAchievement-" + userAddiction.getId().getUser() + "-"
                + userAddiction.getId().getAddiction() + "-" + UUID.randomUUID();
        String jobGroup = "addictionAchievements";
        LocalDateTime runAt = userAddiction.getLastRelapseDate()
                .plusMinutes(userAddiction.getNextAchievement().getMinuteDuration());
        AddictionNotificationScheduledJob jobEntry = new AddictionNotificationScheduledJob(
                userAddiction.getId().getAddiction(),
                userAddiction.getUser(),
                jobName,
                runAt);
        addictionNotificationScheduledJobRepository.save(jobEntry);
        Map<String, Object> jobData = Map.of("userAddictionId", userAddiction.getId());
        try {
            quartzTaskSchedulerService.scheduleJob(
                    SetAddictionAchievementJob.class,
                    jobName,
                    jobGroup,
                    jobData,
                    runAt);
        } catch (SchedulerException e) {
            throw new RuntimeException(e);
        }
    }

    public void cancelScheduledAddictionAchievementTask(AppUser user, UUID addictionId) {
        addictionNotificationScheduledJobRepository.findAllByAddictionIdAndUser(addictionId, user)
                .forEach(addictionNotificationScheduledJob -> {
                    try {
                        quartzTaskSchedulerService.cancelScheduledJob(
                                addictionNotificationScheduledJob.getJobId(),
                                "addictionAchievements");
                    } catch (SchedulerException e) {
                        throw new RuntimeException(e);
                    }
                });
        addictionNotificationScheduledJobRepository
                .deleteAddictionNotificationScheduledJobsByAddictionIdAndUser(addictionId, user);
    }
}
