package com.movieticketapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public final class DateTimeUtils {
    private static final String ISO_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final SimpleDateFormat ISO_FORMAT = new SimpleDateFormat(ISO_PATTERN, Locale.getDefault());
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
    private static final SimpleDateFormat DATE_TIME_FORMAT = new SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault());
    private static final SimpleDateFormat TIME_FORMAT = new SimpleDateFormat("HH:mm", Locale.getDefault());
    private static final SimpleDateFormat DATE_KEY_FORMAT = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private static final SimpleDateFormat DAY_LABEL_FORMAT = new SimpleDateFormat("EEE", Locale.getDefault());
    private static final SimpleDateFormat DAY_NUMBER_FORMAT = new SimpleDateFormat("dd", Locale.getDefault());

    static {
        ISO_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private DateTimeUtils() {
    }

    public static String toIsoString(Date date) {
        return ISO_FORMAT.format(date);
    }

    public static Date parseIso(String value) {
        try {
            return ISO_FORMAT.parse(value);
        } catch (ParseException e) {
            return null;
        }
    }

    public static String formatDate(String value) {
        Date date = parseIso(value);
        return date == null ? value : DATE_FORMAT.format(date);
    }

    public static String formatDateTime(String value) {
        Date date = parseIso(value);
        return date == null ? value : DATE_TIME_FORMAT.format(date);
    }

    public static String formatTime(String value) {
        Date date = parseIso(value);
        return date == null ? value : TIME_FORMAT.format(date);
    }

    public static String getFutureIsoDate(int dayOffset, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, dayOffset);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return toIsoString(calendar.getTime());
    }

    public static long getReminderTriggerAtMillis(String isoStartTime) {
        Date startDate = parseIso(isoStartTime);
        long now = System.currentTimeMillis();
        if (startDate == null) {
            return now + 10_000L;
        }
        long reminderTime = startDate.getTime() - (30 * 60 * 1000L);
        if (reminderTime <= now) {
            return now + 10_000L;
        }
        return reminderTime;
    }

    public static String toDateKey(String isoValue) {
        Date date = parseIso(isoValue);
        return date == null ? "" : DATE_KEY_FORMAT.format(date);
    }

    public static String formatDayLabel(String isoValue) {
        Date date = parseIso(isoValue);
        return date == null ? "" : DAY_LABEL_FORMAT.format(date);
    }

    public static String formatDayNumber(String isoValue) {
        Date date = parseIso(isoValue);
        return date == null ? "" : DAY_NUMBER_FORMAT.format(date);
    }

    public static List<Date> getNextSevenDays() {
        List<Date> dates = new java.util.ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        for (int i = 0; i < 7; i++) {
            dates.add(calendar.getTime());
            calendar.add(Calendar.DATE, 1);
        }
        return dates;
    }

    public static String toDateKey(Date date) {
        return DATE_KEY_FORMAT.format(date);
    }

    public static String formatDayLabel(Date date) {
        return DAY_LABEL_FORMAT.format(date);
    }

    public static String formatDayNumber(Date date) {
        return DAY_NUMBER_FORMAT.format(date);
    }
}
