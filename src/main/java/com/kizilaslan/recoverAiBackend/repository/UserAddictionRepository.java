package com.kizilaslan.recoverAiBackend.repository;


import com.kizilaslan.recoverAiBackend.model.UserAddiction;
import com.kizilaslan.recoverAiBackend.model.UserAddictionId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAddictionRepository extends JpaRepository<UserAddiction, UserAddictionId> {

    List<UserAddiction> findAllByUserId(UUID userId);

    Optional<UserAddiction> findById(UserAddictionId id);

    boolean existsById(UserAddictionId id);
    
}
