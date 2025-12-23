package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.SupportTicket;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.repository.SupportTicketRepository;
import com.kizilaslan.recoverAiBackend.request.SupportTicketRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SupportTicketService {

    private final SupportTicketRepository supportTicketRepository;

    public SupportTicket save(SupportTicketRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        AppUser user = (AppUser) authentication.getPrincipal();
        SupportTicket supportTicket = new SupportTicket(user, request.getTitle(), request.getDescription());
        return supportTicketRepository.save(supportTicket);
    }

}
