package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.kizilaslan.recoverAiBackend.model.ProgressUnitType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGoalDTO {

    private UUID id;
    private String name;
    @JsonIgnore
    private UUID userId;
    private Float progress;
    private Float goal;
    private Float progressInPercent;
    private Float remainingProgress;
    private Boolean isCompleted;
    private ProgressUnitType progressUnitType;
    private String progressUnit;
    private LocalDateTime createdDate;
    private String successTitle;
    private String successDescription;


}
