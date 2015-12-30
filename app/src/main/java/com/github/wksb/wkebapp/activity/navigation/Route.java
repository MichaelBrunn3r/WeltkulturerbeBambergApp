package com.github.wksb.wkebapp.activity.navigation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.support.annotation.IntDef;

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.utilities.DebugUtils;
import com.github.wksb.wkebapp.utilities.ListUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
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

    // TODO Description. Used to only accept DESTINATION_TO_START and START_TO_DESTINATION as Parameters in Methods
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({DESTINATION_TO_START, START_TO_DESTINATION})
    public @interface WaypointOrder{}

    // Values used to determine the Order of th Waypoints
    /** The Waypoints should be ordered from the Start to the Destination */
    static final int START_TO_DESTINATION = 1;
    /** The Waypoints should be ordered from the Destination to the Start */
    static final int DESTINATION_TO_START = 2;

    // Default values
    /** The default Value for the Name of the current Route */
    static final String DEFAULT_ROUTE_NAME = "NO_ROUTE";
    /** The default Value for the Progress of the current Route */
    static final int DEFAULT_ROUTE_PROGRESS = 0;
    /** Default for the boolean that determines if the current Route is in progress */
    static final boolean DEFAULT_IS_IN_PROGRESS = false;
    /** The default Value for Id of the current Quiz that has to be solved to progress in the current Route */
    static final int DEFAULT_CURRENT_QUIZ_ID = -1; // No Quiz should have an Id of -1

    /** The Context of this Route */
    private Context context;
    /** The List of {@link RouteSegment}s in this Route */
    private final List<RouteSegment> routeSegmentsList;
    /** This List is a central Collection of all {@link Waypoint}s that will be visited in the current Route.
     * All {@link RouteSegment}s in the current Route use this List as a Reference to their {@link Waypoint}s */
    private final List<Waypoint> waypointList;
    /** This List contains the Ids of the {@link Waypoint}s that will be visited in the current Route, ordered from
     * the Start to the Destination */
    private final List<Integer> waypointOrderList;

    private List<Marker> mMarkerList;
    private List<Polyline> mNavigationPolylineList;
    private RouteSegment mActiveRouteSegment;
    private Waypoint mCurrentDestinationWaypoint;

    public Route(Context context) {
        this.context = context;
        this.routeSegmentsList = new ArrayList<>();
        this.waypointList = new ArrayList<>();
        this.waypointOrderList = new ArrayList<>();
        this.mMarkerList = new ArrayList<>();
        this.mNavigationPolylineList = new ArrayList<>();
    }

    /**
     * Render this Route on a {@link GoogleMap}. All {@link RouteSegment}s that were visited up to this point will be
     * rendered and the currently active {@link RouteSegment} will be highlighted
     * @param googleMap The {@link GoogleMap} to render this Route on
     */
    public void renderOnMap(GoogleMap googleMap) {
        renderMarkersOnMap(googleMap);
        renderNavigationPolylinesOnMap(googleMap);

        // Zoom to the Route Segment on the Map
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                        mCurrentDestinationWaypoint.getLatitude(),
                        mCurrentDestinationWaypoint.getLongitude()), 16));
    }

    private void renderMarkersOnMap(GoogleMap googleMap) {
        // Remove all Markers of this Route the Map
        for (Marker marker : mMarkerList) marker.remove();
        mMarkerList.clear();

        // Add the Markers to the Map
        for (Waypoint waypoint : waypointList) {
            if (!waypoint.isNotVisited()) { // Only add a Marker, if the Waypoint is visited or the current Position
                MarkerOptions waypointMarker = new MarkerOptions()
                        .title(waypoint.getName())
                        .position(new LatLng(waypoint.getLatitude(), waypoint.getLongitude()));

                // If the Waypoint is the current Position, give it a special Icon
                if (waypoint.isCurrentDestination()) {
                    Drawable iconDrawable = getContext().getResources().getDrawable(R.drawable.ic_marker_current_position);
                    Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
                    Canvas canvas = new Canvas(iconBitmap);
                    iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
                    iconDrawable.draw(canvas);
                    waypointMarker.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
                }

                mMarkerList.add(googleMap.addMarker(waypointMarker));
            }
        }
    }

    private void renderNavigationPolylinesOnMap(GoogleMap googleMap) {
        // Remove all Polylines of this Route from the Map
        for (Polyline polyline : mNavigationPolylineList) polyline.remove();
        mNavigationPolylineList.clear();

        // Add the Polylines to the Map
        for (RouteSegment routeSegment : routeSegmentsList) {
            if (routeSegment.isCompleted() || routeSegment.isActive()) {
                PolylineOptions navigationPolyline = routeSegment.getNavigationPolyline();

                // If the RouteSegment is active, the Color of the Polyline is the PrimaryColor, otherwise its Color is the SecondaryTextColor
                if (routeSegment.isActive()) navigationPolyline.color(getContext().getResources().getColor(R.color.PrimaryColor));
                else navigationPolyline.color(getContext().getResources().getColor(R.color.BackgroundTextAndIconsColorLight));

                // Finally add the Polyline to the Map
                mNavigationPolylineList.add(googleMap.addPolyline(navigationPolyline));
            }
        }
    }

    private void addCurrentDestinationProximityAlert() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Activity.LOCATION_SERVICE);
        Intent proximityAlert = new Intent();
        proximityAlert.setAction("com.github.weltkulturschnitzelbamberg.weltkulturerbebambergapp.PROXIMITY_ALERT");
        proximityAlert.putExtra("waypoint-name", mCurrentDestinationWaypoint.getName());
        proximityAlert.putExtra("quiz-id", mCurrentDestinationWaypoint.getQuizId());

        int detectionRadius = 40; // The radius around the central point in which to send an proximity alert
        int expirationTime = -1; // The time in milliseconds it takes this proximity alert to expire (-1 = no expiration)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, proximityAlert, PendingIntent.FLAG_UPDATE_CURRENT); // TODO Previous Proximity Alerts have to be removed

        locationManager.addProximityAlert(mCurrentDestinationWaypoint.getLatitude(), mCurrentDestinationWaypoint.getLongitude(), detectionRadius, expirationTime, pendingIntent);
    }

    /**
     * Add a {@link RouteSegment} to this Route
     * @param fromWaypointId The Id of the Start {@link Waypoint}
     * @param toWaypointId The Id of the Destination {@link Waypoint}
     * @param polyline The Navigation {@link PolylineOptions} of the {@link RouteSegment}
     */
    public void addRouteSegment(int fromWaypointId, int toWaypointId, PolylineOptions polyline) {
        routeSegmentsList.add(new RouteSegment(this, fromWaypointId, toWaypointId, polyline));

        if (!waypointOrderList.contains(fromWaypointId)) {
            Waypoint fromWaypoint = new Waypoint(fromWaypointId);
            fromWaypoint.loadDataFromDatabase(getContext());
            addWaypoint(fromWaypoint);
        }

        if (!waypointOrderList.contains(toWaypointId)) {
            Waypoint toWaypoint = new Waypoint(toWaypointId);
            toWaypoint.loadDataFromDatabase(getContext());
            addWaypoint(toWaypoint);
        }
    }

    /**
     * Get a List of the {@link RouteSegment}s of this Route
     * @return A List containing all {@link RouteSegment}s of this Route
     */
    public List<RouteSegment> getRouteSegments() {
        return this.routeSegmentsList;
    }

    /**
     * Add a {@link Waypoint} to the this Route. The {@link Waypoint}s should be visited in the Order in which
     * they were added
     * @param newWaypoint
     */
    public void addWaypoint(Waypoint newWaypoint) {
        if (waypointOrderList.contains(newWaypoint.getId())) return;

        waypointOrderList.add(newWaypoint.getId());
        waypointList.add(newWaypoint);
    }

    /**
     * Get the {@link Waypoint} with the passed Id. If the {@link Waypoint} is not visited in the current Route
     * (--> is no Part of the current Route) this Method returns null
     * @param id The Id of the {@link Waypoint} you want to get
     * @return The {@link Waypoint} with the passed Id. If the {@link Waypoint} is not visited in the current Route
     * (--> is no Part of the current Route) this Method returns null
     */
    public Waypoint getWaypointById(int id) {
        for (Waypoint waypoint : waypointList) {
            if (waypoint.getId() == id) return waypoint;
        }
        return null;
    }

    public Waypoint getWaypointAt(int position) {
        int id = waypointOrderList.get(position);
        return getWaypointById(id);
    }

    /** Get the {@link Waypoint}s that will be visited in this Route
     * @return The List of {@link Waypoint}s that will be visited in this Route
     */
    public List<Waypoint> getWaypoints() {
        return waypointList;
    }

    /** Get the {@link Waypoint}s that will be visited in this Route in a specific Order
     * @param waypointOrder Determines in which Order the {@link Waypoint}s will be returned
     * @return The List of {@link Waypoint}s that will be visited in this Route
     */
    public List<Waypoint> getWaypointsInOrder(@WaypointOrder int waypointOrder) {
        List<Waypoint> result = new ArrayList<>(waypointList.size());
        if (waypointOrder == START_TO_DESTINATION) {
            for (int id : waypointOrderList) {
                result.add(getWaypointById(id));
            }
        } else if (waypointOrder == DESTINATION_TO_START) {
            for (int id : ListUtils.reversed(waypointOrderList)) {
                result.add(getWaypointById(id));
            }
        }
        return result;
    }

    public void syncWithProgress() {
        for (Waypoint waypoint : getWaypoints()) {
            if (waypointOrderList.indexOf(waypoint.getId()) <= getProgress(getContext()))
                waypoint.setState(Waypoint.WaypointState.VISITED);
        }

        for (RouteSegment segment : getRouteSegments()) {
            if (routeSegmentsList.indexOf(segment) < getProgress(getContext()))
                segment.setState(RouteSegment.RouteSegmentState.COMPLETED);
            else if (routeSegmentsList.indexOf(segment) == getProgress(getContext())) {
                segment.setState(RouteSegment.RouteSegmentState.ACTIVE);
                mActiveRouteSegment = segment;
            }
        }

        mCurrentDestinationWaypoint = getWaypointById(mActiveRouteSegment.getDestinationWaypointId());
        mCurrentDestinationWaypoint.setState(Waypoint.WaypointState.CURRENT_DESTINATION);
        // Set the Id of the Quiz that has to be solved next to progress in this Route to the QuizId of the current Destination Waypoint
        setCurrentQuizId(getContext(), mCurrentDestinationWaypoint.getQuizId());

        addCurrentDestinationProximityAlert();
    }

    /**
     * Get the {@link Context} of this Route
     * @return The {@link Context} of this Route
     */
    public Context getContext() {
        return context;
    }

    /**
     * Set the Users' Progress in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @param progress The Progress of the User
     */
    public static void setProgress(Context context, int progress) {
        context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).edit().putInt("PROGRESS", progress).commit();
    }

    /**
     * Get the Users' Progress in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @return The Users' Progress in the current Route
     */
    public static int getProgress(Context context) {
        return context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).getInt("PROGRESS", DEFAULT_ROUTE_PROGRESS);
    }

    /**
     * Set the Name of the current Name. The name will be used in the SQL Query, so make sure it matches exactly the Name of an existing Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @param name The Name of the current Route
     */
    public static void setName(Context context, String name) {
        context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).edit().putString("ROUTE_NAME", name).commit();
    }

    /**
     * Get the Name of the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @return The Name of the current Route
     */
    public static String getName(Context context) {
        return context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).getString("ROUTE_NAME", DEFAULT_ROUTE_NAME);
    }

    /**
     * Set the State for the current Route. The Route can be either in Progress or not
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @param isInProgress The State of the current Route
     */
    public static void setProgressState(Context context, boolean isInProgress) {
        context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).edit().putBoolean("IS_IN_PROGRESS", isInProgress).commit();
    }

    /**
     * Check if the current Route is in Progress
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @return true if the current Route is in Progress, false otherwise
     */
    public static boolean isInProgress(Context context) {
        return context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).getBoolean("IS_IN_PROGRESS", DEFAULT_IS_IN_PROGRESS);
    }

    /**
     * Get the length of the current Route
     * @return The length of the current Route
     */
    public int getLength() {
        return waypointOrderList.size();
    }

    /**
     * Set the QuizId of the Quiz that has to be solved next to make Progress in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @param quizId The QuizId of the Quiz that has to be solved next to make Progress in the current Route
     */
    public static void setCurrentQuizId(Context context, int quizId) {
        context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).edit().putInt("CURRENT_QUIZ_ID", quizId).commit();
    }

    /**
     * Get the QuizId of the Quiz that has to be solved next to make Progress in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @return
     */
    public static int getCurrentQuizId(Context context) {
        return context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).getInt("CURRENT_QUIZ_ID", DEFAULT_CURRENT_QUIZ_ID);
    }

    /**
     * Reset the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     */
    public static void reset(Context context) {
        setProgress(context, DEFAULT_ROUTE_PROGRESS);
        setName(context, DEFAULT_ROUTE_NAME);
        setProgressState(context, DEFAULT_IS_IN_PROGRESS);
        setCurrentQuizId(context, DEFAULT_CURRENT_QUIZ_ID);
    }
}