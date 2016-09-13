package com.mateuszkoslacz.realmdatarefreshshowcase.data.model;

import com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers.InvalidEntriesRemover;

import java.util.List;
import java.util.ListIterator;

/**
 * Created by mkoslacz on 18.03.16.
 * <p>
 * Api response only - not saved to db like this
 */
public class CategoriesAndSubcategories implements IModelObject {

    List<Category> categories;

    public CategoriesAndSubcategories() { // mandatory for correct gson deserialisation
    }

    public List<Category> getCategories() {
        return categories;
    }

    @Override
    public boolean isContentValid() {
        return true;
    }

    @Override
    public CategoriesAndSubcategories bind() {
        ListIterator<Category> iterator = categories.listIterator();
        while (iterator.hasNext()) {
            Category boundCategory = (Category) iterator.next().bind();
            iterator.set(boundCategory);
        }
        InvalidEntriesRemover.processList(categories);
        return this;
    }
}

