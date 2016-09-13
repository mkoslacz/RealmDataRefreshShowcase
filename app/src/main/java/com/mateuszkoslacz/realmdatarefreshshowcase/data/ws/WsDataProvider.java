package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.CategoriesAndSubcategories;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IModelObject;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Recommended;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.SearchResults;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.StationsAndProviders;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers.RealmGsonDeserializerFactoryForRetrofit2;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.IntegerListQueryFormatter;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.interceptor.Fixing406RequestInterceptor;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.listeners.WsRequestListener;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.ITeleshowResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowListResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowPrimitiveResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/**
 * Created by klewandowski on 2016-03-09.
 * Class which will handle calls and adapters
 * connected with webservice
 */
public class WsDataProvider {

    //DEBUG
    //   public static final String URL_MOBILE_API = "http://programtv.wp.front-test-2.alladyn.srv/mobileAPI/";
    //FINAL
    public static final String URL_MOBILE_API = "http://tv.wp.pl/mobileAPI/";
    // manually, so it's only peak value
    // that will appear at most for few seconds
    private static final long HTTP_CACHE_SIZE = 100 * 1024 * 1024;  // set to 100MB, but we clean it
    /**
     * Interface with api calls
     */
    private final IWsDataProvider mIWsDataProvider;
    private final Context mContext;

    public WsDataProvider(Context pContext) {
        mContext = pContext;

        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new Fixing406RequestInterceptor()) // without this api returns 406
//                .addInterceptor(
//                        new HttpLoggingInterceptor() // logging to System.out for testing purposes
//                                .setLevel(HttpLoggingInterceptor.Level.BASIC))
//                .addInterceptor(new Preserve304RequestInterceptor())
                .addNetworkInterceptor(new StethoInterceptor())
                .cache(createHttpClientCache(mContext))
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(URL_MOBILE_API)
                // set custom deserialization for custom realm adapters
                .addConverterFactory(RealmGsonDeserializerFactoryForRetrofit2.getDefault())
                .client(client)
                .build();

        mIWsDataProvider = retrofit.create(IWsDataProvider.class);


        OkHttpClient counterClient = new OkHttpClient.Builder()
                .addNetworkInterceptor(
                        new Fixing406RequestInterceptor()) // without this api returns 406
//                .addInterceptor(
//                        new HttpLoggingInterceptor() // logging to System.out for testing purposes
//                                .setLevel(HttpLoggingInterceptor.Level.BASIC))
//                .cache(createHttpClientCache(mContext))
                .build();

