package com.kizilaslan.recoverAiBackend.model;

import com.kizilaslan.recoverAiBackend.request.RegisterUserRequest;
import com.kizilaslan.recoverAiBackend.request.RegisterUserWithAppleRequest;
import com.kizilaslan.recoverAiBackend.request.RegisterUserWithGoogleRequest;
import com.kizilaslan.recoverAiBackend.util.ValidationUtils;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Table(name = "app_user")
public class AppUser implements UserDetails {

    @Id
    @GeneratedValue(generator = "UUID")
    @Column(name = "id", nullable = false)
    private UUID id;

    private String name;

    private String surname;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(nullable = true)
    private String email;

    @Column(nullable = true)
    private String phoneNumber;

    private String password;

    @Enumerated(EnumType.STRING)
    private Gender gender = Gender.UNKNOWN;

    @Enumerated(EnumType.STRING)
    private Language language = Language.ENGLISH;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(name = "timezone")
    private ZoneId timezone;

    @Column(name = "notification_device_id")
    private String notificationDeviceId;

    @Column(name = "reset_timer_message_index", nullable = false)
    @ColumnDefault(value = "0")
    private Long resetTimerMessageIndex = 0L;

    @Column(name = "apple_id")
    private String appleId;

    @Column(name = "is_premium")
    private Boolean isPremium = false;

    @Column(name = "is_chatting_first_time")
    private Boolean isChattingFirstTime = true;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public String getUsername() {
        return email != null ? email : phoneNumber;
    }

    @Override
    public String getPassword() {
        return password;
    }

    public static AppUser fromRegisterUserRequest(RegisterUserRequest request) {
        AppUser user = new AppUser();
        user.setRole(Role.USER);
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPassword(request.getPassword());
        if (ValidationUtils.isValidEmail(request.getUsername())) {
            user.setEmail(request.getUsername());
        } else {
            user.setPhoneNumber(request.getUsername());
        }
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        user.setGender(request.getGender());
        user.setLanguage(request.getLanguage());
        user.setTimezone(request.getTimeZone());
        user.setNotificationDeviceId(request.getNotificationDeviceId());
        if (request.getAppleId() != null) {
            user.setAppleId(request.getAppleId());
        }
        return user;

    }

    public static AppUser fromRegisterUserWithGoogleRequest(RegisterUserWithGoogleRequest request) {
        AppUser user = new AppUser();
        user.setRole(Role.USER);
        user.setName(request.getName());
        user.setSurname(request.getSurname());
        user.setPassword(UUID.randomUUID().toString());
        user.setEmail(request.getUsername());
        user.setBirthDate(request.getBirthDate());
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        user.setGender(request.getGender());
        user.setLanguage(request.getLanguage());
        user.setTimezone(request.getTimeZone());
        user.setNotificationDeviceId(request.getNotificationDeviceId());
        return user;
    }

    public static AppUser fromRegisterUserWithAppleRequest(RegisterUserWithAppleRequest request) {
        String appleId = request.getAppleId();
        AppUser user = new AppUser();
        user.setRole(Role.USER);
        user.setName(request.getName());
        user.setEmail(appleId + "@email.com");
        user.setSurname(request.getSurname());
        user.setPassword(UUID.randomUUID().toString());
        if (request.getBirthDate() != null) {
            user.setBirthDate(request.getBirthDate());
        }
        user.setGender(request.getGender());
        user.setLanguage(request.getLanguage());
        user.setTimezone(request.getTimeZone());
        user.setNotificationDeviceId(request.getNotificationDeviceId());
        user.setAppleId(appleId);
        return user;
    }

}