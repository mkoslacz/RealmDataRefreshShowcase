package com.mateuszkoslacz.realmdatarefreshshowcase.util;

import android.support.compat.BuildConfig;

/**
 * Created by mateuszkoslacz on 02.09.2016.
 */
public class InterruptEarly {

    public static boolean now(String message) {
        if (BuildConfig.DEBUG) throw new RuntimeException(message);
        return true;
    }

    public static boolean now(String message, Throwable e) {
        if (BuildConfig.DEBUG) throw new RuntimeException(message, e);
        return true;
    }
}
