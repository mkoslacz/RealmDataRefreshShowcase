package com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers;

import com.google.gson.GsonBuilder;

import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mkoslacz on 16.03.16.
 */
public class RealmGsonDeserializerFactoryForRetrofit2 {

    public static GsonConverterFactory getDefault() {
        GsonConverterFactory realmGsonDeserializer = GsonConverterFactory.create(new GsonBuilder()
                .setExclusionStrategies(new RealmExclusionStrategy())
                .registerTypeAdapter(RealmListIntTypeAdapter.TYPE_TOKEN, new RealmListIntTypeAdapter())
                .create());

        return realmGsonDeserializer;
    }
}
