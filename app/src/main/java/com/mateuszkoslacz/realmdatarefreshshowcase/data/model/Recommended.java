package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.Timestamp;
import com.mateuszkoslacz.realmdatarefreshshowcase.util.InterruptEarly;

import org.joda.time.Interval;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.RealmResults;
import io.realm.Sort;
import io.realm.annotations.Index;
import io.realm.annotations.PrimaryKey;


/**
 * Created by mkoslacz on 03.06.16.
 */
public class Recommended extends RealmObject implements IModelObject {
    public static final String C_PROGRAM_PROGRAM = "program";
    public static final String C_ID_INT = "id";
    public static final String C_STATIONID_INT = "stationId";
    public static final String C_STATION_STATION = "station";
    public static final String C_TITLE_STRING = "title";
    public static final String C_NOSPECIALCHARS_TITLE_STRING = "noSpecialCharsTitle";
    public static final String C_TITLEORIG_STRING = "titleOrig";
    public static final String C_DIRECTOR_STRING = "director";
    public static final String C_CAST_STRING = "cast";
    public static final String C_EPISODE_INT = "episode";
    public static final String C_EPISODESTOTAL_INT = "episodesTotal";
    public static final String C_SEASON_INT = "season";
    public static final String C_PART_INT = "part";
    public static final String C_ISLIVE_BOOLEAN = "isLive";
    public static final String C_PHOTO_STRING = "photo";
    public static final String C_CATEGORIES_LIST_CATEGORY = "categories";
    public static final String C_SUBCATEGORIES_LIST_SUBCATEGORY = "subcategories";
    public static final String C_SHORTDESCRIPTION_STRING = "shortDesciption";
    public static final String C_HIT_STRING = "hit";
    public static final String C_DURATION_INT = "duration";
    public static final String C_COUNTRY_STRING = "country";
    public static final String C_YEAR_INT = "year";
    public static final String C_DESCRIPTION_STRING = "description";
    public static final String C_RATING_STRING = "rating";
    public static final String C_STARTTIME_LONG = "startTime";
    public static final String C_ENDTIME_LONG = "endTime";
    public static final String C_NEXT_INT = "next";
    public static final String C_PREV_INT = "prev";
    public static final String C_TYPE_INT = "type";
    public static final String C_RECOMMENDED_BOOLEAN = "recommended";
    public static final String C_USERRATING_INT = "userRating";
    public static final String C_LIKESCOUNT_INT = "likesCount";
    public static final String C_CATEGORIESSTRING_STRING = "categoriesString";
    public static final String C_SUBCATEGORIESSTRING_STRING = "subcategoriesString";

    public static final int USER_RATING_NEUTRAL = 0;
    public static final int USER_RATING_FAVOURITE = 1;
    public static final int USER_RATING_NOT_FOR_ME = -1;

    public static final int TYPE_PROGRAM = 1;
    public static final int TYPE_MOVIE = 2;
    public static final int TYPE_TV_SERIES = 3;
    public static final int TYPE_OTHERS = 4;


    @PrimaryKey
    private int id;
    private int stationId;
    private Station station;
    private String title;
    private String titleTel;
    private String titleOrig;
    private String titleTelOrig;
    private String director;
    private String cast;
    @Index
    private int episode;
    private int episodesTotal;
    private int season;
    private int part;
    private boolean isLive;
    private String photoTel;
    private String photo;
    private RealmList<Category> categories;
    private RealmList<Subcategory> subcategories;
    private String shortDescription;
    private String hit;
    private int duration;
    private String country;
    private int year;
    private String description;
    private String descriptionTel;
    private String rating;
    private long startTime;
    private long endTime;
    private int next;
    private int prev;
    private int type;
    private boolean recommended;
    private int userRating;
    private int likesCount;
    private Reminder reminder;
    private String categoriesString;
    private String subcategoriesString;
    private Program program;