        Retrofit counterRetrofit = new Retrofit.Builder()
                .baseUrl(URL_MOBILE_API)
                // set custom deserialization for custom realm adapters
                .addConverterFactory(RealmGsonDeserializerFactoryForRetrofit2.getDefault())
                .client(counterClient)
                .build();


    }

    private static Cache createHttpClientCache(Context context) {
        return new Cache(context.getCacheDir(), HTTP_CACHE_SIZE);
    }


    public void getStationsAndProviders(WsRequestListener<StationsAndProviders> listener) {
        enqueueWithListener(mIWsDataProvider.getStationsAndProviders(), listener);
    }


    public void getCategoriesWithSubcategories(WsRequestListener<CategoriesAndSubcategories> listener) {
        enqueueWithListener(mIWsDataProvider.getCategoriesWithSubcategories(), listener);
    }


    public void searchApi(String searchQuery, WsRequestListener<SearchResults> listener) {
        enqueueWithListener(mIWsDataProvider.searchApi(searchQuery), listener);
    }


    public void getRecommendedPrograms(long timestampStart, long timestampEnd, WsRequestListener<List<Recommended>> listener) {
        enqueueListWithListener(mIWsDataProvider.getRecommendedPrograms(timestampStart, timestampEnd), listener);
    }


    public void getProgramsFor7DaysForStation(int stationId, WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(mIWsDataProvider.getProgramsFor7DaysForStation(stationId), listener);
    }


    public void getProgramsFor24HoursForStation(int stationId, WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(mIWsDataProvider.getProgramsFor24HoursForStation(stationId), listener);
    }


    public void getProgramsFor24HoursForStation(int stationId,
                                                long timestamp,
                                                WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(mIWsDataProvider
                .getProgramsFor24HoursForStation(stationId, timestamp), listener);
    }


    public void getAllProgramsForPeriod(long fromTimestamp,
                                        long toTimestamp,
                                        WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(mIWsDataProvider
                .getAllProgramsForPeriod(fromTimestamp, toTimestamp), listener);
    }


    public void getProgramsForPeriodForStations(long fromTimestamp,
                                                long toTimestamp,
                                                List<Integer> ids,
                                                WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(
                mIWsDataProvider.getProgramsForPeriodForStations(
                        fromTimestamp,
                        toTimestamp,
                        IntegerListQueryFormatter.format(ids)),
                listener);
    }


    public void getProgramsForPeriodForStation(long fromTimestamp,
                                               long toTimestamp,
                                               int id,
                                               WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(
                mIWsDataProvider.getProgramsForPeriodForStations(
                        fromTimestamp,
                        toTimestamp,
                        String.valueOf(id)),
                listener);
    }


    public void getProgramsForPeriodForCategory(long fromTimestamp,
                                                long toTimestamp,
                                                String categoryId,
                                                WsRequestListener<List<Program>> listener) {
        enqueueListWithListener(
                mIWsDataProvider.getProgramsForPeriodForCategory(
                        fromTimestamp,
                        toTimestamp,
                        categoryId),
                listener);
    }


    public void getProgramForId(int programId, WsRequestListener<Program> listener) {
        enqueueWithListener(mIWsDataProvider.getProgramDetails(programId), listener);
    }


    public ITeleshowResponse<StationsAndProviders> getStationsAndProvidersSync() {
        return handleException(mIWsDataProvider.getStationsAndProviders());
    }


    public ITeleshowResponse<CategoriesAndSubcategories> getCategoriesWithSubcategoriesSync() {
        return handleException(mIWsDataProvider.getCategoriesWithSubcategories());
    }


    public ITeleshowResponse<SearchResults> searchApiSync(String searchQuery) {
        return handleException(mIWsDataProvider.searchApi(searchQuery));
    }


    public TeleshowListResponse<Recommended> getRecommendedProgramsSync(long timestampStart, long timestampEnd) {
        return handleListException(mIWsDataProvider.getRecommendedPrograms(timestampStart, timestampEnd));
    }


    public TeleshowListResponse<Program> getProgramsFor7DaysForStationSync(int stationId) {
        return handleListException(mIWsDataProvider.getProgramsFor7DaysForStation(stationId));
    }


    public TeleshowListResponse<Program> getProgramsFor24HoursForStationSync(int stationId) {
        return handleListException(mIWsDataProvider.getProgramsFor24HoursForStation(stationId));
    }


    public TeleshowListResponse<Program> getProgramsFor24HoursForStationSync(int stationId, long timestamp) {
        return handleListException(mIWsDataProvider.getProgramsFor24HoursForStation(stationId, timestamp));
    }


    public TeleshowListResponse<Program> getAllProgramsForPeriodSync(long fromTimestamp, long toTimestamp) {
        return handleListException(mIWsDataProvider.getAllProgramsForPeriod(fromTimestamp, toTimestamp));
    }


    public TeleshowListResponse<Program> getProgramsForPeriodForStationSync(long fromTimestamp,
                                                                            long toTimestamp,
                                                                            int id) {
        return handleListException(
                mIWsDataProvider.getProgramsForPeriodForStations(
                        fromTimestamp,
                        toTimestamp,
                        String.valueOf(id)));
    }


    public TeleshowListResponse<Program> getProgramsForPeriodForStationsSync(long fromTimestamp,
                                                                             long toTimestamp,
                                                                             List<Integer> ids) {
        return handleListException(
                mIWsDataProvider.getProgramsForPeriodForStations(
                        fromTimestamp,
                        toTimestamp,
                        IntegerListQueryFormatter.format(ids)));
    }


    public TeleshowListResponse<Program> getProgramsForPeriodForCategorySync(long fromTimestamp,
                                                                             long toTimestamp,
                                                                             String categoryId) {
        return handleListException(
                mIWsDataProvider.getProgramsForPeriodForCategory(
                        fromTimestamp,
                        toTimestamp,
                        categoryId));
    }

    // TODO make methods below more generic
    private <T extends IModelObject> TeleshowListResponse<T> handleListException(Call<List<T>> call) {
        try {
            return new TeleshowListResponse<T>(call.execute());
        } catch (IOException e) {
            e.printStackTrace();
            return new TeleshowListResponse<T>(e);
        }
    }

    private <T> TeleshowPrimitiveResponse<T> handlePrimitiveException(Call<T> call) {
        try {
            return new TeleshowPrimitiveResponse<T>(call.execute());
        } catch (IOException e) {
            e.printStackTrace();
            return new TeleshowPrimitiveResponse<T>(e);
        }
    }

    private <T extends IModelObject> ITeleshowResponse<T> handleException(Call<T> call) {
        try {
            return new TeleshowResponse<T>(call.execute());
        } catch (IOException e) {
            e.printStackTrace();
            return new TeleshowResponse<T>(e);
        }
    }

    private <T> void enqueuePrimitiveWithListener(Call<T> call,
                                                  final WsRequestListener<T> listener) {
        call.enqueue(new Callback<T>() {

            public void onResponse(Call<T> call, Response<T> response) {
                listener.onResult(new TeleshowPrimitiveResponse<T>(response));
            }


            public void onFailure(Call<T> call, Throwable t) {
                listener.onResult(new TeleshowPrimitiveResponse<T>(t));
            }
        });
    }

    private <T extends IModelObject> void enqueueWithListener(Call<T> call,
                                                              final WsRequestListener<T> listener) {
        call.enqueue(new Callback<T>() {

            public void onResponse(Call<T> call, Response<T> response) {
                listener.onResult(new TeleshowResponse<>(response));
            }


            public void onFailure(Call<T> call, Throwable t) {
                listener.onResult(new TeleshowResponse<T>(t));
            }
        });
    }

    private <T extends IModelObject> void enqueueListWithListener(Call<List<T>> call,
                                                                  final WsRequestListener<List<T>> listener) {
        call.enqueue(new Callback<List<T>>() {

            public void onResponse(Call<List<T>> call, Response<List<T>> response) {
                listener.onResult(new TeleshowListResponse<>(response));
            }


            public void onFailure(Call<List<T>> call, Throwable t) {
                listener.onResult(new TeleshowListResponse<T>(t));
            }
        });
    }

}
