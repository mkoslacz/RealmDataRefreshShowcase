package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Log;

import com.mateuszkoslacz.realmdatarefreshshowcase.util.InterruptEarly;

import java.util.Random;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 18.05.16.
 */
public class Reminder extends RealmObject {

    public static final String C_ID_INT = "id";
    public static final String C_PROGRAM_PROGRAM = "program";
    public static final String C_TIMEAHEAD_LONG = "timeAhead";
    public static final String C_REPEATING_BOOLEAN = "repeating";
    public static final String C_STARTTIME_LONG = "startTime";
    public static final long BEFORE_5_MIN_SECONDS = 5 * 60;
    public static final long BEFORE_15_MIN_SECONDS = 15 * 60;
    public static final long BEFORE_30_MIN_SECONDS = 30 * 60;
    public static final long BEFORE_1_HOUR_SECONDS = 1 * 60 * 60;
    private static final String TAG = "Reminder";
    @PrimaryKey
    private int id;
    private Program program;
    private long timeAhead;
    private long startTime;
    private boolean repeating;
    private SeriesReminder seriesReminder;

    public static Reminder create(Context context, Realm realm, int programId, long timeAhead) {
        return create(context, realm, programId, timeAhead, null);
    }

    public static Reminder create(Context context, Realm realm, Program program, long timeAhead) {
        return create(context, realm, program, timeAhead, null);
    }

    public static Reminder create(Context context, Realm realm, int programId, long timeAhead, @Nullable SeriesReminder seriesReminder) {
        Program program = realm.where(Program.class).equalTo(Program.C_ID_INT, programId).findFirst();
        if (program == null) {
            if (InterruptEarly.now(String.format("Program with id %d doesnt exist!", programId)))
                return null;
        }
        return create(context, realm, program, timeAhead, seriesReminder);
    }

    public static Reminder create(Context context, Realm realm, Program program, long timeAhead, SeriesReminder seriesReminder) {
        if (requestedReminderIsInvalid(realm, program, timeAhead, seriesReminder)) {
            return null;
        }

        final Reminder newReminder = realm.createObject(Reminder.class,
                new Random(System.currentTimeMillis()).nextInt());
        newReminder.setProgram(program);
        newReminder.setTimeAhead(timeAhead);
        newReminder.setStartTime(program.getStartTime() - timeAhead);
        if (seriesReminder != null) {   // TODO this causes stackOverflowError while using Realm#insert - check if it's needed
            newReminder.setRepeating(true);
            newReminder.setSeriesReminder(seriesReminder);
        }
        realm.copyToRealm(newReminder); // TODO change to insert§
        Log.d(TAG, String.format("created reminder for program \"%s\" with id %d and timeAhead %d",
                program.getTitle(), program.getId(), timeAhead));
        return newReminder;
    }

    public static void delete(Context context, Realm realm, int intentId) {
        Reminder reminderToDelete = realm.where(Reminder.class).equalTo(Reminder.C_ID_INT, intentId).findFirst();
        if (reminderToDelete == null) {
            if (InterruptEarly.now(String.format("Reminder with id %d doesnt exist!", intentId)))
                return;
        }
        delete(context, reminderToDelete);
    }

    public static void delete(Context context, Reminder reminder) {
        reminder.deleteFromRealm();
    }

    private static boolean requestedReminderIsInvalid(Realm realm, Program program, long timeAhead, SeriesReminder seriesReminder) {
        if (seriesReminder != null) {
            if (reminderForSeriesAlreadyExists(realm, program, seriesReminder)) {
                return true;
            }
            if (reminderWouldBeBeforeNow(program, timeAhead)) {
                return true;
            }
        } else {
            crashIfSingleReminderAlreadyExists(realm, program, timeAhead);
        }
        return false;
    }

    private static boolean reminderWouldBeBeforeNow(Program program, long timeAhead) {
        return program.getStartTimeInMillis() - timeAhead * 1000 < System.currentTimeMillis();
    }

    private static void crashIfSingleReminderAlreadyExists(Realm realm, Program program, long timeAhead) {
        Reminder programReminder = realm
                .where(Reminder.class)
                .equalTo(Reminder.C_PROGRAM_PROGRAM + "." + Program.C_ID_INT, program.getId())
                .equalTo(Reminder.C_TIMEAHEAD_LONG, timeAhead)
                .findFirst();
        if (programReminder != null) {
            InterruptEarly.now(String.format(
                    "reminder for program \"%s\" with id %d and timeAhead %d " +
                            "already exists!",
                    program.getTitle(), program.getId(), timeAhead));
        }
    }

    private static boolean reminderForSeriesAlreadyExists(Realm realm, Program program, SeriesReminder seriesReminder) {
        Reminder programReminder = realm
                .where(Reminder.class)
                .equalTo(Reminder.C_PROGRAM_PROGRAM + "." + Program.C_ID_INT, program.getId())
                .equalTo(Reminder.C_TIMEAHEAD_LONG, seriesReminder.getTimeAhead())
                .findFirst();
        if (programReminder != null) {
            Log.d(TAG, String.format(
                    "create: reminder for program \"%s\" with id %d and timeAhead %d " +
                            "already exists, skipping",
                    program.getTitle(), program.getId(), seriesReminder.getTimeAhead()));
            return true;
        }
        return false;
    }

    public int getId() {
        return id;
    }

    private void setId(int id) {
        this.id = id;
    }

    public Program getProgram() {
        return program;
    }

    private void setProgram(Program program) {
        this.program = program;
    }

    public long getTimeAhead() {
        return timeAhead;
    }

    private void setTimeAhead(long timeAhead) {
        this.timeAhead = timeAhead;
    }

    public long getTimeAheadInMillis() {
        return timeAhead * 1000;
    }

    public boolean isRepeating() {
        return repeating;
    }

    private void setRepeating(boolean repeating) {
        this.repeating = repeating;
    }

    public long getStartTime() {
        return startTime;
    }

    private void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getStartTimeInMillis() {
        return startTime * 1000;
    }

    public SeriesReminder getSeriesReminder() {
        return seriesReminder;
    }

    private void setSeriesReminder(SeriesReminder seriesReminder) {
        this.seriesReminder = seriesReminder;
    }
}
