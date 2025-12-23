package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.UserDTO;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserDailyFeeling;
import com.kizilaslan.recoverAiBackend.request.ChangePasswordRequest;
import com.kizilaslan.recoverAiBackend.request.DailyFeelingRequest;
import com.kizilaslan.recoverAiBackend.response.AuthenticationResponse;
import com.kizilaslan.recoverAiBackend.service.AuthenticationService;
import com.kizilaslan.recoverAiBackend.service.UserDailyFeelingService;
import com.kizilaslan.recoverAiBackend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final ModelMapper modelMapper;
    private final UserService userService;
    private final UserDailyFeelingService userDailyFeelingService;
    private final AuthenticationService authService;

    @GetMapping
    public ResponseEntity<UserDTO> getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserDTO userDTO = modelMapper.map(user, UserDTO.class);
        return ResponseEntity.ok(userDTO);
    }

    @PostMapping("/update-profile")
    public ResponseEntity<UserDTO> updateUserProfile(@RequestBody UserDTO userDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        user.setName(userDTO.getName());
        user.setSurname(userDTO.getSurname());
        user.setGender(userDTO.getGender());
        user.setBirthDate(userDTO.getBirthDate());
        userService.update(user);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/change-password")
    public ResponseEntity<AuthenticationResponse> changePassword(
            @RequestBody ChangePasswordRequest changePasswordRequest) {
        return ResponseEntity.ok(authService.changePassword(changePasswordRequest));
    }

    @PostMapping("/log-daily-feeling")
    public ResponseEntity<Void> logDailyFeeling(@RequestBody DailyFeelingRequest request) {
        Optional<UserDailyFeeling> dailyFeeling = userDailyFeelingService.findByUserIdAndDate(request.getDate());
        if (dailyFeeling.isPresent()) {
            userDailyFeelingService.deleteByUserIdAndDate(request.getDate());
            userDailyFeelingService.create(request.getFeeling(), request.getDate().toLocalDate());
        } else {
            userDailyFeelingService.create(request.getFeeling(), request.getDate().toLocalDate());
        }
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/delete-user")
    private ResponseEntity<Void> deleteUser() {
        userService.delete();
        return ResponseEntity.noContent().build();
    }

}
