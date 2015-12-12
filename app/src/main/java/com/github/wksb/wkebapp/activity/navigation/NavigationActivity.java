package com.github.wksb.wkebapp.activity.navigation;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
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

import com.github.wksb.wkebapp.R;
import com.github.wksb.wkebapp.activity.QuizActivity;
import com.github.wksb.wkebapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.wksb.wkebapp.database.RouteSegmentsTable;
import com.github.wksb.wkebapp.database.RoutesTable;
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

    // Title of the ActionBar
    private TextView mActionBarTitle;

    // The Google Maps Fragment
    private GoogleMap mMap;

    // This Object contains the current Route with all its RouteSegments
    private Route mRoute;

    // Components of the Navigation Drawer
    private DrawerLayout mDrawerLayout;
    private RecyclerView mRvRouteList;
    private RouteAdapter mRouteAdapter;
    private ActionBarDrawerToggle mDrawerToggle;  // Button in ActionBar toggling the DrawerLayout

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Set up the Action Bar
        setUpActionBar();

        // Set Tour in progress
        Route.setProgressState(this, true);
    }

    @Override
     protected void onStart() {
        super.onStart();

        // Set up Route and Map if they don't exist
        if (mMap == null) setUpMap();
        if (mRoute == null) setUpRoute();

        // Synchronise the States of the Waypoints with the Progress of the Route
        mRoute.syncWithProgress();

        // Set up the Navigation Drawer
        setUpDrawer();

        mMap.clear();
        mRoute.renderOnMap(mMap);

        // TODO Change the Design of the Progress Bar
        mActionBarTitle.setText(String.format("Progress: %d / %d", Route.getProgress(this), mRoute.getRouteSegments().size()));
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

        Intent startQuiz = new Intent(this, QuizActivity.class);
        switch (item.getItemId()) {
            case R.id.action_navigation_waypoint_1:
                // No Quiz
                return true;
            case R.id.action_navigation_waypoint_2:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 3);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_3:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 8);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_4:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 9);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_5:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 7);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_6:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 4);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_7:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 5);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_8:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 1);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_9:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 6);
                startActivity(startQuiz);
                return true;
            case R.id.action_navigation_waypoint_10:
                QuizActivity.setProgressState(this, QuizActivity.IS_IN_PROGRESS);
                startQuiz.putExtra(QuizActivity.TAG_QUIZ_ID, 2);
                startActivity(startQuiz);
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

        // Use Custom ActionBar Layout and Display BackButton
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_HOME_AS_UP);

        // Set Custom ActionBar Layout
        getSupportActionBar().setCustomView(R.layout.actionbar_title);

        mActionBarTitle = (TextView) findViewById(R.id.actionbar_title);
    }

    private void setUpDrawer() {
        // Set up the Navigation Drawer
        mDrawerLayout = (DrawerLayout) findViewById(R.id.navlayout_navigation);

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
        mRvRouteList = (RecyclerView) findViewById(R.id.rv_navigation_route);
        mRvRouteList.setHasFixedSize(true); // No new Waypoints
        mRvRouteList.setLayoutManager(new LinearLayoutManager(this));
        mRouteAdapter = new RouteAdapter(this, mRoute);
        mRvRouteList.setAdapter(mRouteAdapter);
        mRvRouteList.addItemDecoration(new DividerItemDecorator(this));
    }

    /**
     * This function sets up the GoogleMaps v2 Map
     */
    private void setUpMap() {
        // Get the Map Fragment
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .getMap();

        // Move Camera to Bamberg
        LatLng BAMBERG = new LatLng(49.898814, 10.890764);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(BAMBERG, 12f));

        // Show Location on Maps
        mMap.setMyLocationEnabled(true);
        mMap.setPadding(0, (int) getResources().getDimension(R.dimen.actionbar_height) + (int) getResources().getDimension(R.dimen.activity_margin), 0, 0);
    }

    //TODO Documentation
    private void setUpRoute() {

        // Query Arguments
        String[] projection = {RoutesTable.COLUMN_ROUTE_SEGMENT_ID, RoutesTable.COLUMN_ROUTE_SEGMENT_POSITION};
        String selection = RoutesTable.COLUMN_ROUTE_NAME + "=?";
        String[] selectionArgs = {Route.getName(this)};

        // Create a new Route
        mRoute = new Route(this);

        // Query for Route Segments
        Cursor routeSegments = getContentResolver().query(WeltkulturerbeContentProvider.URI_TABLE_ROUTES,
                projection, selection, selectionArgs, null);
        while (routeSegments.moveToNext()) {
            projection = new String[]{RouteSegmentsTable.COLUMN_START_WAYPOINT_ID, RouteSegmentsTable.COLUMN_END_WAYPOINT_ID, RouteSegmentsTable.COLUMN_KML_FILENAME};
            selection = RouteSegmentsTable.COLUMN_SEGMENT_ID + "=?";
            selectionArgs = new String[]{""+routeSegments.getInt(routeSegments.getColumnIndex(RoutesTable.COLUMN_ROUTE_SEGMENT_ID))};

            Cursor routeSegment = getContentResolver().query(WeltkulturerbeContentProvider.URI_TABLE_ROUTE_SEGMENTS, projection, selection, selectionArgs, null);
            if (routeSegment.moveToNext()) {
                int fromWaypointID = routeSegment.getInt(routeSegment.getColumnIndex(RouteSegmentsTable.COLUMN_START_WAYPOINT_ID));
                int toWaypointID = routeSegment.getInt(routeSegment.getColumnIndex(RouteSegmentsTable.COLUMN_END_WAYPOINT_ID));
                String polylineFile = routeSegment.getString(routeSegment.getColumnIndex(RouteSegmentsTable.COLUMN_KML_FILENAME));

                List<LatLng> points = new ArrayList<>();

                try {
                    JSONParser parser = new JSONParser();
                    Object obj = parser.parse(new InputStreamReader(getAssets().open(polylineFile)));

                    if (obj instanceof JSONObject) {
                        JSONObject jsonObject = (JSONObject) obj;

                        for (String coordinates : ((String)jsonObject.get("polyline")).split(",0.0")) {
                            String longitude = coordinates.substring(0, coordinates.indexOf(","));
                            String latitude = coordinates.substring(coordinates.indexOf(",") +1);

                            LatLng point = new LatLng(Float.parseFloat(latitude), Float.parseFloat(longitude));
                            points.add(point);
                        }
                    }
                } catch (ParseException | IOException e) {
                    e.printStackTrace();
                }

                PolylineOptions polyline = new PolylineOptions();

                polyline.addAll(points);
                polyline.color(getResources().getColor(R.color.PrimaryColor)); // TODO Deprecated Method
                polyline.width(12);
                polyline.geodesic(true);

                // Add a new Route Segment to the current Route
                mRoute.addRouteSegment(fromWaypointID, toWaypointID, polyline);
            }
        }
    }
}