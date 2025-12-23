package com.kizilaslan.recoverAiBackend.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class GoalDetailResponse {
    private String goalName;
    private String goalUnit;
    private Float targetQuantity;
}
