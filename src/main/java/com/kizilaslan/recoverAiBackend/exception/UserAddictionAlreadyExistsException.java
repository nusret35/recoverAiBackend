package com.kizilaslan.recoverAiBackend.exception;

public class UserAddictionAlreadyExistsException extends RuntimeException {
    public UserAddictionAlreadyExistsException(String message) {
        super(message);
    }
}
