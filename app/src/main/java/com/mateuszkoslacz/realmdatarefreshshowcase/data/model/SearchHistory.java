package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 08.04.16.
 */
public class SearchHistory extends RealmObject {

    public static final String C_TIMESTAMP_LONG = "timestamp";
    public static final String C_QUERY_STRING = "query";

    @PrimaryKey
    private String query;
    private long timestamp;

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof SearchHistory && this.getQuery().equals(((SearchHistory) o).getQuery());
    }

    @Override
    public int hashCode() {
        return getQuery().hashCode();
    }
}
