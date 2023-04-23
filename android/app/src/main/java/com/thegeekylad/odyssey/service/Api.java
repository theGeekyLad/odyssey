package com.thegeekylad.odyssey.service;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thegeekylad.odyssey.R;
import com.thegeekylad.odyssey.adapter.SimplePlacesAdapter;
import com.thegeekylad.odyssey.model.Bus;
import com.thegeekylad.odyssey.model.Place;
import com.thegeekylad.odyssey.model.Stop;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class Api {
    private static Api api;
    private static RequestQueue queue;
//    private static final String BASE_URL = "http://172.20.10.3:3000";
    private static final String BASE_URL = "http://192.168.128.71:8080";
    public static String uid = null;

    private MainActivityViewModel viewModel;

    public Api(Context context, MainActivityViewModel viewModel) {
        if (queue == null) queue = Volley.newRequestQueue(context);

        // initialize ui
        Api.uid = context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE).getString("uid", null);
        if (Api.uid == null) {
            Api.uid = UUID.randomUUID().toString();
            context.getSharedPreferences(context.getResources().getString(R.string.app_name), MODE_PRIVATE).edit().putString("uid", Api.uid).apply();
        }

        this.viewModel = viewModel;
    }

    public static Api getInstance(Context context, MainActivityViewModel viewModel) {
        if (api == null)
            api = new Api(context, viewModel);
        return api;
    }

    public void getNearbyStops(String lat, String lng, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/nearby-stops-db?lat=%s&lon=%s&max_distance=%d", lat, lng, 200),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.showProgress.setValue(false);
                        onSuccess.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewModel.showProgress.setValue(false);
                        onError.onErrorResponse(error);
                    }
                });

        queue.add(stringRequest);
    }

    public void getBuses(String stopId, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/getDeparturesFromDatabase?global_stop_id=" + stopId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.showProgress.setValue(false);
                        onSuccess.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewModel.showProgress.setValue(false);
                        onError.onErrorResponse(error);
                    }
                });

        queue.add(stringRequest);
    }

//    public void getAllPlaces(
//            String busId,
//            Place.Filter filter,
//            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
//        viewModel.showProgress.setValue(true);
//
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.GET,
//                BASE_URL + String.format("/allPlaces"),
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//                        viewModel.showProgress.setValue(false);
//                        onSuccess.onResponse(response);
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//                        viewModel.showProgress.setValue(false);
//                        onError.onErrorResponse(error);
//                    }
//                });
//
//        queue.add(stringRequest);
//    }

    public void getPlaces(
            Bus bus,
            String stopId,
            Place.Filter filter,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        String url = BASE_URL + String.format("/places/placesByFilter?bus_id=%s&stop_id=%s&place_type=%s&radius=%s&items_per_stop=%s", bus.busName + (!bus.headSign.isEmpty() ? "-" + bus.headSign : ""), stopId, filter.type, filter.radius, (int) filter.detail);
        Log.e("URL", url);
        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.showProgress.setValue(false);
                        try {
                            JSONArray array = new JSONArray(response);
                            for (int i = 0; i < array.length(); i++) {
                                Place place = new Place(array.get(i).toString());
                                if (filter.type != null && !filter.type.equalsIgnoreCase(place.type))
                                    array.remove(i);
                            }
                            onSuccess.onResponse(array.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                            onSuccess.onResponse(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewModel.showProgress.setValue(false);
                        onError.onErrorResponse(error);
                    }
                }
        );

        queue.add(stringRequest);
    }

    public void getAllStops(
            Bus bus,
            String stopId,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/get_route_by_key?key=%s&global_stop_id=%s", bus.busName + (!bus.headSign.isEmpty() ? "-" + bus.headSign : ""), stopId),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.showProgress.setValue(false);
                        onSuccess.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewModel.showProgress.setValue(false);
                        onError.onErrorResponse(error);
                    }
                });

        queue.add(stringRequest);
    }

    public void getMapView(
            String busId,
            float radius,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/mapView"),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        viewModel.showProgress.setValue(false);
                        onSuccess.onResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        viewModel.showProgress.setValue(false);
                        onError.onErrorResponse(error);
                    }
                });

        queue.add(stringRequest);
    }

//    public void postOpen(Double lat, Double lng, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                BASE_URL_FLASK + "/nearbyopenplaces",
//                onSuccess,
//                onError) {
//            @Override
//            public String getBodyContentType() {
//                return "application/json; charset=utf-8";
//            }
//
//            @Override
//            public byte[] getBody() throws AuthFailureError {
//                try {
//                    JSONObject requestBody = new JSONObject();
//                    requestBody.put("latitude", lat);
//                    requestBody.put("longitude", lng);
//
//                    return requestBody.toString().getBytes("utf-8");
//                } catch (Exception uee) {
//                    uee.printStackTrace();
//                    return null;
//                }
//            }
//        };
//
//        queue.add(stringRequest);
//    }
}
