package com.kizilaslan.recoverAiBackend.exception;

public class UserGoalAlreadyExists extends RuntimeException {
    public UserGoalAlreadyExists(String message) {
        super(message);
    }
}
