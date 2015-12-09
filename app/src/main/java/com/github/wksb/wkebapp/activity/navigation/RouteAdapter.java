package com.github.wksb.wkebapp.activity.navigation;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.wksb.wkebapp.R;

/**
 * Created by Michael on 18.11.2015.
 */
public class RouteAdapter extends RecyclerView.Adapter<RouteAdapter.WaypointViewHolder> {

    private Route mRoute;

    public RouteAdapter(Route route) {
        mRoute = route;
    }

    @Override
    public WaypointViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_waypoint, parent, false);
        return new WaypointViewHolder(view);
    }

    @Override
    public void onBindViewHolder(WaypointViewHolder holder, int position) {
        Waypoint waypoint = mRoute.getWaypointAt(position);

        switch (waypoint.getState()) {
            case VISITED:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_visited);
                break;
            case CURRENT_DESTINATION:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_current_position);
                break;
            default:
                holder.mIvWaypointStateIcon.setImageResource(R.drawable.ic_waypoint_not_visited);
        }
        holder.mTvWaypointName.setText(waypoint.getName());
    }

    @Override
    public int getItemCount() {
        return mRoute.getLength();
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