package com.kizilaslan.recoverAiBackend.repository;

import com.kizilaslan.recoverAiBackend.model.Language;
import com.kizilaslan.recoverAiBackend.model.Localization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface LocalizationRepository extends JpaRepository<Localization, UUID> {

    Localization findByLocalizationKeyAndLocale(String localizationKey, Language locale);

}
