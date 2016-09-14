package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 18.03.16.
 */
public class Category extends RealmObject implements IModelObject {

    public static final String C_NAME_STRING = "name";
    public static final String C_SUBCATEGORIES_LIST_SUBCATEGORY = "subcategories";
    public static final String C_COLOR_STRING = "color";

    @PrimaryKey
    String name;
    RealmList<Subcategory> subcategories;
    String color;

    public Category() { //mandatory for correct json deserialization
    }

    public String getName() {
        return name;
    }

    public RealmList<Subcategory> getSubcategories() {
        return subcategories;
    }

    public void setSubcategories(RealmList<Subcategory> subcategories) {
        this.subcategories = subcategories;
    }

    public String getColor() {
        return color;
    }

    @Override
    public boolean isContentValid() {
        return getName() != null && !getName().isEmpty();
    }

    @Override
    public IModelObject bind() {
        name = name.trim();
        color = color.trim();
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Category && this.getName().equals(((Category) o).getName());
    }

    @Override
    public int hashCode() {
        return getName().hashCode();
    }
}
