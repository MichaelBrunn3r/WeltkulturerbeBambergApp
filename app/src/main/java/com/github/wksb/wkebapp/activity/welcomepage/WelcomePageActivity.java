package com.github.wksb.wkebapp.activity.welcomepage;

import android.app.LoaderManager;
import android.content.Loader;
import android.graphics.Typeface;
import android.os.Bundle;

import com.github.wksb.wkebapp.InformationAsyncTaskLoader;
import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.RouteSegmentsAsyncTaskLoader;
import com.github.wksb.wkebapp.RoutesAsyncTaskLoader;
import com.github.wksb.wkebapp.WaypointsAsyncTaskLoader;
import com.github.wksb.wkebapp.activity.quiz.QuizActivity;
import com.github.wksb.wkebapp.activity.navigation.Route;
import com.github.wksb.wkebapp.utilities.DebugUtils;
import com.github.wksb.wkebapp.QuizzesAsyncTaskLoader;
import com.github.wksb.wkebapp.Settings;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * This activity is the launch activity of the World-Heritage-Application
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */
public class WelcomePageActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks{

    /**
     * The state of this Activity. The Behaviour of this Activity changes with its current state
     */
    private WelcomePageActivityState activityState;

    /** LOADER-ID of {@link QuizzesAsyncTaskLoader} **/
    private static final int QUIZZES_LOADER_ID = 0;
    /** LOADER-ID of {@link InformationAsyncTaskLoader} **/
    private static final int INFORMATION_LOADER_ID = 1;
    /** LOADER-ID of {@link RoutesAsyncTaskLoader} **/
    private static final int ROUTES_LOADER_ID = 2;
    /** LOADER-ID of {@link WaypointsAsyncTaskLoader} **/
    private static final int WAYPOINTS_LOADER_ID = 3;
    /** LOADER-ID of {@link RouteSegmentsAsyncTaskLoader} **/
    private static final int ROUTE_SEGMENTS_LOADER_ID = 4;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Settings.isFirstAppLaunch(this)) {
            onFirstLaunch();
            Settings.setIsFirstLaunch(this, false);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (QuizActivity.nextQuizIsInUnlocked()) {
            setActivityState(new QuizInProgress(this));
        } else if (Route.get().isInProgress()) {
            setActivityState(new TourInProgress(this));
        } else {
            setActivityState(new NoTourInProgress(this));
        }

        activityState.onActivityStart();

        //TODO Remove or improve
        ((TextView)findViewById(R.id.textview_welcome_header)).setTypeface(Typeface.createFromAsset(getAssets(), "fonts/hanged_letters.ttf"));
    }

    /**
     * Set the state of this Activity
     * @param state The new state of this Activity
     */
    public void setActivityState(WelcomePageActivityState state) {
        activityState = state;
        activityState.initState(); // Initialise state
    }

    /**
     * Logic executed on first Launch of this Application
     */
    private void onFirstLaunch() {
        // Initialise Loaders
        getLoaderManager().initLoader(WAYPOINTS_LOADER_ID, null, this);
        getLoaderManager().initLoader(ROUTES_LOADER_ID, null, this);
        getLoaderManager().initLoader(QUIZZES_LOADER_ID, null, this);
        getLoaderManager().initLoader(INFORMATION_LOADER_ID, null, this);
        getLoaderManager().initLoader(ROUTE_SEGMENTS_LOADER_ID, null, this);
    }

    @Override
    public Loader onCreateLoader(int id, Bundle args) {
        switch (id) {
            case ROUTES_LOADER_ID:
                // Return new RoutesAsyncTaskLoader to load routes in the database
                DebugUtils.toast("Loading Routes in Database ...");
                return new RoutesAsyncTaskLoader(this);
            case QUIZZES_LOADER_ID:
                // Return new QuizzesAsyncTaskLoader to load quizzes in the database
                DebugUtils.toast("Loading Quizzes in Database ...");
                return new QuizzesAsyncTaskLoader(this);
            case WAYPOINTS_LOADER_ID:
                // Return new WaypointsAsyncTaskLoader to load waypoints in the database
                DebugUtils.toast("Loading Waypoints in Database ...");
                return new WaypointsAsyncTaskLoader(this);
            case INFORMATION_LOADER_ID:
                // Return new InformationAsyncTaskLoader to load the information about the waypoints in the database
                DebugUtils.toast("Loading Information about the Waypoints in the Database ...");
                return new InformationAsyncTaskLoader(this);
            case ROUTE_SEGMENTS_LOADER_ID:
                // Return new RouteSegmentsAsyncTaskLoader to load the rout-segments
                DebugUtils.toast("Loading Route Segments in Database ...");
                return new RouteSegmentsAsyncTaskLoader(this);
            default:
                // There is no such Loader ID -> throw Exception
                throw new IllegalArgumentException("Loader not found. ID: " + id);
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {
        //Not used
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        switch (loader.getId()) {
            case ROUTES_LOADER_ID:
                DebugUtils.toast("Routes loaded");
                getLoaderManager().destroyLoader(ROUTES_LOADER_ID);
                break;
            case QUIZZES_LOADER_ID:
                DebugUtils.toast("Quizzes loaded");
                getLoaderManager().destroyLoader(QUIZZES_LOADER_ID);
                break;
            case WAYPOINTS_LOADER_ID:
                DebugUtils.toast("Waypoints loaded");
                getLoaderManager().destroyLoader(WAYPOINTS_LOADER_ID);
                break;
            case INFORMATION_LOADER_ID:
                DebugUtils.toast("Information about Waypoints loaded");
                getLoaderManager().destroyLoader(INFORMATION_LOADER_ID);
                break;
            case ROUTE_SEGMENTS_LOADER_ID:
                DebugUtils.toast("Route Segments loaded");
                getLoaderManager().destroyLoader(ROUTE_SEGMENTS_LOADER_ID);
                break;
        }
    }

    //TODO Documentation
    public void onBtnClickedStart(View view) {
        activityState.onBtnClickedStart(view);
    }

    //TODO Documentation
    public void onBtnClickedContinue(View view) {
        activityState.onBtnClickedContinue(view);
    }

    public void onBtnClickedAbout(View view) {
        activityState.onBtnClickedAbout(view);
    }

    public void onBtnClickedRestartTour(View view) {
        activityState.onBtnClickedRestartTour(view);
    }
}