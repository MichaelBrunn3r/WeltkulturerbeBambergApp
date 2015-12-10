package com.github.wksb.wkebapp.activity.navigation;

import android.content.Context;

import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Michael on 27.10.2015.
 */
public class RouteSegment {

    private Route route;
    private int fromWaypointID;
    private int toWaypointID;
    private PolylineOptions mNavigationPolyline;
    private RouteSegmentState mState;

    public RouteSegment(Route route, int fromWaypointID, int toWaypointID, PolylineOptions polyline) {
        this.route = route;
        this.fromWaypointID = fromWaypointID;
        this.toWaypointID = toWaypointID;
        this.mNavigationPolyline = polyline;
    }

    public Context getContext() {
        return route.getContext();
    }

    public int getDestinationWaypointId() {
        return toWaypointID;
    }

    public PolylineOptions getNavigationPolyline() {
        return mNavigationPolyline;
    }

    public void setState(RouteSegmentState newState) {
        this.mState = newState;
    }

    public boolean isNotStarted() {
        return mState == RouteSegmentState.NOT_STARTED;
    }

    public boolean isCompleted() {
        return mState == RouteSegmentState.COMPLETED;
    }

    public boolean isActive() {
        return mState == RouteSegmentState.ACTIVE;
    }

    public enum RouteSegmentState {
        NOT_STARTED,
        COMPLETED,
        ACTIVE
    }
}