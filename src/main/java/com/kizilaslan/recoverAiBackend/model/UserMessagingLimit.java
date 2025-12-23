package com.kizilaslan.recoverAiBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@Table(name = "user_messaging_limit")
@NoArgsConstructor
public class UserMessagingLimit {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @OneToOne
    private AppUser user;

    private Integer messageCount;

    private Instant limitExpirationDate;

    public UserMessagingLimit(AppUser user, Integer messageCount, Instant limitExpirationDate) {
        this.user = user;
        this.messageCount = messageCount;
        this.limitExpirationDate = limitExpirationDate;
    }
}
