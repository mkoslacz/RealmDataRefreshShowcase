package com.mateuszkoslacz.realmdatarefreshshowcase.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.CategoriesAndSubcategories;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Station;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.StationsAndProviders;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.WsDataProvider;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.Timestamp;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.ITeleshowResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowListResponse;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by mkoslacz on 29.03.16.
 */
public class UpdateDataFromApiService extends IntentService {

    private static final String TAG = "UpdateDataFromApi";
    private static final int FAILURES_LIMIT = 5;
    WsDataProvider mWsDataProvider;
    private Realm mRealm;

    public UpdateDataFromApiService() {
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mWsDataProvider = new WsDataProvider(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mWsDataProvider = null;
    }

    private Realm initRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        return Realm.getDefaultInstance();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        mRealm = initRealm();
        getAndSaveStationsAndProviders();
        getAndSaveCategoriesWithSubcategories();
        getAndSaveAllProgramsForStations();
    }

    private void getAndSaveStationsAndProviders() {
        ITeleshowResponse<StationsAndProviders> stationsAndProviders =
                mWsDataProvider.getStationsAndProvidersSync();
        mRealm.executeTransaction(realm -> {
            realm.copyToRealmOrUpdate(stationsAndProviders.getBody().getStations());
            realm.copyToRealmOrUpdate(stationsAndProviders.getBody().getProviders());
        });
    }

    private void getAndSaveCategoriesWithSubcategories() {
        ITeleshowResponse<CategoriesAndSubcategories> categoriesWithSubcategories =
                mWsDataProvider.getCategoriesWithSubcategoriesSync();
        mRealm.executeTransaction(realm ->
                realm.copyToRealmOrUpdate(categoriesWithSubcategories.getBody().getCategories()));
    }

    private void getAndSaveAllProgramsForStations() {
        List<Integer> subscribedStationIds = getAllStationsIds();
        wholeWeekFetch(subscribedStationIds);
    }

    public List<Integer> getAllStationsIds() {
        List<Station> subscribedStations =
                mRealm.where(Station.class).findAll();

        List<Integer> subscribedStationIds = new ArrayList<>();
        for (Station station : subscribedStations) {
            subscribedStationIds.add(station.getSid());
        }
        return subscribedStationIds;
    }

    private void wholeWeekFetch(List<Integer> stationIdsToFetch) {
        for (int i = 0; i < 7; i++) {
            Log.d(TAG, String.format("wholeWeekFetch: starting day %d", i));
            givenDayFetch(stationIdsToFetch, i);
        }
    }

    private void givenDayFetch(List<Integer> stationIdsToFetch, final int dayIndexFromNow) {
        long startFetchTimestamp = Timestamp.getBegginingOfCurrentDay()
                + dayIndexFromNow * Timestamp.getDayInSeconds();
        long endFetchTimestamp = startFetchTimestamp + Timestamp.getDayInSeconds();

        genericFetch(stationIdsToFetch, "givenDayFetch",
                new DownloadProgramsFromTo(startFetchTimestamp, endFetchTimestamp));
    }

    private void genericFetch(List<Integer> stationIdsToFetch,
                              String methodName, StationProcessingProcedure download) {

        Log.d(TAG, String.format("%s: stationIdsToFetch size: "
                + stationIdsToFetch.size() + " ==========", methodName));

        List<Program> allFetchedPrograms = new ArrayList<>();

        for (int i = 0, stationIdsToFetchSize = stationIdsToFetch.size(); i < stationIdsToFetchSize; i++) {
            int stationId = stationIdsToFetch.get(i);

            List<Program> programsFetchedForCurrentStationId = download.getPrograms(stationId);

            if (programsFetchedForCurrentStationId != null) {
                allFetchedPrograms.addAll(programsFetchedForCurrentStationId);
            } else {
                return;
            }

            if (i % 10 == 0 && !allFetchedPrograms.isEmpty()) {
                saveProgramsToDb(allFetchedPrograms);
                allFetchedPrograms = new ArrayList<>();
                Log.d(TAG, String.format(
                        "%s: programs size after %d stations fetch: %d",
                        methodName,
                        i,
                        mRealm.where(Program.class).count()));
            }

        }
        if (!allFetchedPrograms.isEmpty()) {
            saveProgramsToDb(allFetchedPrograms);
            Log.d(TAG, String.format(
                    "%s: programs size on finish: %d",
                    methodName,
                    mRealm.where(Program.class).count()));
        } else {
            Log.d(TAG, String.format("%s: no programs downloaded on finish", methodName));
        }
    }

