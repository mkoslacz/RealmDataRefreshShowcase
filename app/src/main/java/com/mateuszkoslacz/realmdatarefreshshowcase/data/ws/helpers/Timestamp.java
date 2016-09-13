package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers;

import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.LocalDate;


/**
 * Created by mkoslacz on 22.04.16.
 */
public class Timestamp {

    private static final String TAG = "Timestamp";

    private static final long minuteInSeconds = 60;
    private static final long hourInSeconds = 60 * 60;
    private static final long dayInSeconds = 60 * 60 * 24;
    private static final long dayInMiliseconds = dayInSeconds * 1000;
    private static final long weekInMilliseconds = 60 * 60 * 24 * 7 * 1000;

    private static final long dayEndTimeShiftInSeconds = 0;// 60 * 60 * 4;

    public static long getNow() {
        return System.currentTimeMillis() / 1000;
    }

    public static long getMinuteInSeconds() {
        return minuteInSeconds;
    }

    public static long getHourInSeconds() {
        return hourInSeconds;
    }

    public static long getDayInSeconds() {
        return dayInSeconds;
    }

    public static long getEndOfSyncPeriod() {
        return (DateTime.now().getMillis() + weekInMilliseconds) / 1000;
    }

    public static long getBegginingOfCurrentDay() {
        DateTime now = DateTime.now();
        DateTime dateTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        return dateTime.getMillis() / 1000;
    }

    public static long getBeginningOfGivenDay(int day) {
        DateTime now = DateTime.now().plus(day * dayInMiliseconds);
        DateTime dateTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        return dateTime.getMillis() / 1000;
    }


    public static long getBeginningOfGivenDay(long timestamp) {
        DateTime now = new DateTime(timestamp * 1000);
        DateTime dateTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        return dateTime.getMillis() / 1000;
    }

    public static long getWeekInMillis() {
        return weekInMilliseconds;
    }

    public static long getDayEndTimeShiftInSeconds() {
        return dayEndTimeShiftInSeconds;
    }

    public static long calculateTimeOneWeekAheadForGivenPeriod(long timestampSeconds) {
        return timestampSeconds + weekInMilliseconds / 1000;
    }

    //TODO change usages of this method to getCustomBoundsDayNumber to achieve our custom day bounds
    public static int getDayNumber(long timestampSeconds) {
        LocalDate date = new LocalDate(timestampSeconds * 1000);
        LocalDate dateNow = new LocalDate();
        Days days = Days.daysBetween(dateNow, date);
        return days.getDays();
    }

    public static String getHourString(long timestampSeconds) {
        String date = Timestamp.getTimeFromTimestamp(DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour());
        return date;
    }

    public static String getMinutesString(long timestampSeconds) {
        long minutes = timestampSeconds / 60;
        return String.format("%d min", minutes);
    }

    public static String getTimeFromTimestamp(long timestampSeconds) {
        DateTime dateTime = new DateTime(timestampSeconds * 1000);
        return getTimeFromTimestamp(dateTime.getHourOfDay(), dateTime.getMinuteOfHour());
    }

    public static String getTimeFromTimestamp(int hour, int minute) {
        return String.format("%02d:%02d", hour, minute);
    }

    public static String translateSecondsToString(long timestampSeconds) {
        double hours = Math.floor((double) timestampSeconds / 60 / 60);
        double minutes = Math.floor((double) timestampSeconds / 60);
        if (hours > 0) {
            return hours + " godz.";
        } else {
            return minutes + " min.";
        }
    }

    public static long calculateTimestampFor8PM() {
        DateTime now = DateTime.now();
        DateTime dateTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 20, 0);
        return dateTime.getMillis() / 1000;
    }

    public static int getHour(long timestampSeconds) {
        DateTime dateTime = new DateTime(timestampSeconds * 1000);
        return dateTime.getHourOfDay();
    }

    public static int getMinutes(long timestampSeconds) {
        DateTime dateTime = new DateTime(timestampSeconds * 1000);
        return dateTime.getMinuteOfHour();
    }

    public static int getDiffBetweenNowAndTimestamp(long timestamp) {
        DateTime dateTime = new DateTime(timestamp);
        DateTime dateTimeNow = DateTime.now();

        long diff = Math.abs(dateTime.getMillis() - dateTimeNow.getMillis());
        return (int) (diff / (60 * 1000));
    }
}
