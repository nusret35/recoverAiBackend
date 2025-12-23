package com.kizilaslan.recoverAiBackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddictionDTO {
    @JsonIgnore
    private UUID userId;
    private UUID addictionId;
    private String addictionName;
    private LocalDateTime startDate;
    private LocalDateTime lastRelapseDate;
    private SobrietyAchievementDTO nextAchievement;
    private List<SobrietyAchievementDTO> achievements;
    private String aiNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
