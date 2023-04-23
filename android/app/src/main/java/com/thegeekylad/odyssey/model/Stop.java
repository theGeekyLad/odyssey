package com.thegeekylad.odyssey.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Stop {
    public String name;
    public Double lat, lon;
    public String stopId;
    public Stop(String name, Double lat, Double lon, String stopId) {
        this.name = name;
        this.lat = lat;
        this.lon = lon;
        this.stopId = stopId;
    }

    public Stop(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        name = jsonObject.  getString("stop_name");
        lat = jsonObject.getDouble("lat");
        lon = jsonObject.getDouble("lon");
        stopId = jsonObject.getString("stop_id");
    }

    @Override
    public String toString() {
        try {
            JSONObject obj = new JSONObject();
            obj.put("stop_name", name);
            obj.put("lat", lat);
            obj.put("lon", lon);
            obj.put("stop_id", stopId);

            return obj.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
