package com.thegeekylad.odyssey;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.thegeekylad.odyssey.adapter.SimpleStopsAdapter;
import com.thegeekylad.odyssey.model.Stop;
import com.thegeekylad.odyssey.service.Api;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class DestinationFragment extends Fragment {
    private MainActivityViewModel viewModel;
    private Api api;
    private Button buttonLoadStops;
    private RecyclerView recyclerListStops;
    private NavController navController;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view model
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

        // init
        api = Api.getInstance(getContext(), viewModel);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_destination, container, false);
        buttonLoadStops = (Button) rootView.findViewById(R.id.button_load_stops);
        recyclerListStops = rootView.findViewById(R.id.recycler_list_stops);

        // init listeners
        navController = ((NavHostFragment) getActivity()
                .getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment))
                .getNavController();
        recyclerListStops.setLayoutManager(new LinearLayoutManager(getContext()));
        buttonLoadStops.setOnClickListener(v -> {
            loadAllStops();
        });

        return rootView;
    }

    public void loadAllStops() {
        api.getAllStops(
                viewModel.nearbyBusesList.get(viewModel.selectedBusIndex).busId,
                new Response.Listener<String>() {
                    @SuppressLint("WrongViewCast")
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Stop> stopsList = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                Stop stop = new Stop(array.get(i).toString());
                                stopsList.add(stop);
                            }
                            SimpleStopsAdapter simpleStopsAdapter = new SimpleStopsAdapter(navController, stopsList);
                            recyclerListStops.setAdapter(simpleStopsAdapter);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );
    }
}