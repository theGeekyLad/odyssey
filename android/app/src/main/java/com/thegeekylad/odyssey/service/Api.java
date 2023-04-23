package com.thegeekylad.odyssey.service;

import static android.content.Context.MODE_PRIVATE;
import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.thegeekylad.odyssey.R;
import com.thegeekylad.odyssey.model.Place;
import com.thegeekylad.odyssey.model.Stop;
import com.thegeekylad.odyssey.viewmodel.MainActivityViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Api {
    private static Api api;
    private static RequestQueue queue;
    private static final String BASE_URL = "http://172.20.10.3:3000";
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

//    public void postMe(Double lat, Double lng, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
//        StringRequest stringRequest = new StringRequest(
//                Request.Method.POST,
//                BASE_URL + "/location/save",
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
//                    requestBody.put("uid", Api.uid);
//                    requestBody.put("latitude", lat);
//                    requestBody.put("longitude", lng);
//                    requestBody.put("radius", 0);
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

    public void getNearbyStops(String lat, String lng, Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/nearbyStops?lat=%s&lng=%s", lat, lng),
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
                BASE_URL + String.format("/buses"),
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

    public void getAllPlaces(
            String busId,
            Place.Filter filter,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/allPlaces"),
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

    public void getPlaces(
            String stopId,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/places"),
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

    public void getAllStops(
            String busId,
            Response.Listener<String> onSuccess, Response.ErrorListener onError) {
        viewModel.showProgress.setValue(true);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                BASE_URL + String.format("/allStops"),
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
