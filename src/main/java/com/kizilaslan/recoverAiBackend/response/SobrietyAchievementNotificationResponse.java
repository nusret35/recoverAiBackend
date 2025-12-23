package com.kizilaslan.recoverAiBackend.response;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SobrietyAchievementNotificationResponse {
    private String title;
    private String body;
}
