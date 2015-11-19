package com.github.wksb.wkebapp.activity.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.wksb.wkebapp.R;

import java.util.ArrayList;

/**
 * Created by Michael on 18.11.2015.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.WaypointViewHolder> {

    private ArrayList<Waypoint> mWaypoints;

    public RouteAdapter(ArrayList<Waypoint> waypoints) {
        mWaypoints = waypoints;
    }

    @Override
    public WaypointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_waypoint, parent, false);
        return new WaypointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaypointViewHolder holder, int position) {
        switch (mWaypoints.get(position).getState()) {
            case VISITED:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_visited);
                break;
            case CURRENT_POSITION:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_current_position);
                break;
            default:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_not_visited);
        }
        holder.mTvWaypointName.setText(mWaypoints.get(position).getName());
    }

    public void addWaypoint(Waypoint waypoint, int position) {
        mWaypoints.add(position, waypoint);
        notifyItemChanged(position);
    }

    public void removeWaypoint(Waypoint waypoint, int position) {
        mWaypoints.remove(position);
        notifyItemChanged(position);
    }

    @Override
    public int getItemCount() {
        return mWaypoints.size();
    }

    public boolean isEmpty() {
        return mWaypoints.size() == 0;
    }

    class WaypointViewHolder extends RecyclerView.ViewHolder {

        public ImageView mIvWaypointStateIcon;
        public TextView mTvWaypointName;
        public TextView mTvDistanceToNextWaypoint;

        public WaypointViewHolder(View view) {
            super(view);
            mIvWaypointStateIcon = (ImageView) view.findViewById(R.id.row_waypoint_icon);
            mTvWaypointName = (TextView) view.findViewById(R.id.row_waypoint_name);
            mTvDistanceToNextWaypoint = (TextView) view.findViewById(R.id.row_waypoint_distance);

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