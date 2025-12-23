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
@Table(name = "user_notification")
@NoArgsConstructor
public class UserNotification {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    private String title;

    private String body;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    @CreationTimestamp
    private LocalDateTime createdAt;

    private boolean seen;

    public UserNotification(AppUser user, String title, String body) {
        this.title = title;
        this.body = body;
        this.user = user;
        this.seen = false;
    }
}
