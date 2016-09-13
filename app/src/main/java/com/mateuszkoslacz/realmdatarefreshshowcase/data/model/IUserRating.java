package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by aszady on 2016-05-23.
 */
public interface IUserRating {

    long getTimestamp();

    void setTimestamp(long timestamp);

    String getTitle();

    void setTitle(String title);

    List<Category> getCategories();

    void setCategories(RealmList<Category> categories);

    int getLikesCount();
}
