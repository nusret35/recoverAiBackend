package com.kizilaslan.recoverAiBackend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.kizilaslan.recoverAiBackend.model.DurationUnit;
import lombok.Data;

import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SobrietyAchievementDTO {
    private UUID id;
    private Integer duration;
    private DurationUnit durationType;
}
