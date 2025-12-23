package com.kizilaslan.recoverAiBackend.mcp;

import com.kizilaslan.recoverAiBackend.dto.UserRoutineTaskLogDTO;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserRoutineTaskLog;
import com.kizilaslan.recoverAiBackend.service.UserRoutineService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@AllArgsConstructor
public class McpUserRoutineService {

    private UserRoutineService userRoutineService;

    private ModelMapper modelMapper;

    @Tool(name = "get_active_user_task", description = "Get current active user routine task")
    public UserRoutineTaskLogDTO getActiveUserRoutineTask() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        LocalDateTime now = LocalDateTime.now();
        Optional<UserRoutineTaskLog> activeTask = userRoutineService.findActiveUserTask(user.getId(), now.toString());
        UserRoutineTaskLogDTO activeTaskLogDTO = activeTask
                .map(userRoutineTaskLog -> modelMapper.map(activeTask, UserRoutineTaskLogDTO.class)).orElse(null);
        if (activeTaskLogDTO == null) {
            return null;
        }
        LocalDate today = LocalDate.now();
        if (!activeTaskLogDTO.getRoutineDate().isEqual(today)) {
            activeTaskLogDTO.setIsTomorrow(true);
        }
        return activeTaskLogDTO;
    }

}
