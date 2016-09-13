package com.mateuszkoslacz.realmdatarefreshshowcase.exceptions;


import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.ITeleshowResponse;

/**
 * Created by mkoslacz on 18.04.16.
 */
public class NetworkingError extends RuntimeException {

    ITeleshowResponse response;

    public NetworkingError(Throwable throwable, ITeleshowResponse response) {
        super(throwable);
        this.response = response;
    }

    public NetworkingError(ITeleshowResponse response) {
        this.response = response;
    }

    @Override
    public String getMessage() {
        return String.format("Status : %d, code: %d", response.getStatus(), response.getResponseCode());
    }
}
