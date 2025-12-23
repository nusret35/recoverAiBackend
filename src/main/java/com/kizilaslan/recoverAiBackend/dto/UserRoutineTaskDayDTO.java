package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTask;
import lombok.Data;

import java.time.DayOfWeek;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRoutineTaskDayDTO {
    private UUID id;
    private UserRoutineTask routineTask;
    private DayOfWeek dayOfWeek;
}
