package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.DayOfWeek;
import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserGoalProgressLogDTO {

    private Double progress;
    private DayOfWeek dayOfWeek;
    private LocalDate createdDate;

    public UserGoalProgressLogDTO(LocalDate createdDate, Double progress) {
        this.createdDate = createdDate;
        this.progress = progress;
        this.dayOfWeek = this.createdDate.getDayOfWeek();
    }
}
