package com.kizilaslan.recoverAiBackend.model;


import com.fasterxml.jackson.annotation.JsonValue;

public enum DurationUnit {
    MINUTE("Minute"),
    HOUR("Hour"),
    DAY("Day"),
    WEEK("Week"),
    MONTH("Month"),
    YEAR("Year");

    private final String value;

    DurationUnit(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }
}
