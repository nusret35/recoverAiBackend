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
@Table(name = "addiction_notification_scheduled_job")
@NoArgsConstructor
public class AddictionNotificationScheduledJob {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    @CreationTimestamp
    private LocalDateTime createdTime;

    private UUID addictionId;

    private String jobId;

    private LocalDateTime runAt;

    public AddictionNotificationScheduledJob(UUID addictionId, AppUser user, String jobId, LocalDateTime runAt) {
        this.addictionId = addictionId;
        this.user = user;
        this.jobId = jobId;
        this.runAt = runAt;
    }

}
