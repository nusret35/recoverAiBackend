package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kizilaslan.recoverAiBackend.model.MessageType;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessageDTO {
    private UUID id;
    private String message;
    private Instant createdAt;
    private MessageType type;

}
