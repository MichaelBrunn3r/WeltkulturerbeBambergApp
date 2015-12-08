package com.github.wksb.wkebapp.activity.navigation;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;

import com.github.wksb.wkebapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * Created by Michael on 27.10.2015.
 */
public class RouteSegment {

    private static final int DEFAULT_CURRENT_DESTINATION_WAYPOINT_ID = -1;

    private Route route;
    private int fromWaypointID;
    private int toWaypointID;
    private PolylineOptions polyline;
    private boolean active = false;

    public RouteSegment(Route route, int fromWaypointID, int toWaypointID, PolylineOptions polyline) {
        this.route = route;
        this.fromWaypointID = fromWaypointID;
        this.toWaypointID = toWaypointID;
        this.polyline = polyline;
    }

    public Context getContext() {
        return route.getContext();
    }

    public int[] getWaypointIds() {
        return new int[]{fromWaypointID, toWaypointID};
    }

    public void renderOnMap(GoogleMap googleMap) {

        // Add the Marker for the from and to Waypoint to the map
        addMarkersToMap(googleMap);

        // Add the Polyline showing the Route to the Map
        addPolylineToMap(googleMap);
    }

    /**
     * Initialises this RouteSegment. Adds Markers for the Start and Destination, the Polyline connecting the two Markers and a Proximity for the Destination
     * @param googleMap The Map on which to add the Markers and the Polyline
     */
    public void init(GoogleMap googleMap) {

        // Set the current Quiz to the Quiz of the current destination Waypoint
        Route.setCurrentQuizId(getContext(), route.getWaypointById(toWaypointID).getQuizId());

        // Add the proximity alert at the destination Waypoint
        addProximityAlert();

        // Zoom to the Route Segment on the Map
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(
                        route.getWaypointById(fromWaypointID).getLatitude(),
                        route.getWaypointById(fromWaypointID).getLongitude()), 16));
    }

    /**
     * Add the Marker for the from and to Waypoints to a {@link GoogleMap}
     * @param map The {@link GoogleMap} to which the Markers are added
     */
    private void addMarkersToMap(GoogleMap map) {
        MarkerOptions fromWapointMarker = new MarkerOptions()
                .title(route.getWaypointById(fromWaypointID).getName())
                .position(new LatLng(route.getWaypointById(fromWaypointID).getLatitude(), route.getWaypointById(fromWaypointID).getLongitude()));

        MarkerOptions toWapointMarker = new MarkerOptions()
                .title(route.getWaypointById(toWaypointID).getName())
                .position(new LatLng(route.getWaypointById(toWaypointID).getLatitude(), route.getWaypointById(toWaypointID).getLongitude()));

        // If this RouteSegment is active, change its Icon
        if (active) {
            Drawable iconDrawable = getContext().getResources().getDrawable(R.drawable.ic_marker_current_position);
            Bitmap iconBitmap = Bitmap.createBitmap(iconDrawable.getIntrinsicWidth(), iconDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(iconBitmap);
            iconDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            iconDrawable.draw(canvas);
            toWapointMarker.icon(BitmapDescriptorFactory.fromBitmap(iconBitmap));
        }

        map.addMarker(fromWapointMarker);
        map.addMarker(toWapointMarker);
    }

    /**
     * Add a proximity alert at the destination Waypoint
     */
    private void addProximityAlert() {
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Activity.LOCATION_SERVICE);
        Intent proximityAlert = new Intent();
        proximityAlert.setAction("com.github.weltkulturschnitzelbamberg.weltkulturerbebambergapp.PROXIMITY_ALERT");
        proximityAlert.putExtra("waypoint-name", route.getWaypointById(toWaypointID).getName());
        proximityAlert.putExtra("quiz-id", route.getWaypointById(toWaypointID).getQuizId());

        int detectionRadius = 40; // The radius around the central point in which to send an proximity alert
        int expirationTime = -1; // The time in milliseconds it takes this proximity alert to expire (-1 = no expiration)
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, proximityAlert, PendingIntent.FLAG_UPDATE_CURRENT); // TODO Previous Proximity Alerts have to be removed

        // TODO Security Error
        locationManager.addProximityAlert(route.getWaypointById(toWaypointID).getLatitude(), route.getWaypointById(toWaypointID).getLongitude(), detectionRadius, expirationTime, pendingIntent);
    }

    private void addPolylineToMap(GoogleMap map) {
        if (active) {
            polyline.color(getContext().getResources().getColor(R.color.PrimaryColor));
        } else polyline.color(getContext().getResources().getColor(R.color.SecondaryTextColor));
        map.addPolyline(polyline);
    }

    public void setIsActive(boolean isActive) {
        active = isActive;
    }
}