package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Feeling {
    HAPPY("Happy"),
    SAD("Sad"),
    ANGRY("Angry"),
    ANXIOUS("Anxious"),
    STRESSED("Stressed"),
    EXCITED("Excited"),
    TIRED("Tired"),
    CALM("Calm");

    private final String value;

    Feeling(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
