package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.graphics.Color;

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
    public static final String C_TIMESTAMP_LAST_USE_LONG = "lastUse";

    private static final int DEFAULT_COLOR = Color.BLACK;

    @PrimaryKey
    String name;
    RealmList<Subcategory> subcategories;
    String color;
    Long lastUse = 0l;

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

    public int getColor() {
        if (color == null) return DEFAULT_COLOR;
        try {
            if (color.length() > 4) return Color.parseColor(color);
            else return Color.parseColor(convert3SymbolHexColorTo6SymbolHexColor(color));
        } catch (IllegalArgumentException e) {
            return DEFAULT_COLOR;
        }
    }

    public Long getLastUse() {
        return lastUse;
    }

    public void setLastUse(long pTimestamp) {
        lastUse = pTimestamp;
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

    private String convert3SymbolHexColorTo6SymbolHexColor(String threeSymbolHexColor) {
        StringBuilder rrggbbBuilder = new StringBuilder("#");
        for (int i = 1; i < threeSymbolHexColor.length(); i++) {
            rrggbbBuilder.append(threeSymbolHexColor.charAt(i));
            rrggbbBuilder.append(threeSymbolHexColor.charAt(i));
        }
        return rrggbbBuilder.toString();
    }
}
