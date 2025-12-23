package com.kizilaslan.recoverAiBackend.mcp;

import com.kizilaslan.recoverAiBackend.dto.UserGoalDTO;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserGoal;
import com.kizilaslan.recoverAiBackend.service.UserGoalService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class McpUserGoalService {

    private UserGoalService userGoalService;

    private ModelMapper modelMapper;

    @Tool(name = "get_active_user_goal", description = "Get long term active user goal")
    public UserGoalDTO getActiveUserGoal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        Optional<UserGoal> userGoal = userGoalService.getUserGoal(user.getId());
        return modelMapper.map(userGoal, UserGoalDTO.class);
    }

}
