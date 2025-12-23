package com.kizilaslan.recoverAiBackend.request;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class AddAddictionRequest {
    private UUID addictionId;
    private LocalDate lastRelapseDate;
}
