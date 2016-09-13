package com.mateuszkoslacz.realmdatarefreshshowcase.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.facebook.stetho.Stetho;
import com.mateuszkoslacz.realmdatarefreshshowcase.R;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Station;
import com.mateuszkoslacz.realmdatarefreshshowcase.service.UpdateDataFromApiService;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private RealmResults<Program> mAllPrograms;
    private RealmResults<Station> mAllStations;
    private Realm mRealm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);

        mRealm = initRealm();

        mAllPrograms = mRealm.where(Program.class).findAllAsync();
        mAllPrograms.addChangeListener(element ->
                Log.d(TAG, String.format("programs change: all elements size: %d, changed " +
                        "elements size: %d", mAllPrograms.size(), element.size())));

        mAllStations = mRealm.where(Station.class).findAllAsync();
        mAllStations.addChangeListener(element ->
                Log.d(TAG, String.format("stations change: all elements size: %d, changed " +
                        "elements size: %d", mAllStations.size(), element.size())));

        startFetchingData();
    }

    private Realm initRealm() {
        RealmConfiguration realmConfig = new RealmConfiguration.Builder(this).build();
        Realm.setDefaultConfiguration(realmConfig);
        return Realm.getDefaultInstance();
    }

    private void startFetchingData() {
        Intent intent = new Intent(MainActivity.this, UpdateDataFromApiService.class);
        startService(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAllPrograms.removeChangeListeners();
        mAllStations.removeChangeListeners();
        mRealm.close();
    }
}
