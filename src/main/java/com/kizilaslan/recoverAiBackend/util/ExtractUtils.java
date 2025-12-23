package com.kizilaslan.recoverAiBackend.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExtractUtils {

    public static Float extractFloatFromString(String input) {
        // Match numbers with optional thousand separators and optional decimal part
        Pattern pattern = Pattern.compile("[0-9]{1,3}(\\.[0-9]{3})*(,[0-9]+)?|[0-9]+(,[0-9]+)?");
        Matcher matcher = pattern.matcher(input);

        if (matcher.find()) {
            String numberStr = matcher.group();
            // Remove thousand separators (dots) and convert decimal comma to dot
            numberStr = numberStr.replace(".", "").replace(",", ".");
            return Float.parseFloat(numberStr);
        }

        return null;
    }


}
