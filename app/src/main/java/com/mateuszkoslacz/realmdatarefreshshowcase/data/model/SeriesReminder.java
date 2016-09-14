package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.Timestamp;
import com.mateuszkoslacz.realmdatarefreshshowcase.util.InterruptEarly;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 19.05.16.
 */
public class SeriesReminder extends RealmObject {

    public static final String C_STATIONID_INT = "stationId";
    public static final String C_TIMEAHEAD_LONG = "timeAhead";
    public static final String C_REMINDERS_REMINDERS = "reminders";
    public static final String C_PROGRAMTITLE_STRING = "programTitle";
    private static final String TAG = "SeriesReminder";
    @PrimaryKey
    private String programTitle;
    private int stationId;
    private long timeAhead;
    private RealmList<Reminder> reminders;

    public static SeriesReminder create(Context context, Realm realm, int programId, long timeAhead) {
        Program program = realm.where(Program.class).equalTo(Program.C_ID_INT, programId).findFirst();
        crashIfRequestIsInvalid(realm, program);
        final SeriesReminder newSeriesReminder =
                createInitialSeriesReminder(realm, timeAhead, program);
        updateRemindersForEpisodes(context, realm, newSeriesReminder);
        return newSeriesReminder;

    }

    @NonNull
    private static SeriesReminder createInitialSeriesReminder(Realm realm, long timeAhead, Program program) {
        final SeriesReminder newSeriesReminder = realm.createObject(SeriesReminder.class,
                program.getTitle());
        newSeriesReminder.setStationId(program.getStationId());
        newSeriesReminder.setTimeAhead(timeAhead);
        return newSeriesReminder;
    }

    private static void crashIfRequestIsInvalid(Realm realm, Program program) {
        crashIfProgramDoesntExist(program);
        crashIfProgramIsNotASerial(program);
        crashIfRequestedSeriesReminderAlreadyExists(realm, program);
    }

    private static void crashIfProgramDoesntExist(Program program) {
        if (program == null) {
            InterruptEarly.now("Cannot create SeriesReminder for program with id %d - this program doesn't exist!");
        }
    }

    private static void crashIfProgramIsNotASerial(Program program) {
        if (!program.isSerial()) {
            InterruptEarly.now(String.format(
                    "Program \"%s\" with id %d is not a serial!",
                    program.getTitle(),
                    program.getId()));
        }
    }

    private static void crashIfRequestedSeriesReminderAlreadyExists(Realm realm, Program program) {
        SeriesReminder seriesReminder = realm
                .where(SeriesReminder.class)
                .equalTo(SeriesReminder.C_PROGRAMTITLE_STRING, program.getTitle())
                .findFirst();
        if (seriesReminder != null) {
            InterruptEarly.now(String.format(
                    "Cannot create Series reminder for program \"%s\" with id %d - it already exists!",
                    program.getTitle(),
                    program.getId()));
        }
    }

    public static void updateRemindersForEpisodes(Context context, Realm realm, SeriesReminder seriesReminder) {
        if (seriesReminder.getReminders() == null) {
            seriesReminder.setReminders(new RealmList<>());
        }
        List<Program> incomingEpisodes = findEpisodesWithoutRetransmissions(realm, seriesReminder);

        for (int i = 0, incomingEpisodesSize = incomingEpisodes.size(); i < incomingEpisodesSize; i++) {
            Program program = incomingEpisodes.get(i);
            Reminder newReminder = Reminder.create(context, realm, program.getId(), seriesReminder.getTimeAhead(), seriesReminder);
            if (newReminder != null) {
                seriesReminder.getReminders().add(newReminder);
            }
        }
    }

    public static List<Program> findEpisodesWithoutRetransmissions(Realm realm, SeriesReminder seriesReminder) {
        Program program = realm.where(Program.class).equalTo(Program.C_TITLE_STRING, seriesReminder.getProgramTitle()).findFirst();
        crashIfProgramDoesntExist(program);
        crashIfProgramIsNotASerial(program);

        RealmResults<Program> incomingDistinctEpisodes = realm
                .where(Program.class)
                .equalTo(Program.C_TITLE_STRING, program.getTitle())
                .equalTo(Program.C_STATIONID_INT, program.getStationId())
                .greaterThan(Program.C_STARTTIME_LONG, Timestamp.getNow() - seriesReminder.getTimeAhead())
                .findAllSorted(Program.C_STARTTIME_LONG, Sort.ASCENDING)
                .distinct(Program.C_EPISODE_INT);

        Log.d(TAG, String.format("findEpisodesWithoutRetransmissions: found %d episodes", incomingDistinctEpisodes.size()));
        return incomingDistinctEpisodes;
    }

    public static void delete(Context context, Realm realm, String seriesReminderTitle) {
        SeriesReminder seriesReminderToDelete = realm.where(SeriesReminder.class)
                .equalTo(SeriesReminder.C_PROGRAMTITLE_STRING, seriesReminderTitle).findFirst();
        if (seriesReminderToDelete == null) {
            if (InterruptEarly.now(String.format("SeriesReminder with title %s doesnt exist!", seriesReminderTitle)))
                return;
        }
        delete(context, seriesReminderToDelete);
    }

    public static void delete(Context context, SeriesReminder seriesReminder) {
        deleteChildReminders(context, seriesReminder);
        seriesReminder.deleteFromRealm();
    }

    private static void deleteChildReminders(Context context, SeriesReminder seriesReminder) {
        List<Reminder> reminders = seriesReminder.getReminders();
        while (!reminders.isEmpty()) {
            Reminder.delete(context, reminders.get(0));
        }
    }

    public long getTimeAhead() {
        return timeAhead;
    }

    public void setTimeAhead(long timeAhead) {
        this.timeAhead = timeAhead;
    }

    public int getStationId() {
        return stationId;
    }

    public void setStationId(int stationId) {
        this.stationId = stationId;
    }

    public RealmList<Reminder> getReminders() {
        return reminders;
    }

    public void setReminders(RealmList<Reminder> reminders) {
        this.reminders = reminders;
    }

    public String getProgramTitle() {
        return programTitle;
    }

    public void setProgramTitle(String programTitle) {
        this.programTitle = programTitle;
    }
}
