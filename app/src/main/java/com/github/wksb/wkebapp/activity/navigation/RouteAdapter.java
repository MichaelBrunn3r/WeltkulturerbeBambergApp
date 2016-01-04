package com.github.wksb.wkebapp.activity.navigation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.wksb.wkebapp.R;

/**
 * This Adapter provides a binding from the Waypoints in the current Route to views that are displayed within the {@link RecyclerView} in the {@link NavigationActivity}.
 *
 * @author Projekt-Seminar "Schnitzeljagd World-heritage" 2015/2016 des Clavius Gymnasiums Bamberg
 * @version 1.0
 * @since 2015-06-04
 */

public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.WaypointViewHolder> implements LocationListener{

    // Specifiers for the Location Update Subscription
    private final static int MINIMUM_TIME_BETWEEN_UPDATE = 3000; // In Milliseconds
    private final static int MINIMUM_DISTANCECHANGE_FOR_UPDATE = 1; // In Meter

    private Context mContext;

    public RouteAdapter(Context context) {
        mContext = context;

        // Subscribe to Location Updates
        LocationManager locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MINIMUM_TIME_BETWEEN_UPDATE, MINIMUM_DISTANCECHANGE_FOR_UPDATE, this);
    }

    @Override
    public WaypointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //  Inflate the ViewHolder
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_waypoint, parent, false);
        return new WaypointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaypointViewHolder holder, int position) {
        Waypoint waypoint = Route.get().getWaypointAt(position);

        // Set the Icon of the WaypointViewHolder, depending on the state of the Waypoint
        switch (waypoint.getState()) {
            case VISITED:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_visited);
                holder.mTvWaypointName.setTextColor(getContext().getResources().getColor(R.color.BackgroundTextAndIconsColor)); // Set the Text Color of the WaypointName to the BackgroundTextAndIconsColor if the Waypoint was already visited
                break;
            case CURRENT_DESTINATION:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_current_position);
                holder.mTvWaypointName.setTextColor(getContext().getResources().getColor(R.color.BackgroundTextAndIconsColor)); // Set the Text Color of the WaypointName to the BackgroundTextAndIconsColor if the Waypoint is the current Destination
                break;
            case NOT_VISITED:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_not_visited);
                holder.mTvWaypointName.setTextColor(getContext().getResources().getColor(R.color.BackgroundTextAndIconsColorLight)); // Set the Text Color of the WaypointName to the BackgroundTextAndIconsColorLight if the Waypoint was not yet visited
                break;
        }

        // Set the WaypointName TextViews Text to the Name of the Waypoint
        holder.mTvWaypointName.setText(waypoint.getName());

        // Set the Text of the DistanceToWaypoint
        Location lastKnownLocation = ((LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE)).getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastKnownLocation != null) {
            int distance = Math.round(lastKnownLocation.distanceTo(waypoint.getLocation())); // The Distance between the current Position and the Waypoint
            if (distance > 1) holder.mTvDistanceToWaypoint.setText(distance + " " + getContext().getResources().getString(R.string.distance_unit));
            else holder.mTvDistanceToWaypoint.setText(distance + " " + getContext().getResources().getString(R.string.distance_unit_one));
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // When an Location Update is received notify this Adapter to update the Data of all ViewHolders.
        // This is used to update the Distance to each Waypoint
        for (int i=0; i<getItemCount(); i++) {
            notifyItemChanged(i);
        }
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public int getItemCount() {
        return Route.get().getLength();
    }

    public Context getContext() {
        return mContext;
    }

    class WaypointViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvWaypointStateIcon;
        public TextView mTvWaypointName;
        public TextView mTvDistanceToWaypoint;

        public WaypointViewHolder(View view) {
            super(view);
            mIvWaypointStateIcon = (ImageView) view.findViewById(R.id.row_waypoint_icon);
            mTvWaypointName = (TextView) view.findViewById(R.id.row_waypoint_name);
            mTvDistanceToWaypoint = (TextView) view.findViewById(R.id.row_waypoint_distance);

            // Enable marquee TextScrolling of the Waypoint Name when clicking on it
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Set Waypoint Name TextView selected to enable text scrolling (marquee)
                    if (mTvWaypointName.isSelected()) {
                        mTvWaypointName.setSelected(false);
                    } else {
                        mTvWaypointName.setSelected(true);
                    }
                }
            });
        }
    }
}