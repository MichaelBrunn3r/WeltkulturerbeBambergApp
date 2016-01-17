package com.github.wksb.wkebapp;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import com.github.wksb.wkebapp.activity.navigation.NavigationActivity;
import com.github.wksb.wkebapp.activity.navigation.Route;

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
            if (intent.getIntExtra(TAG_QUIZ_ID, -1) != Route.getCurrentQuizId()) return;

            if (!LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent(NavigationActivity.ACTION_NOTIFY_ARRIVED_AT_WAYPOINT))) {
                Intent openNavigationActivity = new Intent(context, NavigationActivity.class);
                PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, openNavigationActivity, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle(String.format(
                                context.getResources().getString(R.string.notification_arrived_at_waypoint_title)
                                , intent.getStringExtra(TAG_WAYPOINT_NAME)))
                        .setContentText(String.format(
                                context.getResources().getString(R.string.notification_arrived_at_waypoint_detail)
                                , intent.getStringExtra(TAG_WAYPOINT_NAME)
                        ))
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent);
                ((NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE)).notify(0, builder.build());

                Route.setArrivedAtCurrentDestination(true);
            }
        }
    }
}