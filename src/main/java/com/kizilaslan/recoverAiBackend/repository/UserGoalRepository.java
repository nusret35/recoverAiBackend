package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserGoalRepository extends JpaRepository<UserGoal, UUID> {

        boolean existsByUserIdAndIsCompleted(UUID userId, boolean isCompleted);

        @Query("SELECT g FROM UserGoal g " +
                        "WHERE g.isCompleted = false " +
                        "AND g.user.id = :userId")
        Optional<UserGoal> findByUserId(@Param("userId") UUID userId);

        @Query("SELECT g FROM UserGoal g " +
                        "WHERE g.user.id = :userId " +
                        "ORDER BY g.createdDate ASC ")
        List<UserGoal> findAllByUserId(@Param("userId") UUID userId);

        @Query("SELECT COUNT(g) FROM UserGoal g " +
                        "WHERE g.user.id = :userId " +
                        "AND g.isCompleted = true ")
        Long getCompletedCountByUserId(@Param("userId") UUID userId);

        @Modifying
        Long deleteUserGoalByIdAndUser(UUID id, AppUser user);
}
