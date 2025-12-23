package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.Addiction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AddictionRepository extends JpaRepository<Addiction, UUID> {


    @Query("SELECT a FROM Addiction a WHERE a.id NOT IN :addictions")
    List<Addiction> getAddictionToBeAdded(@Param("addictions") List<UUID> addictionIds);
}
