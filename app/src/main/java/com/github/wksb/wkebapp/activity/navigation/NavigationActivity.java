package com.github.wksb.wkebapp.activity.navigation;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.github.wksb.wkebapp.Edge;
import com.github.wksb.wkebapp.ProximityAlertReceiver;
import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.CollapsableView;
import com.github.wksb.wkebapp.activity.quiz.QuizActivity;
import com.github.wksb.wkebapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.wksb.wkebapp.database.RouteSegmentsTable;
import com.github.wksb.wkebapp.database.RoutesTable;
import com.github.wksb.wkebapp.utilities.DebugUtils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * This activity shows a GoogleMaps map on which a route between to waypoints is shown.
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */

public class NavigationActivity extends AppCompatActivity {

    public static final String ACTION_ARRIVED_AT_WAYPOINT = "com.github.wksb.wkebapp.ARRIVED_AT_WAYPOINT";
    public static final String TAG_QUIZ_ID = "quiz_id";
    public static final String TAG_WAYPOINT_NAME = "wapoint_name";

    // Title of the ActionBar
    private TextView mTextViewActionbarTitle;

    // The Google Maps Fragment
    private GoogleMap mMap;

    // Components of the Navigation Drawer
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRvRouteList;
    private RouteAdapter mRouteAdapter;
    private ActionBarDrawerToggle mDrawerToggle;  // Button in ActionBar toggling the DrawerLayout

    private CollapsableView mStartQuiz;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Set up the Action Bar
        setUpActionBar();

        mStartQuiz = (CollapsableView)findViewById(R.id.collapsableview_navigation_start_quiz);
    }

    @Override
     protected void onStart() {
        super.onStart();

        // Set up Route and Map if they don't exist
        if (mMap == null) setUpMap();

        if (!Route.get().isInitialised()) Route.get().init();

        // Set up the Navigation Drawer
        setUpDrawer();

        mMap.clear();
        Route.get().renderOnMap(mMap);

        // TODO Change the Design of the Progress Bar
        mTextViewActionbarTitle.setText(String.format("Progress: %d / %d", Route.get().getProgress(), Route.get().getRouteSegments().size()));
    }

    @Override
    protected void onResume() {
        super.onResume();

        // Register the ArrivedAtWaypointReceiver to receive
        LocalBroadcastManager.getInstance(this).registerReceiver(ArrivedAtWaypointReceiver,
                new IntentFilter(ACTION_ARRIVED_AT_WAYPOINT));
    }

    @Override
    protected void onPause() {
        // Unregister the ArrivedAtWaypointReceiver since the Activity is not visible
        LocalBroadcastManager.getInstance(this).unregisterReceiver(ArrivedAtWaypointReceiver);
        super.onPause();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Synchronize the Drawer Toggle Button with the Navigation Drawer
        mDrawerToggle.syncState(); // Sync State, for example after switching from Landscape to Portrait Mode
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mDrawerLayout.isDrawerOpen(GravityCompat.START)) { // Don't show the OptionsMenu when the Drawer is opened (-> Hide when opened, show when closed)
            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_navigation, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Check if the Drawer Toggle Button was pressed
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        switch (item.getItemId()) {
            case R.id.action_start_next_quiz:

                Intent proximityAlert = new Intent();
                proximityAlert.setAction(ProximityAlertReceiver.ACTION_PROXIMITY_ALERT);
                proximityAlert.putExtra(ProximityAlertReceiver.TAG_WAYPOINT_NAME, Route.get().getCurrentDestinationWaypoint().getName());
                proximityAlert.putExtra(ProximityAlertReceiver.TAG_QUIZ_ID, Route.get().getCurrentDestinationWaypoint().getQuizId());
                proximityAlert.putExtra(LocationManager.KEY_PROXIMITY_ENTERING, true);

                sendBroadcast(proximityAlert);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() { //TODO Don't use the BackStack
        // Close the DrawerLayout if it is opened
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawers();
        } else {
            NavUtils.navigateUpFromSameTask(this);
        }
    }

    private void setUpActionBar() {
        Toolbar actionBar = (Toolbar) findViewById(R.id.actionbar);
        setSupportActionBar(actionBar);

        if (getSupportActionBar() != null) {
            // Use Custom ActionBar Layout and Display BackButton
            getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

            // Set Custom ActionBar Layout
            getSupportActionBar().setCustomView(R.layout.actionbar_title);
        } else {
            DebugUtils.toast(this, "Error while loading the SupportActionbar");
        }

        mTextViewActionbarTitle = (TextView) findViewById(R.id.textview_actionbar_title);
    }

    private void setUpDrawer() {
        // Set up the Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout_navigation_drawer);

        // Configure Drawer Toggle Button in Action Bar
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.waypoint_1, R.string.waypoint_2) {

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // Update the OptionsMenu
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                invalidateOptionsMenu(); // Update the OptionsMenu
            }
        };
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();

        // Set up the Recycler View inside the Navigation Drawer
        mRvRouteList = (RecyclerView) findViewById(R.id.recyclerview_navigation_navigationdrawer_route);
        mRvRouteList.setHasFixedSize(true); // No new Waypoints
        mRvRouteList.setLayoutManager(new LinearLayoutManager(this));
        mRouteAdapter = new RouteAdapter(this);
        mRvRouteList.setAdapter(mRouteAdapter);
        mRvRouteList.addItemDecoration(new DividerItemDecorator(this));
    }

    /**
     * This function sets up the GoogleMaps v2 Map
     */
    private void setUpMap() {
        // Get the Map Fragment
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.googlemap_navigation_map))
                .getMap();
        // Move the Camera to Bamberg
        LatLng BAMBERG = new LatLng(49.898814, 10.890764);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BAMBERG, 12f)); // Move Camera to Bamberg

        // Show Location on Maps
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, (int) getResources().getDimension(R.dimen.actionbar_height) + (int) getResources().getDimension(R.dimen.default_screen_padding), 0, 0);
    }

    public void onBtnClickedStartQuizLater(View view) {
        mStartQuiz.collapse(Edge.BOTTOM);
    }

    public void onBtnClickedStartQuizNow(View view) {
        Intent startCurrentQuiz = new Intent(this, QuizActivity.class);
        startCurrentQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, Route.get().getCurrentQuizId());
        startActivity(startCurrentQuiz);
    }

    private BroadcastReceiver ArrivedAtWaypointReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getIntExtra(TAG_QUIZ_ID, -1) == Route.get().getCurrentQuizId()) {
                QuizActivity.setNextQuizLockState(QuizActivity.QUIZ_UNLOCKED);
                mStartQuiz.show(Edge.BOTTOM, 1000); // Since there is the Possibility that the NavigationActivity is currently starting, add a Delay to the Animation
                ((TextView)mStartQuiz.findViewById(R.id.textview_navigation_start_quiz_text)).setText(String.format(getResources().getString(R.string.textview_navigation_start_quiz_text), intent.getStringExtra(TAG_WAYPOINT_NAME)));
            }
        }
    };
}