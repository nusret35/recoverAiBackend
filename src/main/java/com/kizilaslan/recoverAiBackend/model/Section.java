package com.kizilaslan.recoverAiBackend.model;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Section {
    GOAL("Goal"),
    ROUTINE("Routine"),
    ADDICTION("Addiction");

    private final String value;

    Section(String value) {
        this.value = value;
    }

    @JsonValue
    @Override
    public String toString() {
        return value;
    }

}
