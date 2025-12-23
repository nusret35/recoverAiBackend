package com.kizilaslan.recoverAiBackend.scheduler;

import com.kizilaslan.recoverAiBackend.jobs.CheckRoutineTasksStatusJob;
import com.kizilaslan.recoverAiBackend.jobs.SendTaskStartedNotificationJob;
import com.kizilaslan.recoverAiBackend.model.TaskStatus;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskDay;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskLog;
import com.kizilaslan.recoverAiBackend.service.UserRoutineService;
import lombok.AllArgsConstructor;
import org.quartz.SchedulerException;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
@AllArgsConstructor
public class ScheduledRoutineTaskService {

    private final UserRoutineService userRoutineService;
    private final QuartzTaskSchedulerService quartzTaskSchedulerService;

    @Scheduled(cron = "0 0 12 * * *", zone = "UTC")
    public void createUserRoutineTasksLogs() {
        LocalDate utcToday = LocalDate.now(ZoneOffset.UTC);
        LocalDate utcTomorrow = utcToday.plusDays(1);
        List<UserRoutineTaskDay> taskDays = userRoutineService
                .findAllUsersRoutineTaskDaysByDayOfWeek(utcTomorrow.getDayOfWeek());

        taskDays.forEach(taskDay -> {
            LocalTime startTime = taskDay.getRoutineTask().getStartTime();
            LocalTime endTime = taskDay.getRoutineTask().getEndTime();
            ZoneId zoneId = taskDay.getRoutineTask().getZoneId();
            ZonedDateTime userStartTime = utcTomorrow.atTime(startTime).atZone(zoneId);
            ZonedDateTime userEndTime = utcTomorrow.atTime(endTime).atZone(zoneId);
            ZonedDateTime startTimeUtcEquivalent = userStartTime.withZoneSameInstant(ZoneOffset.UTC);
            ZonedDateTime endTimeUtcEquivalent = userEndTime.withZoneSameInstant(ZoneOffset.UTC);
            LocalDateTime controlRunAt = endTimeUtcEquivalent.toLocalDateTime();
            LocalDateTime startNotificationRunAt = startTimeUtcEquivalent.toLocalDateTime();

            LocalDate logDate = startNotificationRunAt.toLocalDate();

            List<UserRoutineTaskLog> existingLogs = userRoutineService
                    .findUserRoutineTaskLogsRoutineDate(taskDay.getRoutineTask().getUser().getId(), logDate);
            boolean logExists = existingLogs.stream()
                    .anyMatch(log -> log.getRoutineDay().getId().equals(taskDay.getId()));

            if (logExists) {
                return;
            }

            UserRoutineTaskLog log = new UserRoutineTaskLog(taskDay, TaskStatus.NEUTRAL, logDate);
            UUID logId = userRoutineService.saveUserRoutineTaskLog(log);

            String jobName = "checkTask-" + logId;
            String jobGroup = "routineTasks";

            Map<String, Object> jobData = Map.of("taskLogId", logId);

            try {
                quartzTaskSchedulerService.scheduleJob(
                        CheckRoutineTasksStatusJob.class,
                        jobName,
                        jobGroup,
                        jobData,
                        controlRunAt);
                AppUser user = taskDay.getRoutineTask().getUser();
                if (user.getNotificationDeviceId().isEmpty()) {
                    return;
                }
                String notificationJobName = "taskStarted-" + taskDay.getRoutineTask().getTaskName() + taskDay.getId()
                        + "-" + utcTomorrow;
                Map<String, Object> notificationJobData = Map.of(
                        "deviceId", user.getNotificationDeviceId(),
                        "taskName", taskDay.getRoutineTask().getTaskName(),
                        "userId", user.getId());
                quartzTaskSchedulerService.scheduleJob(
                        SendTaskStartedNotificationJob.class,
                        notificationJobName,
                        "notifications",
                        notificationJobData,
                        startNotificationRunAt);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule job for task log ID: " + logId, e);
            }
        });
    }

    public boolean cancelTask(UUID routineTaskId) {
        try {
            if (isTaskScheduled(routineTaskId)) {
                return quartzTaskSchedulerService.cancelScheduledJob("checkTask-" + routineTaskId, "routineTasks");
            }
            return true;
        } catch (SchedulerException e) {
            throw new RuntimeException("Failed to cancel task for ID: " + routineTaskId, e);
        }
    }

    public boolean isTaskScheduled(UUID routineTaskId) {
        try {
            return quartzTaskSchedulerService.isJobScheduled("checkTask-" + routineTaskId, "routineTasks");
        } catch (SchedulerException e) {
            return false;
        }
    }
}
