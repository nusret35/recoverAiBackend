package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.TaskStatus;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTask;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskDay;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskLog;
import com.kizilaslan.recoverAiBackend.repository.UserRoutineTaskDayRepository;
import com.kizilaslan.recoverAiBackend.repository.UserRoutineTaskLogRepository;
import com.kizilaslan.recoverAiBackend.repository.UserRoutineTaskRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class UserRoutineService {

    private final UserRoutineTaskRepository userRoutineTaskRepository;
    private final UserRoutineTaskDayRepository userRoutineTaskDayRepository;
    private final UserRoutineTaskLogRepository userRoutineTaskLogRepository;

    public UserRoutineTask findUserRoutineTaskById(UUID id) {
        return userRoutineTaskRepository.findById(id).orElse(null);
    }

    public List<UserRoutineTask> findAllUserRoutineTasks() {
        return userRoutineTaskRepository.findAll();
    }

    public List<UserRoutineTaskDay> findAllUsersRoutineTaskDaysByDayOfWeek(DayOfWeek dayOfWeek) {
        return userRoutineTaskDayRepository.findAllByDayOfWeek(dayOfWeek);
    }

    public List<UserRoutineTaskDay> findAllUsersRoutineTaskDaysByDaysOfWeek(Set<DayOfWeek> daysOfWeek) {
        return userRoutineTaskDayRepository.findAllByDaysOfWeek(daysOfWeek);
    }

    public List<UserRoutineTaskDay> findUserRoutineTaskDaysByDayOfWeek(UUID userId, DayOfWeek dayOfWeek) {
        return userRoutineTaskDayRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek);
    }

    public List<UserRoutineTaskDay> findUserRoutineTaskDaysByRoutineTaskId(UUID routineTaskId) {
        return userRoutineTaskDayRepository.findAllByRoutineTaskId(routineTaskId);
    }

    public UserRoutineTaskLog findUserRoutineTaskLogById(UUID id) {
        return userRoutineTaskLogRepository.findById(id).orElse(null);
    }

    public List<UserRoutineTaskLog> findUserRoutineTaskLogsByCreatedDate(UUID userId, LocalDate createdDate) {
        return userRoutineTaskLogRepository.findByUserIdAndCreatedDate(userId, createdDate);
    }

    public List<UserRoutineTaskLog> findUserRoutineTaskLogsRoutineDate(UUID userId, LocalDate routineDate) {
        return userRoutineTaskLogRepository.findByUserIdAndRoutineDate(userId, routineDate);
    }

    public Optional<UserRoutineTaskLog> findActiveRoutineTaskLogByTime(UUID userId, LocalDateTime currentTime) {
        LocalDate currentDate = currentTime.toLocalDate();
        LocalTime timeOfDay = currentTime.toLocalTime();

        return userRoutineTaskLogRepository.findActiveTaskLogByTime(
                userId,
                currentDate,
                timeOfDay);
    }

    public Optional<UserRoutineTaskLog> findFirstTaskByDate(UUID userId, LocalDate date) {
        return userRoutineTaskLogRepository.findFirstTaskLogByDate(userId, date);
    }

    public Optional<UserRoutineTaskLog> findActiveUserTask(UUID userId, String date) {
        LocalDateTime localDateTime = LocalDateTime.parse(date, DateTimeFormatter.ISO_DATE_TIME);
        Optional<UserRoutineTaskLog> activeRoutineTaskLog = userRoutineTaskLogRepository.findActiveTaskLogByTime(userId, localDateTime.toLocalDate(), localDateTime.toLocalTime());
        if (activeRoutineTaskLog.isPresent()) {
            return activeRoutineTaskLog;
        }
        LocalDate nextDay = localDateTime.plusDays(1).toLocalDate();
        return userRoutineTaskLogRepository.findFirstTaskLogByDate(userId, nextDay);
    }

    public List<UserRoutineTaskLog> findLogsBetweenDate(UUID userId, LocalDate startDate, LocalDate endDate) {
        return userRoutineTaskLogRepository.findUserRoutineTaskLogsBetweenDates(userId, startDate, endDate);
    }

    public Map<DayOfWeek, List<UserRoutineTaskDay>> getMappedOutUserRoutineTaskDays(UUID userId) {
        return Arrays.stream(DayOfWeek.values())
                .collect(Collectors.toMap(
                        dayOfWeek -> dayOfWeek,
                        dayOfWeek -> userRoutineTaskDayRepository.findByUserIdAndDayOfWeek(userId, dayOfWeek)
                ));
    }


    @Transactional
    public boolean updateTaskStatus(UUID taskId, TaskStatus taskStatus) {
        return userRoutineTaskLogRepository.updateTaskStatus(taskId, taskStatus) > 0;
    }

    @Transactional
    public UUID saveUserRoutineTaskLog(UserRoutineTaskLog userRoutineTaskLog) {
        return userRoutineTaskLogRepository.save(userRoutineTaskLog).getId();
    }

    @Transactional
    public UserRoutineTask saveUserRoutineTask(UserRoutineTask userRoutineTask) {
        return userRoutineTaskRepository.save(userRoutineTask);
    }

    @Transactional
    public UserRoutineTaskDay saveUserRoutineTaskDay(UserRoutineTaskDay userRoutineTaskDay) {
        return userRoutineTaskDayRepository.save(userRoutineTaskDay);
    }


    @Transactional
    public void removeUserRoutineTaskDay(UUID taskId, UUID userId) {
        userRoutineTaskDayRepository.deleteUserRoutineTaskDayById(taskId);
        userRoutineTaskLogRepository.deleteUserRoutineTaskLogByRoutineDateAfterAndRoutineDayId(LocalDate.now(), taskId);
        List<UserRoutineTaskLog> routineTaskLogs = userRoutineTaskLogRepository.findByUserIdAndRoutineDateAndRoutineDay(taskId, LocalDate.now(), taskId);
        if (!routineTaskLogs.isEmpty()) {
            routineTaskLogs.forEach((entry) -> {
                if (entry.getRoutineDay().getDayOfWeek() == LocalDate.now().getDayOfWeek()) {
                    ZoneId zoneId = ZoneId.of(entry.getRoutineDay().getRoutineTask().getZoneId().getId());
                    LocalTime startTime = entry.getRoutineDay().getRoutineTask().getStartTime();
                    LocalDate date = LocalDate.now();
                    ZonedDateTime zonedDateTime = startTime.atDate(date).atZone(zoneId);
                    LocalTime taskTime = zonedDateTime.toLocalTime();
                    if (taskTime.isAfter(LocalTime.now())) {
                        userRoutineTaskLogRepository.deleteUserRoutineTaskLogByLogId(entry.getId());
                    }
                }
            });
        }
    }

}
