package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.GoogleAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GoogleAccountRepository extends JpaRepository<GoogleAccount, Long> {

    Optional<GoogleAccount> findById(String id);
}
