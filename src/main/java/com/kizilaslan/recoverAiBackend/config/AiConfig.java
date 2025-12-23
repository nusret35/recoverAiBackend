package com.kizilaslan.recoverAiBackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class AiConfig {

    private final List<ToolCallback> toolCallbacks;

    @Bean
    ChatClient chatClient(ChatClient.Builder builder) {
        return builder.defaultSystem(SystemPrompt.getDefaultPrompt()).defaultTools(toolCallbacks).build();
    }
}
