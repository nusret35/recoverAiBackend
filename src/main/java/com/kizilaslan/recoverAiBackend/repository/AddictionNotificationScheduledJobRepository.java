package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.AddictionNotificationScheduledJob;
import com.kizilaslan.recoverAiBackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddictionNotificationScheduledJobRepository
        extends JpaRepository<AddictionNotificationScheduledJob, UUID> {

    List<AddictionNotificationScheduledJob> findAllByAddictionIdAndUser(UUID addictionId, AppUser user);

    void deleteAddictionNotificationScheduledJobsByAddictionIdAndUser(UUID addictionId, AppUser user);
}
