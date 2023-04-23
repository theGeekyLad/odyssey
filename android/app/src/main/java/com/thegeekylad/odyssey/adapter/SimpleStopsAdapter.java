package com.thegeekylad.odyssey.adapter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.thegeekylad.odyssey.R;
import com.thegeekylad.odyssey.model.Stop;

import org.json.JSONObject;

import java.util.List;

public class SimpleStopsAdapter extends RecyclerView.Adapter<SimpleStopsAdapter.StopsViewHolder> {
    private NavController navController;
    private List<Stop> stopsList;

    public SimpleStopsAdapter(NavController navController, List<Stop> stopsList) {
        this.navController = navController;
        this.stopsList = stopsList;
    }

    @NonNull
    @Override
    public StopsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_bus_stop, parent, false);
        return new StopsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StopsViewHolder holder, int position) {
        holder.setStop(stopsList.get(position));
        holder.handleStopClick(stopsList.get(position));
    }

    @Override
    public int getItemCount() {
        return stopsList.size();
    }

    class StopsViewHolder extends RecyclerView.ViewHolder {

        public StopsViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void handleStopClick(Stop stop) {
            itemView.setOnClickListener(v -> {
                Bundle args = new Bundle();
                args.putString("stop", stop.toString());
                navController.navigate(R.id.action_destinationFragment_to_exploreFragment, args);
            });
        }

        public void setStop(Stop stop) {
            ((TextView) itemView.findViewById(R.id.text_stop_title)).setText(stop.name);
        }
    }
}
