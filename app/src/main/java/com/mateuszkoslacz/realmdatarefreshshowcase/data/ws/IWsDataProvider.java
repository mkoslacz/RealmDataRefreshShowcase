package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.CategoriesAndSubcategories;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
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

    @GET("programs/{fromTimestamp}")
    Call<List<Program>> getProgramsForPeriodForStations(@Path("fromTimestamp") long fromTimestamp,
                                                        @Query("ets") long toTimestamp,
                                                        @Query("ids") String ids);
}
