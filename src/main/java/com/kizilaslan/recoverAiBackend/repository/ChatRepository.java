package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.ChatMessage;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.UUID;

@Repository
public interface ChatRepository extends JpaRepository<ChatMessage, UUID> {

    Page<ChatMessage> findByUserOrderByCreatedAtAsc(AppUser user, Pageable pageable);

    Page<ChatMessage> findByUserOrderByCreatedAtDesc(AppUser user, Pageable pageable);

    Page<ChatMessage> findTopByUserOrderByCreatedAtDesc(AppUser user, Pageable pageable);

    Page<ChatMessage> findByUserAndCreatedAtBeforeOrderByCreatedAtDesc(AppUser user, Instant before, Pageable pageable);

}
