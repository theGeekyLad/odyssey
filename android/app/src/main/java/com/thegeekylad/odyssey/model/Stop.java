package com.thegeekylad.odyssey.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Stop {
    public String name;
    public Double lat, lon;
    public String stopId;
    public double distance;

    public Stop(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        name = jsonObject.  getString("stop_name");
        lat = jsonObject.getDouble("stop_lat");
        lon = jsonObject.getDouble("stop_lon");
        stopId = jsonObject.getString("global_stop_id");
        distance = jsonObject.getDouble("distance");
    }

    @Override
    public String toString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("stop_name", name);
            obj.put("stop_lat", lat);
            obj.put("stop_lon", lon);
            obj.put("global_stop_id", stopId);
            obj.put("distance", distance);

            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
