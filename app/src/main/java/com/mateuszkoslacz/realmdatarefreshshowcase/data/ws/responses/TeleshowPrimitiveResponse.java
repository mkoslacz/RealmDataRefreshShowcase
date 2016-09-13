package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses;

import retrofit2.Response;

/**
 * Created by mkoslacz on 18.04.16.
 */
public class TeleshowPrimitiveResponse<T> extends ATeleshowResponse<T> implements ITeleshowResponse<T> {

    public TeleshowPrimitiveResponse(Response<T> response) {
        super(response);
    }

    public TeleshowPrimitiveResponse(Throwable error) {
        super(error);
    }

    @Override
    protected int bindAndGetBodyObjectStatus(Response response) {
        if (response.body() != null) {
            body = (T) response.body();
            return SUCCESS;
        } else {
            return INVALID_OBJECT;
        }
    }


}
