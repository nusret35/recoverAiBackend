package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.SobrietyAchievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface SobrietyAchievementRepository extends JpaRepository<SobrietyAchievement, UUID> {

    @Query("SELECT s FROM SobrietyAchievement s " +
            "WHERE s.duration = 1 AND " +
            "s.durationType = 'MINUTE'")
    SobrietyAchievement getFirstMinuteAchievement();

    @Query("SELECT s FROM SobrietyAchievement s " +
            "ORDER BY s.durationType ASC, s.duration ASC")
    List<SobrietyAchievement> findAllOrderedByDurationUnitAndDurationAsc();

}
