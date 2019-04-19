package com.example.sukrit.driverdrowsinessalertsystem.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sukrit.driverdrowsinessalertsystem.Models.DriverPastRide;
import com.example.sukrit.driverdrowsinessalertsystem.R;

import java.util.ArrayList;

/**
 * Created by sukrit on 13/4/18.
 */

public class DriverRidesAdapter extends RecyclerView.Adapter<DriverRidesAdapter.DriverListViewHolder>{

    ArrayList<DriverPastRide> arrayList;
    Context context;

    public DriverRidesAdapter(ArrayList<DriverPastRide> arrayList, Context context) {
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public DriverListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutType = R.layout.driver_rides_list_item;
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View itemView = layoutInflater.inflate(layoutType,parent,false);
        return new DriverListViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(DriverListViewHolder holder, int position) {
        DriverPastRide pastRide = arrayList.get(position);
        holder.source.setText(pastRide.getSource());
        holder.destination.setText(pastRide.getDestination());
        holder.startTime.setText(pastRide.getStartTime());
        holder.endTime.setText(pastRide.getEndTime());
        holder.tvDate.setText(pastRide.getDate());
    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    class DriverListViewHolder extends RecyclerView.ViewHolder
    {
        TextView source,destination,startTime,endTime,avgSpeed,tvDate;
        View testView;

        public DriverListViewHolder(View itemView) {
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
