package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Language {
    ENGLISH("en"),
    TURKISH("tr"),
    SPANISH("es"),
    FRENCH("fr"),
    GERMAN("de"),
    PORTUGUESE("pt"),
    JAPANESE("ja"),
    RUSSIAN("ru");

    private final String value;

    Language(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
