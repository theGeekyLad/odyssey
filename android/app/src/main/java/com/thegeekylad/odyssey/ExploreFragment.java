package com.thegeekylad.odyssey;

import android.annotation.SuppressLint;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.thegeekylad.odyssey.adapter.SimplePlacesAdapter;
import com.thegeekylad.odyssey.model.Bus;
import com.thegeekylad.odyssey.model.Place;
import com.thegeekylad.odyssey.model.Stop;
import com.thegeekylad.odyssey.service.Api;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class ExploreFragment extends Fragment {

    private Api api;
    private View rootView;
    private MaterialAutoCompleteTextView dropdownSourceStop;
    private MaterialAutoCompleteTextView dropdownBuses;
    private MaterialAutoCompleteTextView dropdownPlaceTypes;
    private List<Stop> stopsList;
    private MainActivityViewModel viewModel;
    private RecyclerView recyclerListPlaces;
    private Button buttonLoadPlaces;
    private Slider sliderRadius;
    private Slider sliderDetail;
    private Place.Filter filter;
    private Stop filterByStop;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view model
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

        // init
        api = Api.getInstance(getContext(), viewModel);
        filter = new Place.Filter();
    }

    @SuppressLint("MissingPermission")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        dropdownSourceStop = (MaterialAutoCompleteTextView) getActivity().findViewById(R.id.menu_text_source_stop);
        dropdownBuses = (MaterialAutoCompleteTextView) getActivity().findViewById(R.id.menu_text_bus);
        dropdownPlaceTypes = (MaterialAutoCompleteTextView) rootView.findViewById(R.id.menu_text_place_types);
        recyclerListPlaces = rootView.findViewById(R.id.recycler_list_places);
        buttonLoadPlaces = (Button) rootView.findViewById(R.id.button_load_places);
        sliderRadius = (Slider) rootView.findViewById(R.id.slider_radius);
        sliderDetail = (Slider) rootView.findViewById(R.id.slider_results);

        // attach listeners
        dropdownSourceStop.setOnItemClickListener(new DropdownSourceStopListener());
        dropdownBuses.setOnItemClickListener(new DropdownBusListener());
        dropdownPlaceTypes.setOnItemClickListener(new DropDownPlaceTypeListener());
        recyclerListPlaces.setLayoutManager(new LinearLayoutManager(getContext()));
        buttonLoadPlaces.setOnClickListener(v -> {
            toggleFiltersCollapsible();
            if (filterByStop == null)
                loadPlaces(viewModel.selectedBusIndex, filter);
            else
                loadPlaces(filter);
        });
        sliderRadius.setOnClickListener(v -> {
            filter.radius = ((Slider) v).getValue();
        });
        sliderDetail.setOnClickListener(v -> {
            filter.detail = ((Slider) v).getValue();
        });

        // get my present location
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnLocationListener());

        // are you coming from fragment destination?
        try {
            filterByStop = new Stop(getArguments().getString("stop"));
            rootView.findViewById(R.id.slider_detail).setVisibility(View.GONE);
            filter.detail = 0;
            ((TextView) rootView.findViewById(R.id.text_title_stop_selected)).setText(filterByStop.name);
            loadPlaces(filter);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // collapsible
        rootView.findViewById(R.id.text_title_radius_slider).setOnClickListener(v -> {
            toggleFiltersCollapsible();
        });

        return rootView;
    }

    private void toggleFiltersCollapsible() {
        View layoutFilters = rootView.findViewById(R.id.layout_filters);
        if (layoutFilters.getVisibility() == View.VISIBLE)
            layoutFilters.setVisibility(View.GONE);
        else layoutFilters.setVisibility(View.VISIBLE);
    }

    // on select nearby stop
    private class DropdownSourceStopListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            viewModel.selectedStopIndex = position;

            // get buses at this stop
            api.getBuses(
                    viewModel.nearbyStopsList.get(position).stopId,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                List<String> busesList = new ArrayList<>();
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    Bus bus = new Bus(array.get(i).toString());
                                    viewModel.nearbyBusesList.add(bus);
                                    busesList.add(bus.busName);
                                }
                                dropdownBuses.setSimpleItems(busesList.toArray(new String[]{}));
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

    // on select bus
    public void loadPlaces(int position, Place.Filter filter) {
        // get buses at this stop
        api.getAllPlaces(
                viewModel.nearbyBusesList.get(position).busId,
                filter,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            viewModel.placesList = new ArrayList<>();
                            viewModel.placeTypesSet = new HashSet<>();
                            for (int i = 0; i < array.length(); i++) {
                                Place place = new Place(array.get(i).toString());
                                viewModel.placesList.add(place);
                                viewModel.placeTypesSet.add(place.type);
                            }
                            SimplePlacesAdapter simplePlacesAdapter = new SimplePlacesAdapter(viewModel.placesList);
                            recyclerListPlaces.setAdapter(simplePlacesAdapter);

                            dropdownPlaceTypes.setSimpleItems(viewModel.placeTypesSet.toArray(new String[]{}));
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

    // on coming from destination fragment
    public void loadPlaces(Place.Filter filter) {
        // get buses at this stop
        api.getPlaces(
                filterByStop.stopId,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray array = new JSONArray(response);
                            viewModel.placesList = new ArrayList<>();
                            viewModel.placeTypesSet = new HashSet<>();
                            for (int i = 0; i < array.length(); i++) {
                                Place place = new Place(array.get(i).toString());
                                viewModel.placesList.add(place);
                                viewModel.placeTypesSet.add(place.type);
                            }
                            SimplePlacesAdapter simplePlacesAdapter = new SimplePlacesAdapter(viewModel.placesList);
                            recyclerListPlaces.setAdapter(simplePlacesAdapter);

                            dropdownPlaceTypes.setSimpleItems(viewModel.placeTypesSet.toArray(new String[]{}));
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
    private class DropdownBusListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            viewModel.selectedBusIndex = position;
            loadPlaces(position, new Place.Filter());
        }
    }

    // on selecting a place type filter
    private class DropDownPlaceTypeListener implements AdapterView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            filter.type = viewModel.placeTypesSet.toArray(new String[] {})[position];
        }
    }

    // on getting your location
    private class OnLocationListener implements OnSuccessListener<Location> {
        @Override
        public void onSuccess(Location location) {
            if (location == null) {
                Toast.makeText(getContext(), "Location is null.", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());

            // get stops nearby
            api.getNearbyStops(
                    String.valueOf(location.getLatitude()),
                    String.valueOf(location.getLongitude()),
                    new Response.Listener<String>() {
                        @SuppressLint("WrongViewCast")
                        @Override
                        public void onResponse(String response) {
                            try {
                                List<String> stopsList = new ArrayList<>();
                                JSONArray array = new JSONArray(response);
                                for (int i = 0; i < array.length(); i++) {
                                    Stop stop = new Stop(array.get(i).toString());
                                    viewModel.nearbyStopsList.add(stop);
                                    stopsList.add(stop.name);
                                }
                                dropdownSourceStop.setSimpleItems(stopsList.toArray(new String[]{}));
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
}