package com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers;

import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.lang.reflect.Type;

import io.realm.RealmList;

/**
 * Created by mkoslacz on 16.03.16.
 */
public class RealmListIntTypeAdapter extends TypeAdapter<RealmList<RealmInt>> {

    public static Type TYPE_TOKEN = new TypeToken<RealmList<RealmInt>>() {
    }.getType();

    @Override
    public void write(JsonWriter out, RealmList<RealmInt> value) throws IOException {
        // YAGNI ignore, we use only deserialization
    }

    @Override
    public RealmList<RealmInt> read(JsonReader in) throws IOException {
        RealmList<RealmInt> list = new RealmList<RealmInt>();
        in.beginArray();
        while (in.hasNext()) {
            list.add(new RealmInt(in.nextInt()));
        }
        in.endArray();
        return list;
    }


}