    private List<Program> getProgramsForGivenPeriodProgramAfterProgram(int stationId,
                                                                       long fromTimestamp,
                                                                       long toTimestamp) {
        Log.d(TAG, String.format("getProgramsForGivenPeriodProgramAfterProgram: " +
                        "stationId: %d, fromTimestamp: %d, toTimestmp: %d",
                stationId,
                fromTimestamp,
                toTimestamp));

        int failuresCount = 0;
        while (true) {

            TeleshowListResponse<Program> programsResponse = mWsDataProvider
                    .getProgramsForPeriodForStationSync(fromTimestamp, toTimestamp, stationId);

            if (programsResponse.isSuccessful() && programsResponse.getNetworkResponseCode() != 304) {
                failuresCount = 0;
                return programsResponse.getBody();
            } else if (programsResponse.getResponseCode() != -1) {
                // now, when we got non-2xx response code, we just skip and do not notify user about
                // any problems. We do so because in most cases this scenario means that user has a
                // proper data already (304), or we fucked up sth with our server (404), but it's not
                // a thing that we will happily announce to our user, "no data" prompt will be enough.
                // In case of having X broken stations and next Y ok, in this case sync just carries on
                // to get as much data as possible.
                Log.d(TAG, String.format("response code %d, skipping", programsResponse.getNetworkResponseCode()));
                return new ArrayList<>();
            } else {
                // now, when we got some kind of IOException, we try to redo this few times, and after
                // that skip this query and notify a user about network problems. Sync carries on.
                // We try to redo this and then we notify a user, because this most likely means that
                // a user has network problems and timeouts, or there are some data integrity problems
                // on his side. There is also a possibility of problems on our side, ie. SSL problems
                // or wrongly sent data, but I assume that it will be a rare scenario, so it's ok to notify a user.
                failuresCount++;
                if (failuresCount >= FAILURES_LIMIT) {
                    Log.d(TAG, String.format("getProgramsForGivenPeriodProgramAfterProgram: " +
                                    "failures limit reached for id %d, status %d, code %d, error %s",
                            stationId, programsResponse.getStatus(),
                            programsResponse.getResponseCode(),
                            programsResponse.getError() == null ? "none" :
                                    programsResponse.getError().getLocalizedMessage()));
                    return new ArrayList<>();
                }
            }
        }
    }

    @NonNull
    private void saveProgramsToDb(List<Program> allFetchedPrograms) {
        // TODO: 14.09.2016 why does it say that it saves X programs to db, and then listener on programs
        // claims that there was only one new program saved?
        Log.d(TAG, String.format("saveProgramsToDb: saving %d programs to db", allFetchedPrograms.size()));
        mRealm.executeTransaction(realm
                -> realm.copyToRealmOrUpdate(allFetchedPrograms));
    }

    private interface StationProcessingProcedure {
        List<Program> getPrograms(int stationId);
    }

    private class DownloadProgramsFromTo implements StationProcessingProcedure {

        private long start;
        private long end;

        public DownloadProgramsFromTo(long fromTimestamp, long toTimestamp) {
            this.start = fromTimestamp;
            this.end = toTimestamp;
        }

        @Override
        public List<Program> getPrograms(int stationId) {
            return getProgramsForGivenPeriodProgramAfterProgram(stationId, start, end);
        }
    }
}