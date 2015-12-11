package com.github.wksb.wkebapp.activity.navigation;

import android.content.Context;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationManager;

import com.github.wksb.wkebapp.contentprovider.WeltkulturerbeContentProvider;
import com.github.wksb.wkebapp.database.WaypointsTable;

/**
 * Created by Michael on 27.10.2015.
 */
public class Waypoint {

    private float mLatitude;
    private float mLongitude;
    private String mName;
    private int mQuizId;
    private WaypointState mState;
    private int mId;

    public Waypoint(int Id) {
        mState = WaypointState.NOT_VISITED;
        this.mId = Id;
    }

    public Waypoint(int waypointID, WaypointState state) {
        this.mState = state;
        this.mId = waypointID;
    }

    public void loadDataFromDatabase(Context context) {
        // The parameter for the SQLite query
        String[] projection = {WaypointsTable.COLUMN_LATITUDE, WaypointsTable.COLUMN_LONGITUDE, WaypointsTable.COLUMN_NAME, WaypointsTable.COLUMN_QUIZ_ID};
        String selection = WaypointsTable.COLUMN_WAYPOINT_ID + "=?";
        String[] selectionArgs = {"" + mId};

        Cursor cursor = context.getContentResolver().query(WeltkulturerbeContentProvider.URI_TABLE_WAYPOINTS, projection, selection, selectionArgs, null);
        if (cursor.moveToNext()) {
            mLatitude = cursor.getFloat(cursor.getColumnIndex(WaypointsTable.COLUMN_LATITUDE));
            mLongitude = cursor.getFloat(cursor.getColumnIndex(WaypointsTable.COLUMN_LONGITUDE));
            mName = cursor.getString(cursor.getColumnIndex(WaypointsTable.COLUMN_NAME));
            mQuizId = cursor.getInt(cursor.getColumnIndex(WaypointsTable.COLUMN_QUIZ_ID));
        }
        cursor.close();
    }

    /**
     * Get the Name of this Waypoint
     * @return The Name of this Waypoint
     */
    public String getName() {
        return mName;
    }

    /**
     * Get the Latitude of this Waypoint
     * @return The Latitude of this Waypoint
     */
    public float getLatitude() {
        return mLatitude;
    }

    /**
     * Get the Longitude of this Waypoint
     * @return The Waypoint of this Waypoint
     */
    public float getLongitude() {
        return mLongitude;
    }

    /**
     * Get the {@link Location} of this Waypoint
     * @return The {@link Location} of this Waypoint
     */
    public Location getLocation() {
        Location location = new Location(LocationManager.GPS_PROVIDER);
        location.setLatitude(mLatitude);
        location.setLongitude(mLongitude);
        // location.setAltitude(200); TODO Add Altitude for better Accuracy?
        return location;
    }

    /**
     * Get the ID of the Quiz about this Waypoint
     * @return The ID of the Quiz about this Waypoint
     */
    public int getQuizId() {
        return mQuizId;
    }

    /**
     * Get the Id of this Waypoint
     * @return The Id of this Waypoint
     */
    public int getId() {
        return mId;
    }

    /**
     * Set the {@link com.github.wksb.wkebapp.activity.navigation.Waypoint.WaypointState} of this Waypoint
     * @param state The new {@link com.github.wksb.wkebapp.activity.navigation.Waypoint.WaypointState} of this Waypoint
     */
    public void setState(WaypointState state) {
        mState = state;
    }

    /**
     * Get the {@link com.github.wksb.wkebapp.activity.navigation.Waypoint.WaypointState} of this Waypoint
     * @return The {@link com.github.wksb.wkebapp.activity.navigation.Waypoint.WaypointState} of this Waypoint
     */
    public WaypointState getState() {
        return mState;
    }

    public boolean isNotVisited() {
        return mState == WaypointState.NOT_VISITED;
    }

    public boolean isVisited() {
        return mState == WaypointState.VISITED;
    }

    public boolean isCurrentDestination() {
        return mState == WaypointState.CURRENT_DESTINATION;
    }

    public enum WaypointState {
        NOT_VISITED,
        VISITED,
        CURRENT_DESTINATION
    }
}
