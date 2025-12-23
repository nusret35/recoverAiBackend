package com.kizilaslan.recoverAiBackend.request;

import lombok.Data;

@Data
public class SupportTicketRequest {

    private String title;
    private String description;
}
