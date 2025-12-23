package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import com.kizilaslan.recoverAiBackend.model.UserDailyFeeling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserDailyFeelingRepository extends JpaRepository<UserDailyFeeling, UUID> {

    Optional<UserDailyFeeling> findByUserAndDate(AppUser user, LocalDate date);

    void deleteByUserAndDate(AppUser user, LocalDate date);
}
