package com.kizilaslan.recoverAiBackend.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddRoutineTaskRequest {

    private String taskName;

    private LocalTime startTime;

    private LocalTime endTime;

    private int weekDay;

    private Long timeOffset;


}
