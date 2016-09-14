package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws;

import android.content.Context;

import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.CategoriesAndSubcategories;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IModelObject;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.Program;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.StationsAndProviders;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.helpers.RealmGsonDeserializerFactoryForRetrofit2;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.interceptor.Fixing406RequestInterceptor;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.ITeleshowResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowListResponse;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.TeleshowResponse;

import java.io.IOException;
import java.util.List;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;

/**
 * Created by klewandowski on 2016-03-09.
 * Class which will handle calls and adapters
 * connected with webservice
 */
public class WsDataProvider {

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
    }

    private static Cache createHttpClientCache(Context context) {
        return new Cache(context.getCacheDir(), HTTP_CACHE_SIZE);
    }

    public ITeleshowResponse<StationsAndProviders> getStationsAndProvidersSync() {
        return handleException(mIWsDataProvider.getStationsAndProviders());
    }


    public ITeleshowResponse<CategoriesAndSubcategories> getCategoriesWithSubcategoriesSync() {
        return handleException(mIWsDataProvider.getCategoriesWithSubcategories());
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

    private <T extends IModelObject> TeleshowListResponse<T> handleListException(Call<List<T>> call) {
        try {
            return new TeleshowListResponse<T>(call.execute());
        } catch (IOException e) {
            e.printStackTrace();
            return new TeleshowListResponse<T>(e);
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
}
