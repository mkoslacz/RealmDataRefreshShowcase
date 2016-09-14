package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.net.Uri;

import io.realm.Realm;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 15.03.16.
 */
public class Station extends RealmObject implements IModelObject {
    public static final String C_SID_INT = "sid";
    public static final String C_TITLE_STRING = "title";
    public static final String C_NOSPECIALCHARS_TITLE_STRING = "noSpecialCharsTitle";
    public static final String C_LANG_STRING = "lang";
    public static final String C_IMG_STRING = "img";
    public static final String C_OWNED_BOOLEAN = "owned";
    public static final String C_FAVORITE_BOOLEAN = "favorite";
    public static final String C_FAVOURITE_POSITION_INT = "favouritePosition";
    public static final String C_POSITION_INT = "position";

    @PrimaryKey
    private int sid;
    private String title;
    private String lang;
    private String img;
    private String icon;
    private boolean owned;
    private boolean favorite;
    private int position;

    public Station() { //mandatory for correct json deserialization
    }

    public int getSid() {
        return sid;
    }

    public String getTitle() {
        return title;
    }

    public String getLang() {
        return lang;
    }

    public String getImg() {
        return img;
    }

    public boolean isOwned() {
        return owned;
    }

    public void setOwned(boolean owned) {
        this.owned = owned;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean pFavorite) {
        favorite = pFavorite;
    }

    public int getPosition() {
        return position;
    }

    @Override
    public boolean isContentValid() {
        return getTitle() != null && !getTitle().isEmpty();
    }

    @Override
    public IModelObject bind() {
        if (Realm.getDefaultInstance()
                .where(Station.class)
                .equalTo(Station.C_SID_INT, sid)
                .equalTo(Station.C_OWNED_BOOLEAN, true)
                .findFirst()
                != null) {
            owned = true;
        }
        return this;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Station && this.getSid() == ((Station) o).getSid();
    }

    @Override
    public int hashCode() {
        return getSid();
    }
}
