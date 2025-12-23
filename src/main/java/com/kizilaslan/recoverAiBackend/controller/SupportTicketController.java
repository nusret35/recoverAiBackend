package com.kizilaslan.recoverAiBackend.controller;

import com.kizilaslan.recoverAiBackend.request.SupportTicketRequest;
import com.kizilaslan.recoverAiBackend.service.SupportTicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/support-ticket")
@RequiredArgsConstructor
public class SupportTicketController {

    private final SupportTicketService supportTicketService;

    @PostMapping("/create")
    public ResponseEntity<?> create(@RequestBody SupportTicketRequest supportTicketRequest) {
        supportTicketService.save(supportTicketRequest);
        return ResponseEntity.noContent().build();
    }
}
