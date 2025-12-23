package com.kizilaslan.recoverAiBackend.exception;

public class GoogleSignInFailedException extends RuntimeException {
    public GoogleSignInFailedException(String message) {
        super(message);
    }
}
