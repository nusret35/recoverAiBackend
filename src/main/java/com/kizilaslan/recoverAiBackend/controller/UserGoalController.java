package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.UserGoalDTO;
import com.kizilaslan.recoverAiBackend.dto.UserGoalProgressLogDTO;
import com.kizilaslan.recoverAiBackend.exception.UserGoalNotFound;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.GoalDetail;
import com.kizilaslan.recoverAiBackend.model.UserGoal;
import com.kizilaslan.recoverAiBackend.model.UserGoalProgressLog;
import com.kizilaslan.recoverAiBackend.request.DeleteUserGoalRequest;
import com.kizilaslan.recoverAiBackend.request.NewGoalRequest;
import com.kizilaslan.recoverAiBackend.request.UserGoalAddProgressRequest;
import com.kizilaslan.recoverAiBackend.response.AiCommentResponse;
import com.kizilaslan.recoverAiBackend.response.GoalDetailResponse;
import com.kizilaslan.recoverAiBackend.service.AiService;
import com.kizilaslan.recoverAiBackend.service.SectionCommentService;
import com.kizilaslan.recoverAiBackend.service.UserGoalService;
import com.kizilaslan.recoverAiBackend.util.ExtractUtils;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/user-goal")
@RequiredArgsConstructor
public class UserGoalController {

    private final UserGoalService userGoalService;
    private final SectionCommentService sectionCommentService;
    private final ModelMapper modelMapper;
    private final AiService aiService;

    @GetMapping
    public ResponseEntity<UserGoalDTO> getUserGoal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserGoal> userGoal = userGoalService.getUserGoal(user.getId());
        if (userGoal.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        UserGoalDTO userGoalDTO = modelMapper.map(userGoal, UserGoalDTO.class);
        Float progressInPercentFloat = (userGoal.get().getProgress() / userGoal.get().getGoal() * 100);
        userGoalDTO.setProgressInPercent(progressInPercentFloat);
        Float remainingProgress = userGoal.get().getGoal() - userGoal.get().getProgress();
        userGoalDTO.setRemainingProgress(remainingProgress);
        return ResponseEntity.ok(userGoalDTO);
    }

    @PostMapping("/create")
    public ResponseEntity<UserGoal> create(@RequestBody UserGoalDTO userGoalDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        userGoalDTO.setUserId(user.getId());
        UserGoal userGoal = modelMapper.map(userGoalDTO, UserGoal.class);
        UserGoal createdGoal = userGoalService.create(userGoal);
        return ResponseEntity.ok(createdGoal);
    }

