package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.model.IModelObject;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;


/**
 * Created by mkoslacz on 23.03.16.
 */
public class InvalidEntriesRemover {

    public static <T extends IModelObject> List<T> processList(List<T> objects) {
        if (objects == null) {
            objects = new ArrayList<>();
            return objects;
        }
        ListIterator<T> iterator = objects.listIterator();
        while (iterator.hasNext()) {
            if (!iterator.next().isContentValid()) {
                iterator.remove();
            }
        }
        return objects;
    }
}
