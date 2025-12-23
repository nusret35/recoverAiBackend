package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.UserRoutineTaskDayDTO;
import com.kizilaslan.recoverAiBackend.dto.UserRoutineTaskLogDTO;
import com.kizilaslan.recoverAiBackend.jobs.CheckRoutineTasksStatusJob;
import com.kizilaslan.recoverAiBackend.jobs.SendTaskStartedNotificationJob;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.request.AddRoutineTaskRequest;
import com.kizilaslan.recoverAiBackend.request.RemoveRoutineTaskDayRequest;
import com.kizilaslan.recoverAiBackend.request.UpdateTaskLogStatusRequest;
import com.kizilaslan.recoverAiBackend.response.AiCommentResponse;
import com.kizilaslan.recoverAiBackend.scheduler.QuartzTaskSchedulerService;
import com.kizilaslan.recoverAiBackend.scheduler.ScheduledRoutineTaskService;
import com.kizilaslan.recoverAiBackend.service.AiService;
import com.kizilaslan.recoverAiBackend.service.UserRoutineService;
import com.kizilaslan.recoverAiBackend.util.StringUtil;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.quartz.SchedulerException;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-routine")
@RequiredArgsConstructor
public class UserRoutineController {

    private final UserRoutineService userRoutineService;
    private final ScheduledRoutineTaskService scheduledRoutineTaskService;
    private final QuartzTaskSchedulerService quartzTaskSchedulerService;
    private final ModelMapper modelMapper;
    private final AiService aiService;

    @GetMapping("/daily")
    public ResponseEntity<List<UserRoutineTaskDayDTO>> getDailyRoutine(@RequestParam(value = "day") String day) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        DayOfWeek dayOfWeek = DayOfWeek.valueOf(day.toUpperCase());
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserRoutineTaskDay> routineTasks = userRoutineService.findUserRoutineTaskDaysByDayOfWeek(user.getId(),
                dayOfWeek);
        List<UserRoutineTaskDayDTO> routineTaskDayDTOList = Arrays
                .asList(modelMapper.map(routineTasks, UserRoutineTaskDayDTO[].class));

