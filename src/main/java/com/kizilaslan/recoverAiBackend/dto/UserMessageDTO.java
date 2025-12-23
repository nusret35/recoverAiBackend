package com.kizilaslan.recoverAiBackend.dto;


import lombok.Data;

import java.util.UUID;

@Data
public class UserMessageDTO {
    private UUID id;
    private String message;
}
