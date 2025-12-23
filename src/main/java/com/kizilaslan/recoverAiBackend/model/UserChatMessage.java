package com.kizilaslan.recoverAiBackend.model;

import com.kizilaslan.recoverAiBackend.dto.ChatMessageDTO;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@DiscriminatorValue("USER")
public class UserChatMessage extends ChatMessage {

    public UserChatMessage(String message, AppUser user) {
        super(message, user);
    }

    public ChatMessageDTO toDTO() {
        return new ChatMessageDTO(
                super.getId(),
                super.getMessage(),
                super.getCreatedAt(),
                MessageType.USER);
    }

}
