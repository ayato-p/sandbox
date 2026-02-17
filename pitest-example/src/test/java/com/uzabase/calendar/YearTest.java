package com.uzabase.calendar;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class YearTest {
    @Nested
    class IsOlympicYear {
        @Test
        public void _1911はオリンピックYearではない() {
            var year = new Year(1911);
            assertFalse(year.isOlympicYear());
        }
    }

    @Nested
    class IsLeapYear {
        @Test
        public void _2000はうるう年である() {
            var year = new Year(2000);
            assertTrue(year.isLeapYear());
        }
    }
}
