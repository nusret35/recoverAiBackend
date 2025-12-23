package com.kizilaslan.recoverAiBackend.mcp;

import com.kizilaslan.recoverAiBackend.dto.UserAddictionDTO;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserAddiction;
import com.kizilaslan.recoverAiBackend.service.UserAddictionService;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@AllArgsConstructor
public class McpUserAddictionService {

    private UserAddictionService userAddictionService;

    private ModelMapper modelMapper;

    @Tool(name = "get_user_bad_habits", description = "Get bad habits of the user that he/she is recovering from")
    public List<UserAddictionDTO> getUserAddictions() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        List<UserAddiction> addictionList = userAddictionService.findAllByUserId(user.getId());
        return Arrays.asList(modelMapper.map(addictionList, UserAddictionDTO[].class));
    }
}
