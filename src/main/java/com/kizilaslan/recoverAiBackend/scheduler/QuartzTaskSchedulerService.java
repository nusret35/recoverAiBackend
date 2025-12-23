package com.kizilaslan.recoverAiBackend.scheduler;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.AllArgsConstructor;
import org.quartz.*;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@AllArgsConstructor
public class QuartzTaskSchedulerService {

    private final Scheduler scheduler;

    @PostConstruct
    public void init() throws SchedulerException {
        scheduler.start();
    }

    @PreDestroy
    public void destroy() throws SchedulerException {
        scheduler.shutdown();
    }

    public void scheduleJob(
            Class<? extends Job> jobClass,
            String jobName,
            String jobGroup,
            Map<String, Object> jobData,
            LocalDateTime runAt
    ) throws SchedulerException {
        JobDataMap dataMap = new JobDataMap(jobData);

        JobDetail jobDetail = JobBuilder.newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .usingJobData(dataMap)
                .storeDurably()
                .build();

        Trigger trigger = TriggerBuilder.newTrigger()
                .forJob(jobDetail)
                .withIdentity(jobName + "Trigger", jobGroup)
                .startAt(Timestamp.valueOf(runAt))
                .withSchedule(SimpleScheduleBuilder.simpleSchedule())
                .build();

        scheduler.scheduleJob(jobDetail, trigger);
    }

    public boolean cancelScheduledJob(String jobName, String jobGroup) throws SchedulerException {
        return scheduler.deleteJob(JobKey.jobKey(jobName, jobGroup));
    }

    public boolean isJobScheduled(String jobName, String jobGroup) throws SchedulerException {
        return scheduler.checkExists(JobKey.jobKey(jobName, jobGroup));
    }
}
