package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 18.03.16.
 */
public class Subcategory extends RealmObject implements IModelObject {

    public static final String C_ID_STRING = "id";
    public static final String C_NAME_STRING = "name";

    @PrimaryKey
    String id;
    String name;

    public Subcategory() { //mandatory for correct json deserialization
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean isContentValid() {
        return getName() != null && !getName().isEmpty();
    }

    @Override
    public IModelObject bind() {
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Subcategory && this.getId().equals(((Subcategory) o).getId());
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
    }
}
