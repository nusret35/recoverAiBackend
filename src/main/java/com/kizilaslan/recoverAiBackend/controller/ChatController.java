package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.dto.AssistantMessageDTO;
import com.kizilaslan.recoverAiBackend.dto.ChatMessageDTO;
import com.kizilaslan.recoverAiBackend.dto.UserMessageDTO;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.repository.ChatRepository;
import com.kizilaslan.recoverAiBackend.repository.UserMessagingLimitRepository;
import com.kizilaslan.recoverAiBackend.request.AddictionRequest;
import com.kizilaslan.recoverAiBackend.request.DailyFeelingRequest;
import com.kizilaslan.recoverAiBackend.response.MessagesResponse;
import com.kizilaslan.recoverAiBackend.service.AiService;
import com.kizilaslan.recoverAiBackend.service.ChatService;
import com.kizilaslan.recoverAiBackend.service.UserAddictionService;
import com.kizilaslan.recoverAiBackend.service.UserService;
import com.kizilaslan.recoverAiBackend.util.TimeUtils;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@AllArgsConstructor
@RequestMapping("/ai/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatRepository chatMessageRepository;
    private final AiService aiService;
    private final UserAddictionService userAddictionService;
    private final UserService userService;
    private final UserMessagingLimitRepository userMessagingLimitRepository;

    private Boolean isReachedLimit(AppUser user) {
        Optional<UserMessagingLimit> userMessagingLimit = userMessagingLimitRepository.findByUserId(user.getId());
        UserMessagingLimit limit;
        if (userMessagingLimit.isPresent()) {
            limit = userMessagingLimit.get();
            if (limit.getLimitExpirationDate() != null && limit.getLimitExpirationDate().isAfter(Instant.now())) {
                return true;
            } else {
                if (limit.getLimitExpirationDate() != null) {
                    limit.setLimitExpirationDate(null);
                    limit.setMessageCount(0);
                }
            }
            limit.setMessageCount(limit.getMessageCount() + 1);
            if (limit.getMessageCount() >= 20) {
                limit.setLimitExpirationDate(Instant.now().plus(Duration.ofDays(1)));
                userMessagingLimitRepository.save(limit);
                return true;
            }
        } else {
            limit = new UserMessagingLimit(user, 1, null);
        }
        userMessagingLimitRepository.save(limit);
        return false;
    }

    @PostMapping("/send-message")
    public ResponseEntity<AssistantMessageDTO> sendMessage(@RequestBody UserMessageDTO userMessageDTO) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            AppUser user = (AppUser) authentication.getPrincipal();
            if (isReachedLimit(user)) {
                return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
            }
            String response = chatService.sendMessage(userMessageDTO.getMessage(), user);
            AssistantMessageDTO aiMessageDTO = new AssistantMessageDTO(response);
            return ResponseEntity.ok(aiMessageDTO);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/messages")
    public ResponseEntity<MessagesResponse> getMessages(
            @RequestParam(value = "before", required = false) String before) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        int pageSize = 20;
        List<ChatMessage> chatMessages;

        if (before == null || before.isEmpty()) {
            chatMessages = chatService.getRecentMessages(user, pageSize);
        } else {
            Instant beforeInstant = Instant.parse(before);
            chatMessages = chatService.getMessagesBefore(user, beforeInstant, pageSize);
        }

        List<ChatMessageDTO> chatMessageDTOs = chatMessages.stream()
                .map(ChatMessage::toDTO)
                .toList();

        String lastMessageDate = chatMessages.isEmpty()
                ? null
                : chatMessages.get(chatMessages.size() - 1).getCreatedAt().toString();

        MessagesResponse response = MessagesResponse.builder()
                .messages(chatMessageDTOs)
                .lastMessageDate(lastMessageDate)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/chat-about-to-relapse")
    public ResponseEntity<AssistantMessageDTO> aboutToRelapse(@RequestBody AddictionRequest addictionRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        if (isReachedLimit(user)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        UserAddiction userAddiction = userAddictionService.findById(user.getId(), addictionRequest.getAddictionId());
        String duration = TimeUtils.getDurationString(userAddiction.getLastRelapseDate(), LocalDateTime.now());
        String response = chatService.getAvoidRelapseMessage(user, userAddiction.getAddiction().toString(), duration);
        AssistantMessageDTO aiMessageDTO = new AssistantMessageDTO(response);
        return ResponseEntity.ok(aiMessageDTO);
    }

    @PostMapping("/chat-feeling")
    public ResponseEntity<AssistantMessageDTO> feeling(@RequestBody DailyFeelingRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        if (isReachedLimit(user)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).build();
        }
        String response = chatService.getFeelingMessage(user, request.getFeeling());
        AssistantMessageDTO aiMessageDTO = new AssistantMessageDTO(response);
        return ResponseEntity.ok(aiMessageDTO);
    }

    @PostMapping("/greet-user")
    public ResponseEntity<AssistantMessageDTO> greetUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        String response = aiService.getGreetingMessage(user);
        user.setIsChattingFirstTime(false);
        userService.update(user);
        AssistantMessageDTO aiMessageDTO = new AssistantMessageDTO(response);
        AssistantChatMessage newAiMessage = new AssistantChatMessage(response, user);
        chatMessageRepository.save(newAiMessage);
        return ResponseEntity.ok(aiMessageDTO);
    }

    @GetMapping("/is-limit-reached")
    public ResponseEntity<Boolean> getIsLimitReached() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserMessagingLimit> userMessagingLimit = userMessagingLimitRepository.findByUserId(user.getId());
        if (userMessagingLimit.isPresent()) {
            UserMessagingLimit limit = userMessagingLimit.get();
            if (limit.getLimitExpirationDate() != null && limit.getLimitExpirationDate().isAfter(Instant.now())) {
                return ResponseEntity.ok(true);
            }
        }
        return ResponseEntity.ok(false);
    }

}
