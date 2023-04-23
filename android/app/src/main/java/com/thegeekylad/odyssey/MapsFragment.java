package com.thegeekylad.odyssey;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Slide;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.JointType;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.slider.Slider;
import com.google.maps.DirectionsApi;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.DirectionsStep;
import com.google.maps.model.EncodedPolyline;
import com.thegeekylad.odyssey.adapter.SimplePlacesAdapter;
import com.thegeekylad.odyssey.adapter.SimpleStopsAdapter;
import com.thegeekylad.odyssey.model.Bus;
import com.thegeekylad.odyssey.model.Place;
import com.thegeekylad.odyssey.model.Stop;
import com.thegeekylad.odyssey.model.StopPlaces;
import com.thegeekylad.odyssey.service.Api;
import com.thegeekylad.odyssey.util.Constants;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MapsFragment extends Fragment {
    private MainActivityViewModel viewModel;
    private Api api;
    private Button buttonLoadMap;
    private GoogleMap googleMap;
    private Slider sliderDetail;
    private float sliderDetailValue = Constants.RADIUS_DEFAULT;
    private List<Marker> stopMarkers;
    private List<Marker> placeMarkers;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // view model
        viewModel = new ViewModelProvider(getActivity()).get(MainActivityViewModel.class);

        // init
        api = Api.getInstance(getContext(), viewModel);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // init views
        View rootView = inflater.inflate(R.layout.fragment_maps, container, false);
        buttonLoadMap = (Button) rootView.findViewById(R.id.button_load_map);
        sliderDetail = (Slider) rootView.findViewById(R.id.slider_results);

        // init listeners
        buttonLoadMap.setOnClickListener(v -> {
            loadMapData();
        });
        sliderDetail.setOnClickListener(v -> {
            sliderDetailValue = ((Slider) v).getValue();
        });

        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @SuppressLint("MissingPermission")
                @Override
                public void onMapReady(GoogleMap map) {
                    googleMap = map;
                    googleMap.setMyLocationEnabled(true);
                    locateMe(googleMap);
                }
            });
        }
    }

    @SuppressLint("MissingPermission")
    private void locateMe(@NonNull GoogleMap googleMap) {
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location == null) {
                Toast.makeText(getContext(), "Location is null.", Toast.LENGTH_SHORT).show();
                return;
            }

            LatLng me = new LatLng(location.getLatitude(), location.getLongitude());
