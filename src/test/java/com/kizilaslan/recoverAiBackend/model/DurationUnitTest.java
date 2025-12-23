package com.kizilaslan.recoverAiBackend.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class DurationUnitTest {

    @Test
    public void testDurationUnitComparison() {
        // Test that enum values are ordered from smallest to largest duration
        assertTrue(DurationUnit.MINUTE.compareTo(DurationUnit.HOUR) < 0);
        assertTrue(DurationUnit.HOUR.compareTo(DurationUnit.DAY) < 0);
        assertTrue(DurationUnit.DAY.compareTo(DurationUnit.WEEK) < 0);
        assertTrue(DurationUnit.WEEK.compareTo(DurationUnit.MONTH) < 0);
        assertTrue(DurationUnit.MONTH.compareTo(DurationUnit.YEAR) < 0);
    }

    @Test
    public void testDurationUnitEqualComparison() {
        // Test that same enum values are equal
        assertEquals(0, DurationUnit.DAY.compareTo(DurationUnit.DAY));
        assertEquals(0, DurationUnit.YEAR.compareTo(DurationUnit.YEAR));
    }

    @Test
    public void testDurationUnitReverseComparison() {
        // Test reverse comparison (larger to smaller)
        assertTrue(DurationUnit.YEAR.compareTo(DurationUnit.MONTH) > 0);
        assertTrue(DurationUnit.MONTH.compareTo(DurationUnit.WEEK) > 0);
        assertTrue(DurationUnit.WEEK.compareTo(DurationUnit.DAY) > 0);
        assertTrue(DurationUnit.DAY.compareTo(DurationUnit.HOUR) > 0);
        assertTrue(DurationUnit.HOUR.compareTo(DurationUnit.MINUTE) > 0);
    }

    @Test
    public void testOrdinalValues() {
        // Test that ordinal values match expected order
        assertEquals(0, DurationUnit.MINUTE.ordinal());
        assertEquals(1, DurationUnit.HOUR.ordinal());
        assertEquals(2, DurationUnit.DAY.ordinal());
        assertEquals(3, DurationUnit.WEEK.ordinal());
        assertEquals(4, DurationUnit.MONTH.ordinal());
        assertEquals(5, DurationUnit.YEAR.ordinal());
    }
}