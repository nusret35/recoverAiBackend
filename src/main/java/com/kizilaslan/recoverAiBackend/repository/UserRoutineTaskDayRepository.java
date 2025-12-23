package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.DayOfWeek;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface UserRoutineTaskDayRepository extends JpaRepository<UserRoutineTaskDay, UUID> {

    @Query("SELECT d FROM UserRoutineTaskDay d JOIN UserRoutineTask t ON d.routineTask.id = t.id " +
            "WHERE d.dayOfWeek = :dayOfWeek")
    List<UserRoutineTaskDay> findAllByDayOfWeek(@Param("dayOfWeek") DayOfWeek dayOfWeek);

    @Query("SELECT d FROM UserRoutineTaskDay  d JOIN UserRoutineTask t ON d.routineTask.id = t.id WHERE d.dayOfWeek IN :daysOfWeek")
    List<UserRoutineTaskDay> findAllByDaysOfWeek(@Param("daysOfWeek") Set<DayOfWeek> daysOfWeek);

    @Query("SELECT d FROM UserRoutineTaskDay d JOIN UserRoutineTask t ON d.routineTask.id = t.id " +
            "WHERE d.routineTask.user.id = :userId " +
            "AND d.dayOfWeek = :dayOfWeek " +
            "ORDER BY d.routineTask.endTime ASC ")
    List<UserRoutineTaskDay> findByUserIdAndDayOfWeek(@Param("userId") UUID userId, @Param("dayOfWeek") DayOfWeek dayOfWeek);

    // I'm not sure whether it is able to implement this function automatically. Need to be tested.

    List<UserRoutineTaskDay> findAllByRoutineTaskId(UUID routineTaskId);

    void deleteUserRoutineTaskDayById(UUID routineTaskId);
}
