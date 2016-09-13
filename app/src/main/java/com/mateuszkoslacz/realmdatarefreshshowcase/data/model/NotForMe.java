package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

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
public class NotForMe extends RealmObject implements IUserRating {

    public static final String C_TIMESTAMP_LONG = "timestamp";
    public static final String C_TITLE_STRING = "title";
    public static final String C_CATEGORIES_CATEGORY = "categories";

    @PrimaryKey
    private String title;
    private long timestamp;
    private RealmList<Category> categories;

    /**
     * Use it to remove a given notForMe. It cleans all saved programs from being marked as a notForMe.
     * Use it inside a transaction.
     * <p>
     * Given favourite has to be attached to Realm. In the other cases use method
     * {@link #delete(String notForMeName, Realm realm)}.
     */
    public static void delete(NotForMe notForMe, Realm realm) {
        notForMe.prepareToRemove(realm);
        notForMe.deleteFromRealm();
    }

    /**
     * Use it to remove a favourite with given name. It cleans all saved programs from being marked as a favourite.
     * Use it inside a transaction.
     */
    public static void delete(String notForMeName, Realm realm) {
        NotForMe notForMe = realm.where(NotForMe.class).equalTo(NotForMe.C_TITLE_STRING, notForMeName).findFirst();
        if (notForMe == null) {
            if (InterruptEarly.now(String.format("NotForMe named \"%s\" doesnt exist!", notForMeName)))
                return;
        }
        notForMe.prepareToRemove(realm);
        notForMe.deleteFromRealm();
    }

    /**
     * Use it to create new NotForMe from given Program. It marks all matching programs as
     * a notForMe. Use it inside a transaction.
     * <p>
     * Given program has to be attached to Realm. In the other cases use method
     * {@link #create(int programId, Realm realm)}.
     *
     * @return freshly created NotForMe.
     */
    public static void create(Program program, Realm realm) {
        NotForMe newNotForMe = realm.createObject(NotForMe.class, program.getTitle());
        newNotForMe.setTimestamp(Timestamp.getNow());
        newNotForMe.setCategories(program.getCategories());
        newNotForMe.init(realm);
    }

    /**
     * Use it to create new NotForMe from given programId. It marks all matching programs as
     * a notForMe. Use it inside a transaction.
     *
     * @return freshly created NotForMe.
     */
    public static void create(int programId, Realm realm) {
        Program program = realm.where(Program.class).equalTo(Program.C_ID_INT, programId).findFirst();
        if (program == null) {
            if (InterruptEarly.now(String.format("Program with id %d doesnt exist!", programId)))
                return;
        }
        NotForMe newNotForMe = realm.createObject(NotForMe.class, program.getTitle());
        newNotForMe.setTimestamp(Timestamp.getNow());
        newNotForMe.setCategories(program.getCategories());
        newNotForMe.init(realm);
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
    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public void setCategories(RealmList<Category> categories) {
        this.categories = categories;
    }

    @Override
    public int getLikesCount() {
        InterruptEarly.now("NotForMe doesn't have likes count!");
        return 0;
    }

    /**
     * Use it to remove favourite. Usage:
     * </p>
     * <pre>
     * {@code
     *
     * realm.executeTransactionAsync(new Realm.Transaction() {  // or sync, whatever
     *
     * public void execute(Realm realm) {
     * notForMeToRemove.prepareToRemove(realm);
     * notForMeToRemove.deleteFromRealm();
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
     * NotForMe newNotForMe = realm.createObject(NotForMe.class);
     * newNotForMe.setTimestamp(Timestamp.getNow());
     * newNotForMe.setTitle(title);
     * newNotForMe.setCategories(categories);
     * newNotForMe.init(realm);
     * }
     * });
     * }
     * </pre>
     *
     * @param realm
     */
    public void init(Realm realm) {
        if (correspondingFavouriteExists(realm)) {
            if (InterruptEarly.now(
                    String.format("Tried to create \"%s\" notForMe - so titled favourite already exists!", title)))
                return;
        }

        RealmResults<Program> programsToBeConnected = realm
                .where(Program.class)
                .equalTo(Program.C_TITLE_STRING, title)
                .findAll();

        for (int i = 0, programsToBeConnectedSize = programsToBeConnected.size(); i < programsToBeConnectedSize; i++) {
            Program program = programsToBeConnected.get(i);
            program.setUserRating(Program.USER_RATING_NOT_FOR_ME);
        }
    }

    private boolean correspondingFavouriteExists(Realm realm) {
        return realm.where(Favourite.class).equalTo(Favourite.C_TITLE_STRING, title).findFirst() != null;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof NotForMe && this.getTitle().equals(((NotForMe) o).getTitle());
    }

    @Override
    public int hashCode() {
        return getTitle().hashCode();
    }
}
