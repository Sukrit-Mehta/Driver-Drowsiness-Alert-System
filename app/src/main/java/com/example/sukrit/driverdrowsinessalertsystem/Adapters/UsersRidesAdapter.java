package com.example.sukrit.driverdrowsinessalertsystem.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sukrit.driverdrowsinessalertsystem.Models.DriverCurrentRide;
import com.example.sukrit.driverdrowsinessalertsystem.R;

import java.util.ArrayList;

/**
 * Created by sukrit on 13/4/18.
 */

public class UsersRidesAdapter extends RecyclerView.Adapter<UsersRidesAdapter.UserListViewHolder>{

    ArrayList<DriverCurrentRide> arrayList;
    Context context;

    public UsersRidesAdapter(ArrayList<DriverCurrentRide> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public UserListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutType = R.layout.users_rides_list_item;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(layoutType,parent,false);
        return new UserListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserListViewHolder holder, int position) {
        DriverCurrentRide userRide = arrayList.get(position);
        holder.startTime.setText(userRide.getStartTime());
        holder.endTime.setText(userRide.getEndTime());
        holder.source.setText(userRide.getSource());
        holder.destination.setText(userRide.getDestination());
        holder.tvDate.setText(userRide.getDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class UserListViewHolder extends RecyclerView.ViewHolder
    {
        TextView source,destination,startTime,endTime,avgSpeed,tvDate;
        View testView;

        public UserListViewHolder(View itemView) {
            super(itemView);
            source = itemView.findViewById(R.id.tvSource);
            destination = itemView.findViewById(R.id.tvDestination);
            startTime = itemView.findViewById(R.id.tvStartTime);
            endTime = itemView.findViewById(R.id.tvEndTime);
            avgSpeed = itemView.findViewById(R.id.tvAvgSpeed);
            tvDate = itemView.findViewById(R.id.tvDate);
            testView = itemView;
        }
    }
}
