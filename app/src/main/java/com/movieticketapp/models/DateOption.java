package com.movieticketapp.models;

public class DateOption {
    private final String dateKey;
    private final String dayLabel;
    private final String dayNumber;
    private final boolean enabled;

    public DateOption(String dateKey, String dayLabel, String dayNumber, boolean enabled) {
        this.dateKey = dateKey;
        this.dayLabel = dayLabel;
        this.dayNumber = dayNumber;
        this.enabled = enabled;
    }

    public String getDateKey() {
        return dateKey;
    }

    public String getDayLabel() {
        return dayLabel;
    }

    public String getDayNumber() {
        return dayNumber;
    }

    public boolean isEnabled() {
        return enabled;
    }
}
