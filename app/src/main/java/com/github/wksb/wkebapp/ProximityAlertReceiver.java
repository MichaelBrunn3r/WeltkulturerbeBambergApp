package com.github.wksb.wkebapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.content.LocalBroadcastManager;

import com.github.wksb.wkebapp.activity.navigation.NavigationActivity;

//TODO Documentation
/**
 * Created by michael on 12.06.15.
 */
public class ProximityAlertReceiver extends BroadcastReceiver{

    public static final String ACTION_PROXIMITY_ALERT = "com.github.wksb.wkebapp.PROXIMITY_ALERT";
    public static final String TAG_WAYPOINT_NAME = "waypoint_name"; // Tag of the Waypoint Name of an Intent send to this class
    public static final String TAG_QUIZ_ID = "quiz_id"; // Tag of the Quiz ID of an Intent send to this class

    @Override
    public void onReceive(Context context, Intent intent) {
        boolean entering = intent.getBooleanExtra(LocationManager.KEY_PROXIMITY_ENTERING, false);
        if (entering) {
            Intent notifyArrivedAtWaypoint = new Intent(NavigationActivity.ACTION_ARRIVED_AT_WAYPOINT);
            notifyArrivedAtWaypoint.putExtra(NavigationActivity.TAG_QUIZ_ID, intent.getIntExtra(TAG_QUIZ_ID, -1));
            notifyArrivedAtWaypoint.putExtra(NavigationActivity.TAG_WAYPOINT_NAME, intent.getStringExtra(TAG_WAYPOINT_NAME));
            LocalBroadcastManager.getInstance(context).sendBroadcast(notifyArrivedAtWaypoint);
        }
    }
}