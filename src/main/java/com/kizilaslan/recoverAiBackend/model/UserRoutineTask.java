package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.LocalTime;
import java.time.ZoneId;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_routine_task")
@NoArgsConstructor
public class UserRoutineTask {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @JsonIgnore
    @ManyToOne(cascade = CascadeType.MERGE)
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    private String taskName;

    private String emoji;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm")
    private LocalTime endTime;

    private ZoneId zoneId;

    public UserRoutineTask(AppUser user, String taskName, String emoji, LocalTime startTime, LocalTime endTime,
            ZoneId zoneId) {
        this.user = user;
        this.taskName = taskName;
        this.emoji = emoji;
        this.startTime = startTime;
        this.endTime = endTime;
        this.zoneId = zoneId;
    }

}
