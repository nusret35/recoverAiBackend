package com.kizilaslan.recoverAiBackend.mapper;

import com.kizilaslan.recoverAiBackend.model.AssistantChatMessage;
import com.kizilaslan.recoverAiBackend.model.ChatMessage;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ChatMessageMapper {

    public List<Message> toAbstractMessages(List<ChatMessage> chatMessages) {
        List<Message> abstractMessages = new ArrayList<>();
        chatMessages.forEach(message -> {
            if (message.getClass() == AssistantChatMessage.class) {
                abstractMessages.add(new AssistantMessage(message.getMessage()));
            } else {
                abstractMessages.add(new UserMessage(message.getMessage()));
            }
        });
        return abstractMessages;
    }
}
