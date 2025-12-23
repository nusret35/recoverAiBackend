package com.kizilaslan.recoverAiBackend.util;

import java.time.Duration;
import java.time.LocalDateTime;

public class TimeUtils {

    private TimeUtils() {
    }

    public static String getDurationString(long milliseconds) {
        if (milliseconds < 0) {
            return "0 seconds";
        }

        // Constants for time conversion
        final long secondInMillis = 1000;
        final long minuteInMillis = secondInMillis * 60;
        final long hourInMillis = minuteInMillis * 60;
        final long dayInMillis = hourInMillis * 24;
        final long monthInMillis = dayInMillis * 30; // Approximation
        final long yearInMillis = dayInMillis * 365; // Approximation

        StringBuilder result = new StringBuilder();

        // Calculate years
        long years = milliseconds / yearInMillis;
        if (years > 0) {
            result.append(years).append(years == 1 ? " year " : " years ");
            milliseconds %= yearInMillis;
        }

        // Calculate months
        long months = milliseconds / monthInMillis;
        if (months > 0) {
            result.append(months).append(months == 1 ? " month " : " months ");
            milliseconds %= monthInMillis;
        }

        // Calculate days
        long days = milliseconds / dayInMillis;
        if (days > 0) {
            result.append(days).append(days == 1 ? " day " : " days ");
            milliseconds %= dayInMillis;
        }

        // Calculate hours
        long hours = milliseconds / hourInMillis;
        if (hours > 0) {
            result.append(hours).append(hours == 1 ? " hour " : " hours ");
            milliseconds %= hourInMillis;
        }

        // Calculate minutes
        long minutes = milliseconds / minuteInMillis;
        if (minutes > 0) {
            result.append(minutes).append(minutes == 1 ? " minute " : " minutes ");
            milliseconds %= minuteInMillis;
        }

        // Calculate seconds
        long seconds = milliseconds / secondInMillis;
        if (seconds > 0 || result.isEmpty()) {
            result.append(seconds).append(seconds == 1 ? " second" : " seconds");
        }

        return result.toString().trim();
    }

    /**
     * Overloaded method that takes two Date objects and returns the duration between them
     *
     * @param startDate the start date
     * @param endDate   the end date
     * @return formatted duration string
     */
    public static String getDurationString(LocalDateTime startDate, LocalDateTime endDate) {
        Duration duration = Duration.between(startDate, endDate);

        long milliseconds = duration.toMillis();
        
        return getDurationString(milliseconds);
    }
}
