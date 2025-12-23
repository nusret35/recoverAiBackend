package com.kizilaslan.recoverAiBackend.repository;


import com.kizilaslan.recoverAiBackend.model.UserMessagingLimit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserMessagingLimitRepository extends JpaRepository<UserMessagingLimit, UUID> {

    Optional<UserMessagingLimit> findByUserId(UUID userId);

}
