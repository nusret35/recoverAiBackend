package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoutineTaskDTO {

    private UUID id;
    @JsonIgnore
    private UUID userId;
    private String taskName;
    private LocalTime startTime;
    private LocalTime endTime;
    private Long timeOffset;
}
