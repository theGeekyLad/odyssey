package com.thegeekylad.odyssey.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.thegeekylad.odyssey.R;
import com.thegeekylad.odyssey.model.Place;

import java.util.List;

public class SimplePlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {
    private List<Place> placeList;

    public SimplePlacesAdapter(List<Place> placeList) {
        this.placeList = placeList;
    }

    @NonNull
    @Override
    public PlacesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_item_places, parent, false);
        return new PlacesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlacesViewHolder holder, int position) {
        holder.setPlace(placeList.get(position));
    }

    @Override
    public int getItemCount() {
        return placeList.size();
    }
}

class PlacesViewHolder extends RecyclerView.ViewHolder {

    public PlacesViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public void setPlace(Place place) {
        ((TextView) itemView.findViewById(R.id.text_place_title)).setText(place.title);
    }
}
