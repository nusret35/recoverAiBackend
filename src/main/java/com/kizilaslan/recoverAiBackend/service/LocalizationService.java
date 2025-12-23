package com.kizilaslan.recoverAiBackend.service;

import com.kizilaslan.recoverAiBackend.model.Language;
import com.kizilaslan.recoverAiBackend.model.Localization;
import com.kizilaslan.recoverAiBackend.repository.LocalizationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class LocalizationService {

    private final LocalizationRepository localizationRepository;

    public Localization getLocalization(Language language, String localizationKey) {
        return localizationRepository.findByLocalizationKeyAndLocale(localizationKey, language);
    }

    public Localization getResetTimerMessage(Language language, Long index) {
        String localizationKey = "resetTimer-" + index.toString();
        return getLocalization(language, localizationKey);
    }

}
