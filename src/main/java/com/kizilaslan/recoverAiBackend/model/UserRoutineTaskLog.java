package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_routine_task_log")
@NoArgsConstructor
@AllArgsConstructor
public class UserRoutineTaskLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "routine_day_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private UserRoutineTaskDay routineDay;

    @Enumerated(EnumType.STRING)
    private TaskStatus taskStatus = TaskStatus.NEUTRAL;

    private LocalDate routineDate;

    @CreationTimestamp
    private LocalDateTime createdDate;

    public UserRoutineTaskLog(UserRoutineTaskDay routineTaskDay, TaskStatus taskStatus, LocalDate routineDate) {
        this.routineDay = routineTaskDay;
        this.taskStatus = taskStatus;
        this.routineDate = routineDate;
    }
}
