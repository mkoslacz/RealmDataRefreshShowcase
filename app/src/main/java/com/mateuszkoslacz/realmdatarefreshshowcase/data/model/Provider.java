package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers.RealmInt;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 15.03.16.
 */
public class Provider extends RealmObject implements IModelObject {

    public static final String C_ID_INT = "id";
    public static final String C_NAME_STRING = "name";
    public static final String C_STATIONS_LIST_REALMINT = "stations";
    public static final String C_STATIONOBJECTS_LIST_STATIONS = "stationObjects";
    public static final String C_OWNED_BOOLEAN = "owned";

    @PrimaryKey
    private int id;
    private String name;
    private RealmList<RealmInt> stations;
    private RealmList<Station> stationObjects = new RealmList<>();
    private boolean owned;

    public Provider() {  //mandatory for correct json deserialization
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public RealmList<RealmInt> getStationIds() {
        return stations;
    }

    public RealmList<Station> getStations() {
        return stationObjects;
    }

    public void mapStationIdsToDbRelation(List<Station> allStations) {
        for (RealmInt stationId : stations) {

            for (Station station : allStations) {
                if (station.getSid() == stationId.getValue()) {
                    stationObjects.add(station);
                    break;
                }
            }

        }
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    @Override
    public boolean isContentValid() {
        return getName() != null && !getName().isEmpty();
    }

    @Override
    public IModelObject bind() {
        if (Realm.getDefaultInstance()
                .where(Provider.class)
                .equalTo(Provider.C_ID_INT, id)
                .equalTo(Provider.C_OWNED_BOOLEAN, true)
                .findFirst()
                != null) {
            owned = true;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Provider && this.getId() == ((Provider) o).getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }
}
