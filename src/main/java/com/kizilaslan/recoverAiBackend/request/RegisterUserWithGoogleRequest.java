package com.kizilaslan.recoverAiBackend.request;

import com.kizilaslan.recoverAiBackend.model.Gender;
import com.kizilaslan.recoverAiBackend.model.Language;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.ZoneId;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterUserWithGoogleRequest {
    private String name;
    private String surname;
    private String username;
    private LocalDate birthDate;
    private Gender gender;
    private Language language;
    private ZoneId timeZone;
    private String notificationDeviceId;
    private String googleId;
}
