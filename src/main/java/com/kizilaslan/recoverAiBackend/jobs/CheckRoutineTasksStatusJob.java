package com.kizilaslan.recoverAiBackend.jobs;

import com.kizilaslan.recoverAiBackend.exception.UserRoutineTaskLogNotFoundException;
import com.kizilaslan.recoverAiBackend.model.TaskStatus;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskLog;
import com.kizilaslan.recoverAiBackend.service.UserRoutineService;
import lombok.AllArgsConstructor;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@AllArgsConstructor
public class CheckRoutineTasksStatusJob implements Job {

    private UserRoutineService userRoutineService;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        UUID taskLogId = (UUID) context.getJobDetail().getJobDataMap().get("taskLogId");

        try {
            UserRoutineTaskLog log = userRoutineService.findUserRoutineTaskLogById(taskLogId);
            if (log.getTaskStatus() == TaskStatus.NEUTRAL) {
                boolean updated = userRoutineService.updateTaskStatus(taskLogId, TaskStatus.SKIPPED);
                if (!updated) {
                    throw new UserRoutineTaskLogNotFoundException("UserRoutineTaskLog with id " + taskLogId + " not found");
                }
            }
        } catch (Exception e) {
            throw new JobExecutionException(e);
        }
    }
}
