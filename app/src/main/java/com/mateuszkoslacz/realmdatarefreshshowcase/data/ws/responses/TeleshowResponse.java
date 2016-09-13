package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IModelObject;

import retrofit2.Response;

/**
 * Created by mkoslacz on 18.04.16.
 */
public class TeleshowResponse<T extends IModelObject> extends ATeleshowResponse<T> implements ITeleshowResponse<T> {

    public TeleshowResponse(Response<T> response) {
        super(response);
    }

    public TeleshowResponse(Throwable error) {
        super(error);
    }

    @Override
    protected int bindAndGetBodyObjectStatus(Response response) {
        if (((T) response.body()).isContentValid()) {
            body = ((T) ((T) response.body()).bind()); // this is kinda crappy
            return SUCCESS;
        } else {
            return INVALID_OBJECT;
        }
    }


}
