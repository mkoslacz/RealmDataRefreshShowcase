package com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IRealmPrimitive;

import io.realm.RealmObject;

/**
 * Created by mkoslacz on 16.03.16.
 */
public class RealmInt extends RealmObject implements IRealmPrimitive<Integer> {

    private Integer mValue;

    public RealmInt() { //mandatory for correct json deserialization
    }

    public RealmInt(Integer value) {
        this.mValue = value;
    }

    @Override
    public Integer getValue() {
        return mValue;
    }

    @Override
    public void setValue(Integer value) {
        this.mValue = value;
    }

    @Override
    public String toString() {
        return String.valueOf(mValue);
    }
}
