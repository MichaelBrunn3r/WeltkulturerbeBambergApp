package com.github.wksb.wkebapp.utilities;

import java.util.Collections;
import java.util.List;

/**
 * Created by Admin on 06/12/2015.
 */
public class ListUtils {

    private ListUtils(){}

    public static <T> T getFirst(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    public static <T> T getLast(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(list.size()-1) : null;
    }

    public static <T> List<T> reversed(List<T> list) {
        Collections.reverse(list);
        return list;
    }
}