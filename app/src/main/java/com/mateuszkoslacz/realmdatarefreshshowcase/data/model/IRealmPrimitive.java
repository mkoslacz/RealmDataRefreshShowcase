package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

/**
 * Created by mkoslacz on 16.03.16.
 */
public interface IRealmPrimitive<T> {

    public Object getValue();

    public void setValue(T value);
}
