package com.kizilaslan.recoverAiBackend.request;

import java.util.UUID;

import groovy.transform.builder.Builder;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRecoveryAchievementRequest {
    private UUID addictionId;
}
