package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.UserRoutineTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoutineTaskRepository extends JpaRepository<UserRoutineTask, UUID> {

    List<UserRoutineTask> findAllByUserId(UUID userId);

    @Query("SELECT t FROM UserRoutineTask t JOIN UserRoutineTaskDay d ON t.id = d.routineTask.id " +
            "WHERE t.user.id = :userId AND d.dayOfWeek = :dayOfWeek " +
            "AND :currentTime BETWEEN t.startTime AND t.endTime")
    List<UserRoutineTask> findCurrentTasksForUserOnDay(@Param("userId") Long userId,
                                                       @Param("dayOfWeek") DayOfWeek dayOfWeek,
                                                       @Param("currentTime") LocalTime currentTime);


}
