package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<AppUser, UUID> {

    boolean existsByEmail(String email);

    boolean existsByPhoneNumber(String phoneNumber);

    AppUser findByEmail(String email);

    AppUser findByPhoneNumber(String phoneNumber);

    AppUser findByAppleId(String appleId);

}
