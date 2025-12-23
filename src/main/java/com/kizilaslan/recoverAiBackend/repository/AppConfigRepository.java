package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.AppConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface AppConfigRepository extends JpaRepository<AppConfig, Long> {

    @Query("SELECT a FROM AppConfig a")
    AppConfig getAppConfig();
}
