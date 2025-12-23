package com.kizilaslan.recoverAiBackend.model;

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
@Table(name = "user_goal_progress_log")
@NoArgsConstructor
public class UserGoalProgressLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserGoal goal;

    private Float progress;

    @CreationTimestamp
    private LocalDateTime createdDate;

    public UserGoalProgressLog(UserGoal goal, Float progress) {
        this.goal = goal;
        this.progress = progress;
    }

}
