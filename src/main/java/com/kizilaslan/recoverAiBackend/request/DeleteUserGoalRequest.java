package com.kizilaslan.recoverAiBackend.request;

import lombok.Data;

import java.util.UUID;

@Data
public class DeleteUserGoalRequest {
    private UUID goalId;
}
