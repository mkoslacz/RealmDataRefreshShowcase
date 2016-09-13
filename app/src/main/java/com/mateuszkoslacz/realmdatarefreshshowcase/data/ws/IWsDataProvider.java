package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.CategoriesAndSubcategories;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Recommended;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.SearchResults;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.StationsAndProviders;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


/**
 * Created by klewandowski on 2016-03-09.
 * <p>
 * Interface which has api annotations and defs
 */
public interface IWsDataProvider {
    @GET("stations")
    Call<StationsAndProviders> getStationsAndProviders();

    @GET("categories")
    Call<CategoriesAndSubcategories> getCategoriesWithSubcategories();

    @GET("search")
    Call<SearchResults> searchApi(@Query("query") String searchQuery);

    @GET("recommended")
    Call<List<Recommended>> getRecommendedPrograms(@Query("ts") long fromTimestamp, @Query("ets") long toTimestamp);

    @GET("programssid/{id}")
    Call<List<Program>> getProgramsFor7DaysForStation(@Path("id") int stationId);

    @GET("programsday/{id}")
    Call<List<Program>> getProgramsFor24HoursForStation(@Path("id") int stationId);

    @GET("programsday/{id}")
    Call<List<Program>> getProgramsFor24HoursForStation(@Path("id") int stationId,
                                                        @Query("ts") long timestamp);

    @GET("programs/{fromTimestamp}")
    Call<List<Program>> getAllProgramsForPeriod(@Path("fromTimestamp") long fromTimestamp,
                                                @Query("ets") long toTimestamp);

    @GET("programs/{fromTimestamp}")
    Call<List<Program>> getProgramsForPeriodForStations(@Path("fromTimestamp") long fromTimestamp,
                                                        @Query("ets") long toTimestamp,
                                                        @Query("ids") String ids);


    @GET("programs/{fromTimestamp}")
    Call<List<Program>> getProgramsForPeriodForCategory(@Path("fromTimestamp") long fromTimestamp,
                                                        @Query("ets") long toTimestamp,
                                                        @Query("gid") String categoryId);

    //    @GET("programs/{timestamp}")
//    Call<List<Program>> getProgramsForPlatform(@Path("timestamp") long timestamp,
//                                               @Query("ids") String platformsIds);
//
    @GET("programinfo/{id}")
    Call<Program> getProgramDetails(@Path("id") int programId);
//
//    @GET("series/{id}")
//    Call<List<Program>> getOtherProgramOccurrences(@Path("id") int programId);
//
//    @GET("series/{timestamp}")
//    Call<List<Program>> getPrograms(@Path("timestamp") long timestamp);
//
//    @GET("programs/{timestamp}")
//    Call<List<Program>> getProgramsForCategory(@Path("timestamp") long timestamp,
//                                               @Query("gid") String categoryId);


}
