package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses;

import com.mateuszkoslacz.realmdatarefreshshowcase.exceptions.NetworkingError;

import retrofit2.Response;

/**
 * Created by mkoslacz on 18.04.16.
 */
public abstract class ATeleshowResponse<T> implements ITeleshowResponse<T> {

    protected T body;
    private int status;
    private Response<T> response;
    private Throwable error;

    public ATeleshowResponse(Response<T> response) {
        this.response = response;
        if (response.isSuccessful()) {
            status = bindAndGetBodyObjectStatus(response);
        } else {
            status = NON_200_RESPONSE_CODE;
        }
    }

    public ATeleshowResponse(Throwable error) {
        this.error = error;
        status = THROWABLE;
    }

    protected abstract int bindAndGetBodyObjectStatus(Response response);

    @Override
    public int getResponseCode() {
        return response != null ? response.code() : -1;
    }

    @Override
    public int getNetworkResponseCode() {
        return (response != null && response.raw() != null && response.raw().networkResponse() != null) ? response.raw().networkResponse().code() : getResponseCode();
    }

    @Override
    public T getBody() {
        if (status != SUCCESS) {
            throw new NetworkingError(error, this);
        }
        return body;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public Throwable getError() {
        return error;
    }

    @Override
    public boolean isSuccessful() {
        return status == SUCCESS;
    }
}
