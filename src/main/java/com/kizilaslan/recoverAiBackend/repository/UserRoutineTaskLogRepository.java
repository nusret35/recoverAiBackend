package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.TaskStatus;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface UserRoutineTaskLogRepository extends JpaRepository<UserRoutineTaskLog, UUID> {

    @Query("SELECT l FROM UserRoutineTaskLog l JOIN UserRoutineTaskDay d ON l.routineDay.id = d.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND CAST(l.createdDate AS date) = :createdDate")
    List<UserRoutineTaskLog> findByUserIdAndCreatedDate(@Param("userId") UUID userId, @Param("createdDate") LocalDate createdDate);

    @Query("SELECT l FROM UserRoutineTaskLog  l JOIN UserRoutineTaskDay d ON l.routineDay.id = d.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND CAST(l.routineDate AS date) = :routineDate " +
            "ORDER BY l.routineDay.routineTask.startTime")
    List<UserRoutineTaskLog> findByUserIdAndRoutineDate(@Param("userId") UUID userId, @Param("routineDate") LocalDate createdDate);

    @Query("SELECT l FROM UserRoutineTaskLog  l JOIN UserRoutineTaskDay d ON l.routineDay.id = d.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND CAST(l.routineDate AS date) = :routineDate " +
            "ORDER BY l.routineDay.routineTask.startTime")
    List<UserRoutineTaskLog> findByUserIdAndRoutineDateAndRoutineDay(@Param("userId") UUID userId, @Param("routineDate") LocalDate createdDate, @Param("routineDayId") UUID routineDayId);

    @Query("SELECT l FROM UserRoutineTaskLog  l JOIN UserRoutineTaskDay d ON l.routineDay.id = d.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND CAST(l.routineDate AS date) = :currentDate " +
            "AND l.routineDay.routineTask.endTime > :currentTime " +
            "ORDER BY l.routineDay.routineTask.endTime ASC " +
            "LIMIT 1")
    Optional<UserRoutineTaskLog> findActiveTaskLogByTime(@Param("userId") UUID userId, @Param("currentDate") LocalDate currentDate, @Param("currentTime") LocalTime currentTime);

    @Query("SELECT l FROM UserRoutineTaskLog  l JOIN UserRoutineTaskDay d ON l.routineDay.id = d.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND CAST(l.routineDate AS date) = :date " +
            "ORDER BY l.routineDay.routineTask.startTime ASC " +
            "LIMIT 1"
    )
    Optional<UserRoutineTaskLog> findFirstTaskLogByDate(@Param("userId") UUID userId, @Param("date") LocalDate date);

    @Modifying
    @Query("UPDATE UserRoutineTaskLog l " +
            "SET l.taskStatus = :taskStatus " +
            "WHERE l.id = :id")
    int updateTaskStatus(@Param("id") UUID id, @Param("taskStatus") TaskStatus taskStatus);

    @Modifying
    @Query("DELETE FROM UserRoutineTaskLog l WHERE l.routineDate >= :date AND l.routineDay.id = :routineDayId")
    void deleteUserRoutineTaskLogByRoutineDateAfterAndRoutineDayId(@Param("date") LocalDate date, @Param("routineDayId") UUID routineDayId);

    @Modifying
    @Query("DELETE FROM UserRoutineTaskLog l WHERE l.id = :logId")
    void deleteUserRoutineTaskLogByLogId(@Param("logId") UUID logId);

    @Query("SELECT l FROM UserRoutineTaskLog l WHERE l.routineDay.routineTask.user.id = :userId AND l.routineDate BETWEEN :startDate AND :endDate")
    List<UserRoutineTaskLog> findUserRoutineTaskLogsBetweenDates(@Param("userId") UUID userId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
    
}
