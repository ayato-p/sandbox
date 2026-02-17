package com.uzabase.calendar;

public class Year {
    private final int value;

    public Year(int value) {
        this.value = value;
    }

    public boolean isOlympicYear() {
        if (this.value < 1896) return false;
        return (this.value % 4) == 0;
    }

    public boolean isLeapYear() {
        if ((this.value % 4) != 0) return false;
        if ((this.value % 100) == 0) return true;
        return (this.value % 400) == 0;
    }
}
