package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.UUID;

@Entity
@Data
@Table(name = "sobriety_achievement")
public class SobrietyAchievement {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    private Integer duration;

    @Enumerated(EnumType.STRING)
    private DurationUnit durationType;

    @Column
    private Integer minuteDuration;

    private UUID nextAchievementId;
}
