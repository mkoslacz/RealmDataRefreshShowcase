package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

/**
 * Created by mkoslacz on 23.03.16.
 * <p>
 * Used for db objects.
 */
public interface IModelObject {

    boolean isContentValid();

    /**
     * Binds contents of this container linking db relationships and purging data from invalid entries.
     * Should be called right after receiving this container and BEFORE passing it to upper
     * abstraction layer.
     *
     * @return this
     */
    IModelObject bind();

}
