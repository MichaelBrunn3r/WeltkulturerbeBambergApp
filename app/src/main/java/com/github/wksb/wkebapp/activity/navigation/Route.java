package com.github.wksb.wkebapp.activity.navigation;

import android.content.Context;
import android.content.SharedPreferences;

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

    // Default values
    static final String DEFAULT_ROUTE_NAME = "NO_ROUTE";
    static final int DEFAULT_ROUTE_PROGRESS = 1;
    static final boolean DEFAULT_IS_IN_PROGRESS = false;
    static final int DEFAULT_ROUTE_LENGTH = 0;
    static final int DEFAULT_CURRENT_QUIZ_ID = -1;

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