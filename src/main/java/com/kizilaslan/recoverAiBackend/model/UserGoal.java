package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_goal")
@NoArgsConstructor
public class UserGoal {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    private String name;

    private Float progress = (float) 0;

    private Float goal;

    private Boolean isCompleted = false;

    @Enumerated(EnumType.STRING)
    private ProgressUnitType progressUnitType = ProgressUnitType.NUMBER;

    private String progressUnit;

    @CreationTimestamp
    private LocalDateTime createdDate;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    public UserGoal(String name, Float goal, String progressUnit, AppUser user) {
        this.name = name;
        this.goal = goal;
        this.progressUnit = progressUnit;
        this.user = user;
    }

}
