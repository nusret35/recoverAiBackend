package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.SobrietyAchievementDTO;
import com.kizilaslan.recoverAiBackend.dto.UserAddictionDTO;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.repository.SobrietyAchievementRepository;
import com.kizilaslan.recoverAiBackend.request.AddAddictionRequest;
import com.kizilaslan.recoverAiBackend.request.DeleteUserAddictionRequest;
import com.kizilaslan.recoverAiBackend.request.ResetAddictionTimerRequest;
import com.kizilaslan.recoverAiBackend.response.ResetTimerResponse;
import com.kizilaslan.recoverAiBackend.scheduler.ScheduledAddictionService;
import com.kizilaslan.recoverAiBackend.service.AddictionService;
import com.kizilaslan.recoverAiBackend.service.UserAddictionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user-addiction")
public class UserAddictionController {
    private final ModelMapper modelMapper;
    private final UserAddictionService userAddictionService;
    private final ScheduledAddictionService userAddictionScheduledService;
    private final SobrietyAchievementRepository sobrietyAchievementRepository;
    private final AddictionService addictionService;

    @GetMapping("/")
    public ResponseEntity<List<UserAddictionDTO>> getUserAddictions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        ZoneId zoneId = user.getTimezone();
        List<UserAddiction> userAddictions = userAddictionService.findAllByUserId(user.getId());
        userAddictions = userAddictions.stream()
                .peek(userAddiction -> {
                    LocalDateTime localDateTime = userAddiction.getLastRelapseDate();
                    ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(zoneId);
                    userAddiction.setLastRelapseDate(zonedDateTime.toLocalDateTime());
                })
                .collect(Collectors.toList());
        List<UserAddictionDTO> addictionDTOList = Arrays
                .asList(modelMapper.map(userAddictions, UserAddictionDTO[].class));
        return ResponseEntity.ok(addictionDTOList);
    }

    @Transactional
    @PostMapping("/create")
    public ResponseEntity<UserAddictionDTO> createUserAddiction(@RequestBody AddAddictionRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserAddiction userAddictionInfo = new UserAddiction();
        SobrietyAchievement firstAchievement = sobrietyAchievementRepository.getFirstMinuteAchievement();
        Addiction addiction = addictionService.findById(request.getAddictionId());
        UserAddictionId id = new UserAddictionId(user.getId(), request.getAddictionId());
        userAddictionInfo.setId(id);
        userAddictionInfo.setStartDate(LocalDateTime.now());
        userAddictionInfo.setAddiction(addiction);
        userAddictionInfo.setUser(user);
        // TODO: set the achievements accordingly
        LocalDateTime dateWithCurrentTime = request.getLastRelapseDate().atTime(LocalTime.now());
        userAddictionInfo.setLastRelapseDate(dateWithCurrentTime);
        userAddictionInfo.setNextAchievement(firstAchievement);
        UserAddiction userAddiction = userAddictionService.create(userAddictionInfo);
        userAddictionScheduledService.scheduleAddictionAchievementTask(userAddiction);
        return ResponseEntity.status(HttpStatus.CREATED).body(modelMapper.map(userAddiction, UserAddictionDTO.class));
    }

    @GetMapping("/{addictionId}/achievements")
    public ResponseEntity<List<SobrietyAchievementDTO>> getAchievements(@PathVariable("addictionId") UUID addictionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserAddiction userAddiction = userAddictionService.findById(user.getId(), addictionId);
        List<SobrietyAchievement> sobrietyAchievements = userAddiction.getAchievements().stream()
                .sorted((a1, a2) -> {
                    int typeComparison = a1.getDurationType().compareTo(a2.getDurationType());
                    if (typeComparison != 0) {
                        return typeComparison;
                    }
                    return Integer.compare(a1.getDuration(), a2.getDuration());
                })
                .collect(Collectors.toList());
        List<SobrietyAchievementDTO> sobrietyAchievementDTOList = Arrays
                .asList(modelMapper.map(sobrietyAchievements, SobrietyAchievementDTO[].class));
        return ResponseEntity.ok(sobrietyAchievementDTOList);
    }

    @GetMapping("/{addictionId}")
    public ResponseEntity<UserAddictionDTO> getUserAddiction(@PathVariable("addictionId") UUID addictionId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserAddiction userAddiction;
        userAddiction = userAddictionService.findById(user.getId(), addictionId);
        boolean isUserPassedAchievement = userAddictionService.checkUserAddictionPassedAchievementBySeconds(userAddiction, 5);
        if (isUserPassedAchievement) {
            userAddiction = userAddictionService.updateSobrietyAchievement(user, userAddiction);
        }
        List<SobrietyAchievement> sobrietyAchievements = userAddiction.getAchievements().stream()
                .sorted((a1, a2) -> {
                    int typeComparison = a1.getDurationType().compareTo(a2.getDurationType());
                    if (typeComparison != 0) {
                        return typeComparison;
                    }
                    return Integer.compare(a1.getDuration(), a2.getDuration());
                })
                .collect(Collectors.toList());
        userAddiction.setAchievements(sobrietyAchievements);
        UserAddictionDTO userAddictionDTO = modelMapper.map(userAddiction, UserAddictionDTO.class);
        LocalDateTime localDateTime = userAddictionDTO.getLastRelapseDate();
        ZonedDateTime zonedDateTime = localDateTime.atZone(ZoneOffset.UTC).withZoneSameInstant(user.getTimezone());
        userAddictionDTO.setLastRelapseDate(zonedDateTime.toLocalDateTime());
        return ResponseEntity.ok(userAddictionDTO);
    }

    @PostMapping("/delete")
    public ResponseEntity<?> deleteUserAddiction(@RequestBody DeleteUserAddictionRequest request) {
        userAddictionService.deleteById(request.getAddictionId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/reset-timer")
    public ResponseEntity<ResetTimerResponse> resetTimer(@RequestBody ResetAddictionTimerRequest request) {
        ResetTimerResponse response = userAddictionService.resetTimer(request.getAddictionId());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all-addictions")
    public ResponseEntity<List<Addiction>> getAllUserAddictions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserAddiction> allUserAddictions = userAddictionService.findAllByUserId(user.getId());
        List<Addiction> addictions = addictionService.getAddictionsCanBeAdded(
                allUserAddictions.stream().map(UserAddiction::getAddiction).collect(Collectors.toList()));
        return ResponseEntity.ok(addictions);
    }

}
