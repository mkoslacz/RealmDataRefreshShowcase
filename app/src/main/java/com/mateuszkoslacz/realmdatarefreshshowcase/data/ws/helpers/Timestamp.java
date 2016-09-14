package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers;

import org.joda.time.DateTime;


/**
 * Created by mkoslacz on 22.04.16.
 */
public class Timestamp {

    private static final long dayInSeconds = 60 * 60 * 24;

    public static long getBegginingOfCurrentDay() {
        DateTime now = DateTime.now();
        DateTime dateTime = new DateTime(now.getYear(), now.getMonthOfYear(), now.getDayOfMonth(), 0, 0);
        return dateTime.getMillis() / 1000;
    }

    public static long getDayInSeconds() {
        return dayInSeconds;
    }
}
