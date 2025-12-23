package com.kizilaslan.recoverAiBackend.model;

import com.kizilaslan.recoverAiBackend.dto.ChatMessageDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("AI")
public class AssistantChatMessage extends ChatMessage {

    public AssistantChatMessage(String message, AppUser user) {
        super(message, user);
    }

    public ChatMessageDTO toDTO() {
        return new ChatMessageDTO(
                super.getId(),
                super.getMessage(),
                super.getCreatedAt(),
                MessageType.ASSISTANT);
    }

}
