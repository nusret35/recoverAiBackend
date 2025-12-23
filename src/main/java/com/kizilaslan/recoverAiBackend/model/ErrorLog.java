package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Data
@Table(name = "error_log")
public class ErrorLog {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "description")
    private String description;

    @Column(name = "user_id")
    private UUID userId;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public ErrorLog(UUID userId, String description) {
        this.userId = userId;
        this.description = description;
    }
}