//            if (marker != null) marker.remove();
//            marker = googleMap.addMarker(new MarkerOptions()
//                    .position(me)
//                    .title("You")
//                    .icon(BitmapDescriptorFactory.fromBitmap(createBitmapFromDrawable(getResources().getDrawable(R.drawable.marker_me)))));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(me, 17));
        });
    }

    public static void launchGMaps(Context context, LatLng latLng) {
        // Create a Uri from an intent string. Use the result to create an Intent.
        Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + latLng.latitude + "," + latLng.longitude);

        // Create an Intent from gmmIntentUri. Set the action to ACTION_VIEW
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // Make the Intent explicit by setting the Google Maps package
        mapIntent.setPackage("com.google.android.apps.maps");

        // Attempt to start an activity that can handle the Intent
        context.startActivity(mapIntent);
    }

    public void loadMapData() {
        // init markers
        PolylineOptions lineOptions = new PolylineOptions()
                .color(Color.BLUE)
                .width(5)
                .pattern(Arrays.<PatternItem>asList(new Dash(40), new Gap(10)));

        // load map data
        api.getAllStops(
                viewModel.nearbyBusesList.get(viewModel.selectedBusIndex),
                viewModel.nearbyStopsList.get(viewModel.selectedStopIndex).stopId,
                new Response.Listener<String>() {
                    @SuppressLint("WrongViewCast")
                    @Override
                    public void onResponse(String response) {
                        try {
                            clearMarkersAndReinitialize();

                            List<Stop> stopsList = new ArrayList<>();
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                Stop stop = new Stop(array.get(i).toString());
                                LatLng stopCoordinates = new LatLng(stop.lat, stop.lon);
                                MarkerOptions markerOptions = new MarkerOptions()
                                        .position(stopCoordinates)
                                        .title(stop.name)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
                                stopMarkers.add(googleMap.addMarker(markerOptions));

                                lineOptions.add(stopCoordinates);
                            }

                            // load places info
                            Log.e("sliderDetailValue", sliderDetailValue + "");
                            api.getPlaces(
                                    viewModel.nearbyBusesList.get(viewModel.selectedBusIndex),
                                    viewModel.nearbyStopsList.get(viewModel.selectedStopIndex).stopId,
                                    new Place.Filter(sliderDetailValue),
                                    new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            try {
                                                JSONArray array = new JSONArray(response);
                                                viewModel.placesList = new ArrayList<>();
                                                viewModel.placeTypesSet = new HashSet<>();
                                                for (int i = 0; i < array.length(); i++) {
                                                    Place place = new Place(array.get(i).toString());
                                                    LatLng placeCoordinates = new LatLng(place.lat, place.lon);
                                                    MarkerOptions placeMarkerOptions = new MarkerOptions()
                                                            .position(placeCoordinates)
                                                            .title(place.title)
                                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));
                                                    placeMarkers.add(googleMap.addMarker(placeMarkerOptions));
                                                }

                                                // lineOptions.addAll(getRouteLineMarkers());

                                                googleMap.addPolyline(lineOptions);
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

    private void clearMarkersAndReinitialize() {
        if (stopMarkers != null)
            for (Marker stopMarker : stopMarkers)
                stopMarker.remove();

        if (placeMarkers != null)
            for (Marker placeMarker : placeMarkers)
                placeMarker.remove();

        stopMarkers = new ArrayList<>();
        placeMarkers = new ArrayList<>();
    }

//    private List<LatLng> getRouteLineMarkers() {
//        List<LatLng> path = new ArrayList<>();
//
//        //Execute Directions API request
//        GeoApiContext context = new GeoApiContext.Builder()
//                .apiKey("AIzaSyCVTSwLXsgH-84isnXT6j0-clRiJBSjRR0")
//                .build();
//        DirectionsApiRequest req = DirectionsApi.getDirections(context, "41.385064,2.173403", "40.416775,-3.70379");
//        try {
//            DirectionsResult res = req.await();
//
//            Log.d("TEST", "1");
//
//            //Loop through legs and steps to get encoded polylines of each step
//            if (res.routes != null && res.routes.length > 0) {
//                DirectionsRoute route = res.routes[0];
//
//                Log.d("TEST", "2");
//
//                if (route.legs !=null) {
//
//                    Log.d("TEST", "3");
//
//                    for(int i=0; i<route.legs.length; i++) {
//                        DirectionsLeg leg = route.legs[i];
//                        if (leg.steps != null) {
//                            for (int j=0; j<leg.steps.length;j++){
//                                DirectionsStep step = leg.steps[j];
//                                if (step.steps != null && step.steps.length >0) {
//                                    for (int k=0; k<step.steps.length;k++){
//                                        DirectionsStep step1 = step.steps[k];
//                                        EncodedPolyline points1 = step1.polyline;
//                                        if (points1 != null) {
//                                            //Decode polyline and add points to list of route coordinates
//                                            List<com.google.maps.model.LatLng> coords1 = points1.decodePath();
//                                            for (com.google.maps.model.LatLng coord1 : coords1) {
//                                                path.add(new LatLng(coord1.lat, coord1.lng));
//                                            }
//                                        }
//                                    }
//                                } else {
//                                    EncodedPolyline points = step.polyline;
//                                    if (points != null) {
//                                        //Decode polyline and add points to list of route coordinates
//                                        List<com.google.maps.model.LatLng> coords = points.decodePath();
//                                        for (com.google.maps.model.LatLng coord : coords) {
//                                            path.add(new LatLng(coord.lat, coord.lng));
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                    }
//                }
//            }
//        } catch(Exception ex) {
//            ex.printStackTrace();
//        }
//
//        Log.d("TEST", path.size() + "");
//
//        return path;
//    }
}