    @GetMapping("/completed-count")
    public ResponseEntity<Long> count() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Long count = userGoalService.getCompletedCountByUserId(user.getId());
        return ResponseEntity.ok(count);

    }

    @PostMapping("/add-progress")
    public ResponseEntity<UserGoalDTO> addProgress(@RequestBody UserGoalAddProgressRequest userGoalAddProgressRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserGoal> userGoal = userGoalService.getUserGoal(user.getId());
        if (userGoal.isEmpty()) {
            throw new UserGoalNotFound("User goal not found");
        }
        UserGoalProgressLog userGoalProgressLog = new UserGoalProgressLog(userGoal.get(),
                userGoalAddProgressRequest.getProgress());
        Float newProgress = userGoal.get().getProgress() + userGoalAddProgressRequest.getProgress();
        userGoal.get().setProgress(newProgress);
        if (newProgress >= userGoal.get().getGoal()) {
            userGoal.get().setIsCompleted(true);
        }
        userGoalService.update(userGoal.get());
        userGoalService.addGoalProgressLog(userGoalProgressLog);
        Float progressInPercentFloat = (userGoal.get().getProgress() / userGoal.get().getGoal() * 100);
        UserGoalDTO userGoalDTO = modelMapper.map(userGoal, UserGoalDTO.class);
        userGoalDTO.setProgressInPercent(progressInPercentFloat);
        if (userGoalDTO.getIsCompleted()) {
            String goalSuccessTitle = aiService.getGoalSuccessTitle(user, userGoal.get());
            String goalSuccessDescription = aiService.getGoalSuccessDescription(user, userGoal.get());
            userGoalDTO.setSuccessTitle(goalSuccessTitle);
            userGoalDTO.setSuccessDescription(goalSuccessDescription);
            aiService.deleteGoalComment();
        }
        Float remainingProgress = userGoal.get().getGoal() - userGoal.get().getProgress();
        userGoalDTO.setRemainingProgress(remainingProgress);
        return ResponseEntity.ok(userGoalDTO);
    }

    @GetMapping("/progress-logs/week")
    public ResponseEntity<List<UserGoalProgressLogDTO>> getProgressOfTheWeek() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();

        List<UserGoalProgressLog> logs = userGoalService.getUserGoalProgressLogsOfTheWeek(user.getId());

        Map<DayOfWeek, Double> progressByDay = Arrays.stream(DayOfWeek.values())
                .collect(Collectors.toMap(day -> day, day -> 0.0));

        logs.stream()
                .filter(log -> log.getCreatedDate() != null)
                .forEach(log -> {
                    DayOfWeek day = log.getCreatedDate().getDayOfWeek();
                    progressByDay.put(day, progressByDay.get(day) + log.getProgress());
                });

        List<UserGoalProgressLogDTO> dtoList = progressByDay.entrySet().stream()
                .map(entry -> {
                    DayOfWeek day = entry.getKey();
                    Double progress = entry.getValue();
                    LocalDate createdDate = LocalDate.now().with(TemporalAdjusters.previousOrSame(day));
                    return new UserGoalProgressLogDTO(
                            createdDate, progress);
                })
                .sorted(Comparator.comparing(UserGoalProgressLogDTO::getDayOfWeek))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtoList);
    }

    @PostMapping("/delete")
    public ResponseEntity<Void> deleteGoal(@RequestBody DeleteUserGoalRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        userGoalService.deleteUserGoalById(request.getGoalId(), user);
        sectionCommentService.deleteGoalComment();
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/create-new-goal")
    public ResponseEntity<GoalDetailResponse> getGoalDetail(@RequestBody NewGoalRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        String generatedGoalName = aiService.getGoalName(user, request.getGoalName());
        String unit = aiService.getGoalUnit(user, generatedGoalName);
        Float target = ExtractUtils.extractFloatFromString(generatedGoalName);
        GoalDetailResponse goalDetailResponse = new GoalDetailResponse(generatedGoalName, unit, target);
        if (target != null) {
            UserGoal userGoal = new UserGoal(generatedGoalName, target, unit, user);
            userGoalService.create(userGoal);
        }
        return ResponseEntity.ok(goalDetailResponse);
    }

    @PostMapping("/set-new-goal")
    public ResponseEntity<UserGoalDTO> setNewGoal(@RequestBody GoalDetail request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        UserGoal userGoal = new UserGoal(request.getGoalName(), request.getTargetQuantity(), request.getGoalUnit(),
                user);
        userGoalService.create(userGoal);
        UserGoalDTO userGoalDTO = modelMapper.map(userGoal, UserGoalDTO.class);
        return ResponseEntity.ok(userGoalDTO);
    }

    @GetMapping("/goal-comment")
    public ResponseEntity<AiCommentResponse> getRoutineComment() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserGoal> userGoal = userGoalService.getUserGoal(user.getId());
        if (userGoal.isEmpty()) {
            throw new UserGoalNotFound("User goal not found");
        }
        String goalComment = aiService.getGoalComment(user, userGoal.get());
        AiCommentResponse aiCommentResponse = new AiCommentResponse(goalComment);
        return ResponseEntity.ok(aiCommentResponse);
    }

    @GetMapping("/all-goals")
    public ResponseEntity<List<UserGoalDTO>> getAllUserGoals() {
        return ResponseEntity.ok(userGoalService.getAllUserGoals());
    }

}
