package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "app_config")
public class AppConfig {

    @Id
    private Long id;

    @Column(name = "reset_timer_message_count", nullable = false)
    private Long resetTimerMessageCount;
}
