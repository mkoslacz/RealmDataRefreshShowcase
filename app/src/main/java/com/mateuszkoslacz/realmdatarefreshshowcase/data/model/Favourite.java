package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.util.Log;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.Timestamp;
import com.mateuszkoslacz.realmdatarefreshshowcase.util.InterruptEarly;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.annotations.PrimaryKey;

/**
 * Created by mkoslacz on 08.04.16.
 */
public class Favourite extends RealmObject implements IUserRating {

    public static final String C_TIMESTAMP_LONG = "timestamp";
    public static final String C_TITLE_STRING = "title";
    public static final String C_LIKESCOUNT_INT = "likesCount";
    public static final String C_CATEGORIES_CATEGORY = "categories";
    private static final String TAG = "Favourite";

    @PrimaryKey
    private String title;
    private long timestamp;
    private int likesCount;
    private RealmList<Category> categories;

    /**
     * Use it to remove a given favourite. It cleans all saved programs from being marked as a favourite.
     * Use it inside a transaction.
     * <p>
     * Given favourite has to be attached to Realm. In the other cases use method
     * {@link #delete(String favouriteName, Realm realm)}.
     */
    private static void delete(Favourite favourite, Realm realm) {
        favourite.prepareToRemove(realm);
        favourite.deleteFromRealm();
    }

    /**
     * Use it to remove a favourite with given name. It cleans all saved programs from being marked as a favourite.
     * Use it inside a transaction.
     */
    public static void delete(String favouriteName, Realm realm) {
        Favourite favourite = realm.where(Favourite.class).equalTo(Favourite.C_TITLE_STRING, favouriteName).findFirst();
        if (favourite == null) {
            if (InterruptEarly.now(String.format("Favourite named \"%s\" doesnt exist!", favouriteName)))
                return;
        }
        delete(favourite, realm);
    }

    /**
     * Use it to create new Favourite from given Program. It marks all matching programs as
     * a favourite. Use it inside a transaction.
     * <p>
     * Given program has to be attached to Realm. In the other cases use method
     * {@link #create(int programId, Realm realm)}.
     *
     * @return freshly created Favourite.
     */
    private static Favourite create(Program program, Realm realm) {
        Log.d(TAG, "create: from program");
        Favourite newFavourite = realm.createObject(Favourite.class, program.getTitle());
        newFavourite.setTimestamp(Timestamp.getNow());
        newFavourite.setCategories(program.getCategories());
        newFavourite.init(realm);
        return newFavourite;
    }

    /**
     * Use it to create new Favourite from given programId. It marks all matching programs as
     * a favourite. Use it inside a transaction.
     *
     * @return freshly created Favourite.
     */
    public static void create(int programId, Realm realm) {
        Log.d(TAG, "create: from id");
        Program program = realm.where(Program.class).equalTo(Program.C_ID_INT, programId).findFirst();
        if (program == null) {
            if (InterruptEarly.now(String.format("Program with id %d doesnt exist!", programId)))
                return;
        }
        Favourite favourite = realm.where(Favourite.class).equalTo(Favourite.C_TITLE_STRING, program.getTitle()).findFirst();
        if (favourite != null) {
            if (InterruptEarly.now(String.format("Favourite \"%s\" already exists!", favourite.getTitle())))
                return;
        }
        create(program, realm);
    }

    @Override
    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    @Override
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
    }

    /**
     * Use it to remove favourite. Usage:
     * </p>
     * <pre>
     * {@code
     *
     * final Favourite favouriteToRemove = someFavourite;
     * realm.executeTransactionAsync(new Realm.Transaction() {  // or sync, whatever
     *
     * public void execute(Realm realm) {
     * favouriteToRemove.prepareToRemove(realm);
     * favouriteToRemove.deleteFromRealm();
     * }
     * });
     * }
     * </pre>
     *
     * @param realm
     */
    public void prepareToRemove(Realm realm) {
        RealmResults<Program> connectedPrograms = realm
                .where(Program.class)
                .equalTo(Program.C_TITLE_STRING, title)
                .findAll();
        for (int i = 0, connectedProgramsSize = connectedPrograms.size(); i < connectedProgramsSize; i++) {
            Program program = connectedPrograms.get(i);
            program.setUserRating(Program.USER_RATING_NEUTRAL);
        }

    }

    /**
     * Invoke this just after instantiating fields of new object! Sample usage:
     * </p>
     * <pre>
     * {@code
     *
     * realm.executeTransactionAsync(new Realm.Transaction() { // or sync, wathever
     *
     * public void execute(Realm realm) {
     * Favourite newFavourite= realm.createObject(Favourite.class);
     * newFavourite.setTimestamp(Timestamp.getNow());
     * newFavourite.setTitle(title);
     * newFavourite.setCategories(categories);
     * newFavourite.init(realm);
     * }
     * });
     * }
     * </pre>
     *
     * @param realm
     */
    public void init(Realm realm) {
        Log.d(TAG, "init: ");
        if (correspondingNotForMeExists(realm)) {
            if (InterruptEarly.now(
                    String.format("Tried to create %s favourite - so titled NotForMe already exists!", title)))
                return;
        }

        RealmResults<Program> programsToBeConnected = realm
                .where(Program.class)
                .equalTo(Program.C_TITLE_STRING, title)
                .findAll();

        for (int i = 0, programsToBeConnectedSize = programsToBeConnected.size(); i < programsToBeConnectedSize; i++) {
            Program program = programsToBeConnected.get(i);
            program.setUserRating(Program.USER_RATING_FAVOURITE);
        }
    }

    private boolean correspondingNotForMeExists(Realm realm) {
        return realm.where(NotForMe.class).equalTo(NotForMe.C_TITLE_STRING, title).findFirst() != null;
    }


    @Override
    public boolean equals(Object o) {
        return o instanceof Favourite && this.getTitle().equals(((Favourite) o).getTitle());
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }
}
