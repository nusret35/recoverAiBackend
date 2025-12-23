package com.kizilaslan.recoverAiBackend.request;

import com.kizilaslan.recoverAiBackend.model.Feeling;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyFeelingRequest {
    private LocalDateTime date;
    private Feeling feeling;
}
