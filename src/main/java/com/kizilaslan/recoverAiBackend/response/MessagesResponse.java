package com.kizilaslan.recoverAiBackend.response;

import com.kizilaslan.recoverAiBackend.dto.ChatMessageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessagesResponse {
    private List<ChatMessageDTO> messages;
    private String lastMessageDate;
}
