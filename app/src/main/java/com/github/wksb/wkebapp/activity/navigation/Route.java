package com.github.wksb.wkebapp.activity.navigation;

import java.util.ArrayList;
import java.util.List;

/**
 * This class manages a collection of {@link RouteSegment}s
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */
public class Route {
    private final String name;
    private final List<RouteSegment> routeSegmentsList;

    public Route(String name) {
        this.name = name;
        this.routeSegmentsList = new ArrayList<>();
    }

    /**
     * Add a {@link RouteSegment} to this Route
     * @param routeSegment The {@link RouteSegment} to be added
     */
    public void addRouteSegment(RouteSegment routeSegment) {
        routeSegmentsList.add(routeSegment);
    }

    /**
     * Get a List of the {@link RouteSegment}s of this Route
     * @return A List containing all {@link RouteSegment}s of this Route
     */
    public List<RouteSegment> getRouteSegments() {
        return this.routeSegmentsList;
    }

    /**
     * Get the {@link RouteSegment} at a specific position in the List
     * @param position The position of the {@link RouteSegment}
     * @return The {@link RouteSegment} at the specific position
     */
    public RouteSegment getRouteSegmentAt(int position) {
        return this.routeSegmentsList.get(position);
    }

    /**
     * Get the the number of {@link RouteSegment} in this Route
     * @return The number of {@link RouteSegment} in this Route
     */
    public int getRouteSegmentAmount() {
        return this.routeSegmentsList.size();
    }
}
