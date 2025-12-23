package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_routine_task_day")
@NoArgsConstructor
public class UserRoutineTaskDay {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "routine_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private UserRoutineTask routineTask;

    @Enumerated
    private DayOfWeek dayOfWeek;

    public UserRoutineTaskDay(UserRoutineTask routineTask, DayOfWeek dayOfWeek) {
        this.routineTask = routineTask;
        this.dayOfWeek = dayOfWeek;
    }
}
