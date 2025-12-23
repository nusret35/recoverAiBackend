package com.kizilaslan.recoverAiBackend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.kizilaslan.recoverAiBackend.model.TaskStatus;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskDay;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class UserRoutineTaskLogDTO {

    private UUID id;
    private UserRoutineTaskDay routineDay;
    private TaskStatus taskStatus;
    private LocalDate routineDate;
    private LocalDateTime createdDate;
    private Boolean isTomorrow;
}
