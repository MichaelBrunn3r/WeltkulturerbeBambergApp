package com.github.wksb.wkebapp.utilities;

import java.util.Collections;
import java.util.List;

/**
 * This Utility Class offers some methods to help working with {@link List}s
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-12-06
 */
public class ListUtils {

    private ListUtils(){}

    /**
     * Get the first Item of a List
     * @param list The List you want to get the first Item from
     * @return The first Item in the List
     */
    public static <T> T getFirst(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(0) : null;
    }

    /**
     * Get the last Item of a List
     * @param list The List you want to get the last Item from
     * @return The last Item of the List
     */
    public static <T> T getLast(List<T> list) {
        return list != null && !list.isEmpty() ? list.get(list.size()-1) : null;
    }

    /**
     * Get a the passed List with its Items in reversed Order
     * @param list The List you want o receive with its Items in reversed Order
     * @return The List with its Items in reversed Order
     */
    public static <T> List<T> reversed(List<T> list) {
        Collections.reverse(list); // Reverse the Items in the List
        return list;
    }
}