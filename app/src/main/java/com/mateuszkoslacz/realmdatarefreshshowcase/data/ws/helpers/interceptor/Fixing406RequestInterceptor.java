package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.interceptor;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * Automatically adds Accept application/json header without which api returns 406
 */
public class Fixing406RequestInterceptor implements Interceptor {

    @Override
    public Response intercept(Interceptor.Chain chain) throws IOException {
        Request request = chain.request();
        Request newRequest;

        newRequest = request.newBuilder()
                .addHeader("Accept", "application/json")
                .build();
        return chain.proceed(newRequest);
    }
}
