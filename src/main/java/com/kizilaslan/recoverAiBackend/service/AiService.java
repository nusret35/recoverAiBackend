package com.kizilaslan.recoverAiBackend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kizilaslan.recoverAiBackend.config.SystemPrompt;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.response.SobrietyAchievementNotificationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.MapOutputConverter;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class AiService {
    private final ChatClient.Builder chatClientBuilder;
    private final ChatModel chatModel;
    private final UserRoutineService userRoutineService;
    private final UserGoalService userGoalService;
    private final SectionCommentService sectionCommentService;

    public String getRoutineTaskEmoji(String routineTaskName) {
        ChatClient chatClientForRelapse = chatClientBuilder.defaultSystem(SystemPrompt.getRoutineTaskEmojiPrompt())
                .build();
        return chatClientForRelapse.prompt().user(routineTaskName).call().content();
    }

    public String getGoalSuccessTitle(AppUser user, UserGoal goal) {
        ChatClient chatClient = chatClientBuilder.defaultSystem(SystemPrompt.getGoalSuccessTitlePrompt(goal)).build();
        return chatClient.prompt().user("Please answer in " + user.getLanguage()).call().content();
    }

    public String getGoalSuccessDescription(AppUser user, UserGoal goal) {
        ChatClient chatClient = chatClientBuilder.defaultSystem(SystemPrompt.getGoalSuccessDescriptionPrompt(goal))
                .build();
        return chatClient.prompt().user("Please answer in " + user.getLanguage()).call().content();
    }

    public String getGoalName(AppUser user, String goalName) {
        ChatClient chatClient = chatClientBuilder
                .defaultSystem(SystemPrompt.getGoalFormatterPrompt(user.getLanguage().name(), goalName)).build();
        return chatClient.prompt().user("Please answer in " + user.getLanguage()).call().content();
    }

    public String getGoalUnit(AppUser user, String goalName) {
        PromptTemplate promptTemplate = new PromptTemplate(
                SystemPrompt.getGoalUnitPrompt(user.getLanguage().name(), goalName));
        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
    }

    public String getRoutineComment(AppUser user) {
        Optional<SectionComment> sectionCommentOptional = sectionCommentService.getBySection(Section.ROUTINE);
        if (sectionCommentOptional.isPresent()) {
            SectionComment existingRoutineComment = sectionCommentOptional.get();
            LocalDateTime updatedTime = existingRoutineComment.getUpdatedTime();
            LocalDateTime now = LocalDateTime.now();
            long daysBetween = ChronoUnit.DAYS.between(updatedTime, now);
            if (daysBetween < 1) {
                return existingRoutineComment.getComment();
            }
        }
        String userGoalName = "";
        Optional<UserGoal> userGoal = userGoalService.getUserGoal(user.getId());
        Map<DayOfWeek, List<UserRoutineTaskDay>> mappedOutUserRoutine = userRoutineService
                .getMappedOutUserRoutineTaskDays(user.getId());
        LocalDate today = LocalDate.now();
        if (userGoal.isPresent()) {
            userGoalName = userGoal.get().getName();
        }
        LocalDate lastWeekStart = today.minusWeeks(1).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate lastWeekEnd = lastWeekStart.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        List<UserRoutineTaskLog> userRoutineTaskLogs = userRoutineService.findLogsBetweenDate(user.getId(),
                lastWeekStart, lastWeekEnd);
        if (userRoutineTaskLogs.size() == 0) {
            return null;
        }
        PromptTemplate promptTemplate = new PromptTemplate(SystemPrompt.getRoutineComment(user.getLanguage().name(),
                userGoalName, mappedOutUserRoutine, userRoutineTaskLogs));
        String aiResponse = chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
        SectionComment sectionComment = new SectionComment(user, aiResponse, Section.ROUTINE);
        sectionCommentService.save(sectionComment);
        return aiResponse;
    }

    public String getGoalComment(AppUser user, UserGoal userGoal) {
        Optional<SectionComment> sectionCommentOptional = sectionCommentService.getBySection(Section.GOAL);
        if (sectionCommentOptional.isPresent()) {
            SectionComment existingGoalComment = sectionCommentOptional.get();
            LocalDateTime updatedTime = existingGoalComment.getUpdatedTime();
            long daysBetween = ChronoUnit.DAYS.between(updatedTime.toLocalDate(), LocalDateTime.now());
            if (daysBetween < 1) {
                return existingGoalComment.getComment();
            }
        }
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(3);
        LocalDate endDate = today.minusDays(1);
        List<UserGoalProgressLog> userGoalProgressLogs = userGoalService
                .getUserProgressLogsFromStartToEndDate(user.getId(), startDate, endDate, userGoal.getId());
        PromptTemplate promptTemplate = new PromptTemplate(SystemPrompt.getGoalComment(user.getLanguage().name(),
                userGoal.getCreatedDate().toLocalDate().toString(), userGoal.getName(), userGoal.getGoal().toString(),
                userGoal.getProgressUnit(), userGoal.getProgress().toString(), userGoalProgressLogs));
        String aiResponse = chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
        SectionComment sectionComment = new SectionComment(user, aiResponse, Section.GOAL);
        sectionCommentService.save(sectionComment);
        return aiResponse;
    }

    public void deleteGoalComment() {
        sectionCommentService.deleteGoalComment();
    }

    public String getAchievementNotification(AppUser user, UserAddiction userAddiction,
            SobrietyAchievement achievement) {
        PromptTemplate promptTemplate = new PromptTemplate(SystemPrompt.getSobrietyAchievementNotificationBody(
                user.getLanguage().name(), userAddiction.getAddiction(), achievement));
        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
    }

    public String getRoutineTaskNotification(AppUser user, String taskName) {
        PromptTemplate promptTemplate = new PromptTemplate(
                SystemPrompt.getRoutineTaskNotificationBody(user.getLanguage().name(), taskName));
        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
    }

    public String getRelapsedComment(UserAddiction userAddiction, String duration, String language) {
        PromptTemplate promptTemplate = new PromptTemplate(
                SystemPrompt.getRelapsedPrompt(userAddiction.getAddiction().getName(), duration, language));
        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
    }

    public SobrietyAchievementNotificationResponse getAddictionAchievementNotification(Addiction addiction,
            SobrietyAchievement achievement, AppUser user) {
        MapOutputConverter mapOutputConverter = new MapOutputConverter();
        String duration = achievement.getDuration().toString() + " " + achievement.getDurationType().name();
        String prompt = SystemPrompt.getSobrietyAchievementPrompt(addiction.getName(), duration,
                user.getLanguage().name());
        PromptTemplate promptTemplate = new PromptTemplate(prompt);
        Generation generation = chatModel.call(promptTemplate.create()).getResult();
        Map<String, Object> result = mapOutputConverter.convert(generation.getOutput().getText());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.convertValue(result, SobrietyAchievementNotificationResponse.class);
    }

    public String getGreetingMessage(AppUser user) {
        PromptTemplate promptTemplate = new PromptTemplate(
                SystemPrompt.getGreetingPrompt(user.getLanguage().name(), user.getName()));
        return chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
    }
}
