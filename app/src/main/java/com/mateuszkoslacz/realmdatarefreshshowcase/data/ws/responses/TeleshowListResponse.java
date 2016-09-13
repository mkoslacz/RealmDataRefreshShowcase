package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IModelObject;
import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.InvalidEntriesRemover;

import java.util.List;

import retrofit2.Response;

/**
 * Created by mkoslacz on 18.04.16.
 */
public class TeleshowListResponse<T extends IModelObject> extends ATeleshowResponse<List<T>> implements ITeleshowResponse<List<T>> {

    public TeleshowListResponse(Response<List<T>> response) {
        super(response);
    }

    public TeleshowListResponse(Throwable error) {
        super(error);
    }

    @Override
    protected int bindAndGetBodyObjectStatus(Response response) {
        body = InvalidEntriesRemover.processList((List<T>) response.body());
        for (T element :
                body) {
            element.bind();
        }
        if (!body.isEmpty()) {
            return SUCCESS;
        } else {
            return INVALID_OBJECT;
        }
    }


}
