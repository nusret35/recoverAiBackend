package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum MessageType {
    USER("USER"),
    ASSISTANT("ASSISTANT");

    private final String value;

    MessageType(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