    public static Uri ResourceToUri(Context context, int resID) {
        return Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE + "://" +
                context.getResources().getResourcePackageName(resID) + '/' +
                context.getResources().getResourceTypeName(resID) + '/' +
                context.getResources().getResourceEntryName(resID));
    }

    public static void createSingleReminder(Realm realm, Context context, Program program, long timeAhead) {
        Reminder.create(context, realm, program, timeAhead, null);

    }

    public static void createSeriesReminder(Realm realm, Context context, Program program, long timeAhead) {
        if (!program.isSerial()) {
            if (InterruptEarly.now(
                    String.format(
                            "tried to create series reminder on non-series program \"%s\" id %d",
                            program.getTitle(),
                            program.getId()))) return;
        }
        Reminder.create(context, realm, program, timeAhead, null);
    }

    public static RealmResults<Recommended> getFilteredRecommendeds(Realm realm) {
        return realm.where(Recommended.class)
                .equalTo(Recommended.C_STATION_STATION + "." + Station.C_OWNED_BOOLEAN, true)
                .greaterThan(Recommended.C_STARTTIME_LONG, Timestamp.getNow())
                .lessThan(Recommended.C_STARTTIME_LONG, Timestamp.getBegginingOfCurrentDay() +
                        Timestamp.getDayInSeconds() +
                        Timestamp.getDayEndTimeShiftInSeconds())
                .findAllSorted(Recommended.C_STARTTIME_LONG, Sort.ASCENDING);
    }

    /**
     * @param realm
     * @param dayNumber 0 - get Recommendeds for today, 1 - for tomorrow, etc.
     * @return
     */
    public static RealmResults<Recommended> getFilteredRecommendedsForDay(Realm realm, int dayNumber) {
        return realm.where(Recommended.class)
                .equalTo(Recommended.C_STATION_STATION + "." + Station.C_OWNED_BOOLEAN, true)
                .greaterThan(Recommended.C_STARTTIME_LONG,
                        Timestamp.getBegginingOfCurrentDay() + Timestamp.getDayInSeconds() * dayNumber)
                .lessThan(Recommended.C_STARTTIME_LONG, Timestamp.getBegginingOfCurrentDay() +
                        Timestamp.getDayInSeconds() * (dayNumber + 1) +
                        Timestamp.getDayEndTimeShiftInSeconds())
                .findAllSorted(Recommended.C_STARTTIME_LONG, Sort.ASCENDING);
    }

    public int getStationId() {
        return stationId;
    }

    public Station getStation() {
        return station;
    }

    public String getTitle() {
        return (titleTel != null && !titleTel.isEmpty()) ? titleTel : title;
    }

    public String getTitleOrig() {
        return titleOrig;
    }

    public String getDirector() {
        return director;
    }

    public String getCast() {
        return cast;
    }

    public int getEpisode() {
        return episode;
    }

    public int getEpisodesTotal() {
        return episodesTotal;
    }

    public int getSeason() {
        return season;
    }

    public int getPart() {
        return part;
    }

    public boolean getIsLive() {
        return isLive;
    }

    public RealmList<Category> getCategories() {
        return categories;
    }

    public RealmList<Subcategory> getSubcategories() {
        return subcategories;
    }

    public String getShortDescription() {
        return shortDescription;
    }

    public String getHit() {
        return hit;
    }

    public int getDuration() {
        return duration;
    }

    public String getCountry() {
        return country;
    }

    public int getYear() {
        return year;
    }

    public String getDescription() {
        return (descriptionTel != null && !descriptionTel.isEmpty()) ? descriptionTel : description;
    }

    public String getRating() {
        return rating;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getStartTimeInMillis() {
        return startTime * 1000;
    }

    public long getEndTimeInMillis() {
        return endTime * 1000;
    }

    public int getNext() {
        return next;
    }

    public int getPrev() {
        return prev;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public int getUserRating() {
        return userRating;
    }

    public void setUserRating(int userRating) {
        if (givenIntIsNotInRatingValuesRange(userRating)) {
            InterruptEarly.now(
                    String.format("Tried to set \"%s\" userRating to %d, should be one of " +
                            "USER_RATING_FAVOURITE (1), USER_RATING_NEUTRAL (0), " +
                            "or USER_RATING_NOT_FOR_ME (-1)", title, userRating));
        }
        this.userRating = userRating;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public Favourite getConnectedFavourite() {
        return Realm.getDefaultInstance().where(Favourite.class).equalTo(Favourite.C_TITLE_STRING, title).findFirst();
    }

    public NotForMe getConnectedNotForMe() {
        return Realm.getDefaultInstance().where(NotForMe.class).equalTo(NotForMe.C_TITLE_STRING, title).findFirst();
    }

    public Interval getInterval() {
        return new Interval(getStartTimeInMillis(), getEndTimeInMillis());
    }

    public double getPercentageProgress(long timestampNow) {
        if (timestampNow < getStartTime()) {
            return 0;
        }
        if (timestampNow > getEndTime()) {
            return 1;
        }
        long duration = getEndTime() - getStartTime();
        long durationPassed = timestampNow - getStartTime();
        return (double) durationPassed / (double) duration;
    }

    public boolean isHighlighted() {
        return (recommended || reminder != null || isFavourite());
    }

    public boolean isFavourite() {
        return userRating == USER_RATING_FAVOURITE;
    }

    public Reminder getReminder() {
        return reminder;
    }

    public void setReminder(Reminder reminder) {
        this.reminder = reminder;
    }

    public boolean isSerial() {
        return type == TYPE_TV_SERIES && episode > 0;
    }

    public int getType() {
        return type;
    }

    private boolean givenIntIsNotInRatingValuesRange(int userRating) {
        return userRating != USER_RATING_FAVOURITE && userRating != USER_RATING_NEUTRAL && userRating != USER_RATING_NOT_FOR_ME;
    }

    @Override
    public IModelObject bind() {
        setRecommended(true);
        Realm realm = Realm.getDefaultInstance();

        station = realm.where(Station.class).equalTo(Station.C_SID_INT, stationId).findFirst();

        RealmList<Subcategory> subcategoriesFromCategories = new RealmList<>();

        StringBuilder categoriesStringBuilder = new StringBuilder();
        StringBuilder subcategoriesStringBuilder = new StringBuilder();

        for (int i = 0, categoriesSize = categories.size(); i < categoriesSize; i++) {
            Category newCategory = (Category) categories.get(i).bind();
            categoriesStringBuilder.append(newCategory.getName());
            Category existingCategoryBindedToRealm = realm.where(Category.class).equalTo(Category.C_NAME_STRING, newCategory.getName()).findFirst();
            if (existingCategoryBindedToRealm != null && existingCategoryBindedToRealm.isValid()) {
                int subcategoriesSizeBeforeOperation = existingCategoryBindedToRealm.getSubcategories() ==
                        null ? 0 : existingCategoryBindedToRealm.getSubcategories().size();
                Category existingCategory = realm.copyFromRealm(existingCategoryBindedToRealm);
                if (existingCategory.getSubcategories() != null && newCategory.getSubcategories() != null) {
                    for (Subcategory oneOfNewSubcategories : newCategory.getSubcategories()) {
                        subcategoriesStringBuilder.append(oneOfNewSubcategories.getName());
                        if (!existingCategory.getSubcategories().contains(oneOfNewSubcategories)) {
                            existingCategory.getSubcategories().add(oneOfNewSubcategories);
                        }
                    }
                    categories.set(i, existingCategory);
                } else if (existingCategory.getSubcategories() == null) {
                    existingCategory.setSubcategories(newCategory.getSubcategories());
                }
                if (existingCategory.getSubcategories().size() > subcategoriesSizeBeforeOperation) {
                    realm.executeTransaction(realm1 ->
                            realm1.insertOrUpdate(existingCategory));
                }
            } else {
                for (Subcategory subcategory : newCategory.getSubcategories()) {
                    subcategoriesStringBuilder.append(subcategory.getName());
                }
            }
            subcategoriesFromCategories.addAll(newCategory.getSubcategories());
        }
        subcategories = subcategoriesFromCategories;
        subcategoriesString = subcategoriesStringBuilder.toString();
        categoriesString = categoriesStringBuilder.toString();


        Favourite fav = realm.where(Favourite.class).equalTo(this.C_TITLE_STRING, title).findFirst();
        if (fav != null) {
            userRating = USER_RATING_FAVOURITE;
        }

        NotForMe notForMe = realm.where(NotForMe.class).equalTo(this.C_TITLE_STRING, title).findFirst();
        if (notForMe != null) {
            userRating = USER_RATING_NOT_FOR_ME;
        }

        if (fav != null && notForMe != null) {
            InterruptEarly.now(
                    String.format("Program \"%s\" would be in favourites AND in notForMe - make sure " +
                            "that you always call Favourite.init() and NotForMe.init() right " +
                            "after instantiating its fields!", title));
        }
        return this;
    }

    public boolean isLive() {
        return isLive;
    }

    public String getPhoto() {
        return photo;
    }

    public String getCategoriesString() {
        return categoriesString;
    }

    public String getSubcategoriesString() {
        return subcategoriesString;
    }

    @Override
    public boolean isContentValid() {
        return getTitle() != null && !getTitle().isEmpty() && startTime < endTime;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Program && this.getId() == ((Program) o).getId();
    }

    @Override
    public int hashCode() {
        return getId();
    }

    public int getId() {
        return id;
    }

    public Program getProgram() {
        return Realm.getDefaultInstance().where(Program.class).equalTo(Program.C_ID_INT, id).findFirst();
    }

}