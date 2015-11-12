package com.github.wksb.wkebapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Michael on 11.11.2015.
 */
public class WaypointsArrayAdapter extends ArrayAdapter<String> {

    private Context mContext;
    private String[] mWaypoints;

    public WaypointsArrayAdapter(Context context, String[] values) {
        super(context, -1, values);
        this.mContext = context;
        this.mWaypoints = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View waypointView;

        if (convertView == null) {
            // Create new View if covertView is null
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            waypointView = inflater.inflate(R.layout.list_waypoints_item, parent, false);
        } else {
            // If covertView is not null recycle the View
            waypointView = convertView;
        }

        ImageView icon = (ImageView) waypointView.findViewById(R.id.list_waypoints_item_icon);
        icon.setImageResource(R.drawable.ic_waypoint_selected);
        TextView text = (TextView) waypointView.findViewById(R.id.list_waypoints_item_text);
        text.setText(mWaypoints[position]);

        return waypointView;
    }


}
