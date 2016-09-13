package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.listeners;


import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.responses.ITeleshowResponse;

/**
 * Created by mkoslacz on 22.03.16.
 * <p>
 * This interface allows us change our ws implementation
 * in easy way.
 */
public interface WsRequestListener<T> {

    void onResult(ITeleshowResponse<T> result);
}
