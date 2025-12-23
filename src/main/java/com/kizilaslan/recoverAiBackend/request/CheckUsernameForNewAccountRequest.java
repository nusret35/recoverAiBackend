package com.kizilaslan.recoverAiBackend.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CheckUsernameForNewAccountRequest {
    private String username;
}
