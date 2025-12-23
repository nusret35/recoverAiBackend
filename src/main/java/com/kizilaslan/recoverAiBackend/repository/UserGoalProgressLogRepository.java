package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.UserGoalProgressLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface UserGoalProgressLogRepository extends JpaRepository<UserGoalProgressLog, UUID> {

    @Query("SELECT l FROM UserGoalProgressLog l " +
            "WHERE l.goal.user.id = :userId AND " +
            "l.createdDate BETWEEN :startDate " +
            "AND :endDate " +
            "AND l.goal.id = :goalId")
    List<UserGoalProgressLog> findUserGoalProgressLogByStartEndDateAndGoal
            (@Param("userId") UUID userId,
             @Param("startDate") LocalDateTime startDate,
             @Param("endDate") LocalDateTime endDate,
             @Param("goalId") UUID goalId);


    @Modifying
    Long deleteAllByGoalId(UUID goalId);
}
