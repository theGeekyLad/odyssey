package com.thegeekylad.odyssey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.thegeekylad.odyssey.R;
import com.thegeekylad.odyssey.model.Place;

import java.util.List;

public class SimplePlacesAdapter extends RecyclerView.Adapter<PlacesViewHolder> {
    private Context context;
    private List<Place> placeList;

    public SimplePlacesAdapter(Context context, List<Place> placeList) {
        this.context = context;
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
        ((TextView) holder.itemView.findViewById(R.id.text_place_title)).setText(placeList.get(position).title);
        Glide.with(context).load(placeList.get(position).iconUrl).into(((ImageView) holder.itemView.findViewById(R.id.image_icon_place)));
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
}
