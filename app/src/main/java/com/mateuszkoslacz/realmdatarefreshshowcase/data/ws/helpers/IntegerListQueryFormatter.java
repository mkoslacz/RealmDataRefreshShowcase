package com.mateuszkoslacz.realmdatarefreshshowcase.data.ws.helpers;

import java.util.List;

/**
 * Created by mkoslacz on 21.03.16.
 */
public class IntegerListQueryFormatter {

    public static String format(List<Integer> integers) {
        if (integers == null || integers.isEmpty()) {
            return "";
        }

        String listString = integers.toString();
        return listString.substring(1, listString.length() - 1).replace(" ", "");
    }
}
