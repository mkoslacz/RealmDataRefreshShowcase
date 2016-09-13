package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.InvalidEntriesRemover;

import java.util.List;

import io.realm.RealmList;

/**
 * Created by mkoslacz on 21.03.16.
 * <p>
 * Api response only - not saved to db like this
 */
public class SearchResults implements IModelObject {

    private String query;
    private int page;
    private String pageSize;
    private RealmList<Station> stations;
    private RealmList<Program> programs;

    public String getQuery() {
        return query;
    }

    public int getPage() {
        return page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public List<Station> getStations() {
        return stations;
    }

    public List<Program> getPrograms() {
        return programs;
    }

    @Override
    public boolean isContentValid() {
        return true;
    }

    @Override
    public SearchResults bind() {
        InvalidEntriesRemover.processList(stations);
        InvalidEntriesRemover.processList(programs);
        for (Program program :
                programs) {
            program.bind();
        }
        return this;
    }
}
