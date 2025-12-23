package com.kizilaslan.recoverAiBackend.util;

public class ValidationUtils {

    private ValidationUtils() {}

    public static boolean isValidEmail(String identifier) {
        return identifier.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    public static boolean isValidPhoneNumber(String identifier) {
        return identifier.matches("^\\+?[0-9]{10,15}$");
    }
}
