package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.config.SystemPrompt;
import com.kizilaslan.recoverAiBackend.mapper.ChatMessageMapper;
import com.kizilaslan.recoverAiBackend.model.*;
import com.kizilaslan.recoverAiBackend.repository.ChatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ChatService {

    private final ChatClient chatClient;
    private final ChatRepository chatMessageRepository;
    private final ChatMessageMapper chatMessageMapper;
    private final ChatClient.Builder chatClientBuilder;
    private final ChatModel chatModel;

    public Boolean isLimitReached(AppUser user) {
        return false;
    }

    public String sendMessage(String message, AppUser user) {
        UserChatMessage newUserMessage = new UserChatMessage(message, user);
        chatMessageRepository.save(newUserMessage);
        Pageable pageable = PageRequest.of(0, 20, Sort.by("createdAt").ascending());
        List<ChatMessage> contextWindow = getMessages(user, pageable);
        String aiResponse = chatClient.prompt().messages(chatMessageMapper.toAbstractMessages(contextWindow))
                .user(message).call().content();
        AssistantChatMessage newAiMessage = new AssistantChatMessage(aiResponse, user);
        chatMessageRepository.save(newAiMessage);
        return aiResponse;
    }

    public List<ChatMessage> getMessages(AppUser user, Pageable page) {
        return chatMessageRepository.findByUserOrderByCreatedAtDesc(user, page).stream().toList();
    }

    public String getAvoidRelapseMessage(AppUser user, String addiction, String duration) {
        ChatClient chatClientForRelapse = chatClientBuilder
                .defaultSystem(SystemPrompt.getAboutToRelapsePrompt(addiction, duration)).build();
        String aiResponse = chatClientForRelapse.prompt().user("Please answer in " + user.getLanguage()).call()
                .content();
        AssistantChatMessage newAiMessage = new AssistantChatMessage(aiResponse, user);
        chatMessageRepository.save(newAiMessage);
        return aiResponse;
    }

    public List<ChatMessage> getRecentMessages(AppUser user, int limit) {
        return chatMessageRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(0, limit)).stream().toList();
    }

    public List<ChatMessage> getMessagesBefore(AppUser user, Instant before, int limit) {
        return chatMessageRepository
                .findByUserAndCreatedAtBeforeOrderByCreatedAtDesc(user, before, PageRequest.of(0, limit)).stream()
                .toList();
    }

    public String getFeelingMessage(AppUser user, Feeling feeling) {
        PromptTemplate promptTemplate = new PromptTemplate(SystemPrompt.getFeelingPrompt(feeling, user.getLanguage()));
        String aiResponse = chatModel.call(promptTemplate.create()).getResult().getOutput().getText();
        AssistantChatMessage newAiMessage = new AssistantChatMessage(aiResponse, user);
        chatMessageRepository.save(newAiMessage);
        return aiResponse;
    }

}
