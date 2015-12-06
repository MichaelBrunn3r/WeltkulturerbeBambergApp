package com.github.wksb.wkebapp.activity.navigation;

import android.content.Context;
import android.support.annotation.IntDef;

import com.github.wksb.wkebapp.utilities.ListUtils;
import com.google.android.gms.maps.GoogleMap;

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
    /** The default Value for the Length of the current Route, measured in Waypoints */
    static final int DEFAULT_ROUTE_LENGTH = 0;
    /** The default Value for Id of the current Quiz that has to be solved to progress in the current Route */
    static final int DEFAULT_CURRENT_QUIZ_ID = -1; // No Quiz should have a Quiz Id of -1

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

    public Route(Context context) {
        this.context = context;
        this.routeSegmentsList = new ArrayList<>();
        this.waypointList = new ArrayList<>();
        this.waypointOrderList = new ArrayList<>();
    }

    /**
     * Render this Route on a {@link GoogleMap}. All {@link RouteSegment}s that were visited up to this point will be
     * rendered and the currently active {@link RouteSegment} will be highlighted
     * @param googleMap The {@link GoogleMap} to render this Route on
     */
    public void renderOnMap(GoogleMap googleMap) {
        for (int i=getProgress(context); i>=0; i--) {
            getRouteSegmentAt(i).renderOnMap(googleMap);
        }
        getRouteSegmentAt(getProgress(context)).init(googleMap);
    }

    /**
     * Add a {@link RouteSegment} to this Route
     * @param routeSegment The {@link RouteSegment} to be added
     */
    public void addRouteSegment(RouteSegment routeSegment) {
        routeSegmentsList.add(routeSegment);
        for (int id : routeSegment.getWaypointIds()) {
            Waypoint newWaypoint = new Waypoint(id);
            newWaypoint.loadDataFromDatabase(context);
            addWaypoint(newWaypoint);
        }
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
     * Get a List of the {@link RouteSegment}s of this Route
     * @return A List containing all {@link RouteSegment}s of this Route
     */
    public List<RouteSegment> getRouteSegments() {
        return this.routeSegmentsList;
    }

    /**
     * Add a {@link Waypoint} to the this Route. The {@link Waypoint}s should be visited in the Order in which
     * they were added. If a {@link Waypoint} with the exact same Id already exists in this Route, the previously added
     * {@link Waypoint} will be used instead
     * @param newWaypoint
     */
    public void addWaypoint(Waypoint newWaypoint) {
        // Add
        if (waypointOrderList.isEmpty()) {
            waypointOrderList.add(newWaypoint.getId());
        } else if(ListUtils.getLast(waypointOrderList) == newWaypoint.getId()) {
            // Only add the Id, if it doesn't match the Id of the last Id in th List (you can't go from a Waypoint to the exact same Waypoint again)
            return;
        } else {
            waypointOrderList.add(newWaypoint.getId());
        }

        for (Waypoint waypoint : waypointList) {
            if (waypoint.getId() == newWaypoint.getId()) return; // Return if a Waypoint with the exact same Id already exists
        }
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
     * Set the length of the current Route. The length represents the RouteSegments in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @param length The length of the current Route
     */
    public static void setLength(Context context, int length) {
        context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).edit().putInt("ROUTE_LENGTH", length).commit();
    }

    /**
     * Get the length of the current Route. The length represents the RouteSegments in the current Route
     * @param context The Context associated with the SharedPreferences containing the Progress
     * @return The length of the current Route
     */
    public static int getLength(Context context) {
        return context.getSharedPreferences("TOUR", Context.MODE_PRIVATE).getInt("ROUTE_LENGTH", DEFAULT_ROUTE_LENGTH);
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
        setLength(context, DEFAULT_ROUTE_LENGTH);
        setProgressState(context, DEFAULT_IS_IN_PROGRESS);
        setCurrentQuizId(context, DEFAULT_CURRENT_QUIZ_ID);
    }
}