package com.kizilaslan.recoverAiBackend.util;

public class StringUtil {

    /**
     * Capitalizes the first letter of the given string.
     * If the string is null or empty, it returns the same string.
     *
     * @param input the string to capitalize
     * @return the capitalized string
     */
    public static String capitalize(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }
}