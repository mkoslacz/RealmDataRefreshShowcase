package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.InvalidEntriesRemover;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by mkoslacz on 15.03.16.
 * <p>
 * Api response only - not saved to db like this
 * Endpoint "/stations".
 */
public class StationsAndProviders implements IModelObject {

    private List<Station> stations;
    private List<Provider> providers;

    public StationsAndProviders() { //mandatory for correct json deserialization
    }

    public StationsAndProviders(List<Station> stations, List<Provider> providers) {
        this.stations = stations;
        this.providers = providers;
        bind();
    }

    public List<Station> getStations() {
        return InvalidEntriesRemover.processList(stations);
    }

    public List<Provider> getProviders() {
        return InvalidEntriesRemover.processList(providers);
    }

    private StationsAndProviders mapStationIdsToDbRelation() {
        if (providers == null || stations == null) {
            providers = new ArrayList<>();
            stations = new ArrayList<>();
            return this;
        }

        for (Station station : stations) {
            station.bind();
        }

        for (Provider provider : providers) {
            provider.mapStationIdsToDbRelation(stations);
            provider.bind();
        }

        return this;
    }

    @Override
    public boolean isContentValid() {
        return true;
    }

    @Override
    public StationsAndProviders bind() {
        InvalidEntriesRemover.processList(stations);
        InvalidEntriesRemover.processList(providers);
        mapStationIdsToDbRelation();
        return this;
    }
}
