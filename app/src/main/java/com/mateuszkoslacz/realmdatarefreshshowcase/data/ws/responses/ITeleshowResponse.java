package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses;

/**
 * Created by mkoslacz on 21.04.16.
 */
public interface ITeleshowResponse<T> {

    int SUCCESS = 0;
    int THROWABLE = 1;
    int NON_200_RESPONSE_CODE = 2;
    int INVALID_OBJECT = 3;

    int getResponseCode();

    int getNetworkResponseCode();

    T getBody();

    int getStatus();

    Throwable getError();

    boolean isSuccessful();
}
