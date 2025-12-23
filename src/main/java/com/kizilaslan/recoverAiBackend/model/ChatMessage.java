package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.kizilaslan.recoverAiBackend.dto.ChatMessageDTO;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.time.Instant;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "message_type")
@Table(name = "chat_messages")
public abstract class ChatMessage {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(nullable = false, length = 4000)
    private String message;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @OnDelete(action = OnDeleteAction.CASCADE)
    private AppUser user;

    @CreationTimestamp
    private Instant createdAt;

    public ChatMessage(String message, AppUser user) {
        this.message = message;
        this.user = user;
    }

    public abstract ChatMessageDTO toDTO();
}