        return ResponseEntity.ok(routineTaskDayDTOList);
    }

    @GetMapping("/routine-by-date")
    public ResponseEntity<List<UserRoutineTaskLogDTO>> getRoutineByDate(@RequestParam(value = "date") String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        LocalDate localDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
        List<UserRoutineTaskLogDTO> routineTaskLogsDTO;
        if (localDate.equals(LocalDate.now(user.getTimezone()))
                || localDate.isBefore(LocalDate.now(user.getTimezone()))) {
            List<UserRoutineTaskLog> routineTaskLogs = userRoutineService
                    .findUserRoutineTaskLogsRoutineDate(user.getId(), localDate);
            routineTaskLogsDTO = Arrays.asList(modelMapper.map(routineTaskLogs, UserRoutineTaskLogDTO[].class));
        } else {
            routineTaskLogsDTO = userRoutineService
                    .findUserRoutineTaskDaysByDayOfWeek(user.getId(), localDate.getDayOfWeek())
                    .stream()
                    .map(userRoutineTaskDay -> {
                        UserRoutineTaskLogDTO log = new UserRoutineTaskLogDTO();
                        log.setId(UUID.randomUUID());
                        log.setRoutineDay(userRoutineTaskDay);
                        log.setTaskStatus(TaskStatus.NEUTRAL);
                        log.setRoutineDate(localDate);
                        log.setCreatedDate(LocalDateTime.now());
                        return log;
                    })
                    .collect(Collectors.toList());
        }
        return ResponseEntity.ok(routineTaskLogsDTO);
    }

    @GetMapping("/active-routine-task")
    public ResponseEntity<UserRoutineTaskLogDTO> getActiveRoutineTask(@RequestParam(value = "date") String date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserRoutineTaskLog> activeTask = userRoutineService.findActiveUserTask(user.getId(), date);
        return activeTask
                .map(userRoutineTaskLog -> ResponseEntity
                        .ok(modelMapper.map(userRoutineTaskLog, UserRoutineTaskLogDTO.class)))
                .orElseGet(() -> ResponseEntity.noContent().build());
    }

    @GetMapping("/task-log")
    public ResponseEntity<List<UserRoutineTaskLogDTO>> getTaskLog(
            @RequestParam(value = "date") @DateTimeFormat(pattern = "dd-MM-yyyy") LocalDate date) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserRoutineTaskLog> routineTaskLogs = userRoutineService.findUserRoutineTaskLogsByCreatedDate(user.getId(),
                date);
        List<UserRoutineTaskLogDTO> routineTaskLogsDTOList = Arrays
                .asList(modelMapper.map(routineTaskLogs, UserRoutineTaskLogDTO[].class));
        return ResponseEntity.ok(routineTaskLogsDTOList);
    }

    @Transactional
    @PostMapping("/update-task-status")
    public ResponseEntity<UserRoutineTaskLogDTO> updateTaskStatus(@RequestBody UpdateTaskLogStatusRequest request) {
        if (userRoutineService.updateTaskStatus(request.getId(), request.getStatus())) {
            return ResponseEntity.ok(modelMapper.map(userRoutineService.findUserRoutineTaskLogById(request.getId()),
                    UserRoutineTaskLogDTO.class));
        }
        return ResponseEntity.internalServerError().build();
    }

    @Transactional
    @PostMapping("/add-routine-task")
    public ResponseEntity<UserRoutineTaskDayDTO> addRoutineTask(@RequestBody AddRoutineTaskRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        String emoji = aiService.getRoutineTaskEmoji(request.getTaskName());
        UserRoutineTask userRoutineTask = new UserRoutineTask(user, StringUtil.capitalize(request.getTaskName()), emoji,
                request.getStartTime(), request.getEndTime(), user.getTimezone());
        DayOfWeek dayOfWeek = DayOfWeek.of(request.getWeekDay());
        UserRoutineTask task = userRoutineService.saveUserRoutineTask(userRoutineTask);
        UserRoutineTaskDay userRoutineTaskDay = new UserRoutineTaskDay(task, dayOfWeek);
        UserRoutineTaskDay taskDay = userRoutineService.saveUserRoutineTaskDay(userRoutineTaskDay);
        ZoneId userZone = user.getTimezone();
        LocalDate today = LocalDate.now(userZone);
        LocalDate tomorrow = today.plusDays(1);

        List<LocalDate> datesToCreate = new ArrayList<>();
        if (request.getWeekDay() == today.getDayOfWeek().getValue()) {
            datesToCreate.add(today);
        }
        if (request.getWeekDay() == tomorrow.getDayOfWeek().getValue()) {
            datesToCreate.add(tomorrow);
        }

        for (LocalDate targetDate : datesToCreate) {
            UserRoutineTaskLog log = new UserRoutineTaskLog(taskDay, TaskStatus.NEUTRAL, targetDate);
            UUID logId = userRoutineService.saveUserRoutineTaskLog(log);

            LocalTime startTime = userRoutineTaskDay.getRoutineTask().getStartTime();
            LocalTime endTime = userRoutineTaskDay.getRoutineTask().getEndTime();

            LocalDateTime localStartDateTime = targetDate.atTime(startTime);
            ZonedDateTime userZonedStart = localStartDateTime.atZone(userZone);
            ZonedDateTime utcZonedStart = userZonedStart.withZoneSameInstant(ZoneOffset.UTC);
            LocalDateTime runNotificationJobAt = utcZonedStart.toLocalDateTime();

            LocalDateTime localEndDateTime = targetDate.atTime(endTime);
            ZonedDateTime userZonedEnd = localEndDateTime.atZone(userZone);
            ZonedDateTime utcZonedEnd = userZonedEnd.withZoneSameInstant(ZoneOffset.UTC);
            LocalDateTime runCheckRoutineJobAt = utcZonedEnd.toLocalDateTime();

            try {
                String notificationJobName = "taskStarted-" + userRoutineTaskDay.getRoutineTask().getTaskName()
                        + userRoutineTaskDay.getId() + "-" + targetDate;
                if (!user.getNotificationDeviceId().isEmpty()) {
                    Map<String, Object> notificationJobData = Map.of("deviceId", user.getNotificationDeviceId(),
                            "taskName", userRoutineTask.getTaskName(), "userId", user.getId());
                    quartzTaskSchedulerService.scheduleJob(
                            SendTaskStartedNotificationJob.class,
                            notificationJobName,
                            "notifications",
                            notificationJobData,
                            runNotificationJobAt);
                }
                String checkJobName = "checkTask-" + logId;
                String jobGroup = "routineTasks";
                Map<String, Object> jobData = Map.of("taskLogId", logId);
                quartzTaskSchedulerService.scheduleJob(
                        CheckRoutineTasksStatusJob.class,
                        checkJobName,
                        jobGroup,
                        jobData,
                        runCheckRoutineJobAt);
            } catch (SchedulerException e) {
                throw new RuntimeException("Failed to schedule job for task log ID: " + logId, e);
            }
        }
        UserRoutineTaskDayDTO userRoutineTaskDayDTO = modelMapper.map(userRoutineTaskDay, UserRoutineTaskDayDTO.class);
        return ResponseEntity.ok(userRoutineTaskDayDTO);
    }

    @Transactional
    @PostMapping("/delete-routine-task-day")
    public ResponseEntity<?> deleteRoutineTaskDay(@RequestBody RemoveRoutineTaskDayRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        scheduledRoutineTaskService.cancelTask(request.getId());
        userRoutineService.removeUserRoutineTaskDay(request.getId(), user.getId());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/routine-comment")
    public ResponseEntity<AiCommentResponse> getRoutineComment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        String routineComment = aiService.getRoutineComment(user);
        if (routineComment == null) {
            return ResponseEntity.noContent().build();
        }
        AiCommentResponse aiCommentResponse = new AiCommentResponse(routineComment);
        return ResponseEntity.ok(aiCommentResponse);
    }

}
