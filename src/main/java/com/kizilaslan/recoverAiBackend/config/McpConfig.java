package com.kizilaslan.recoverAiBackend.config;

import com.kizilaslan.recoverAiBackend.mcp.McpUserAddictionService;
import com.kizilaslan.recoverAiBackend.mcp.McpUserGoalService;
import com.kizilaslan.recoverAiBackend.mcp.McpUserRoutineService;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbacks;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class McpConfig {

    @Bean
    public List<ToolCallback> toolCallbacks(McpUserGoalService mcpUserGoalService, McpUserRoutineService mcpUserRoutineService, McpUserAddictionService mcpUserAddictionService) {
        return List.of(ToolCallbacks.from(mcpUserGoalService, mcpUserRoutineService, mcpUserAddictionService));
    }
}
