package com.thegeekylad.odyssey.model;

import com.thegeekylad.odyssey.util.Constants;

import org.json.JSONException;
import org.json.JSONObject;

public class Place {
    public String title;
    public double lat, lon;
    public String type;
    public String iconUrl;

    public Place(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        title = jsonObject.getString("title");
        lat = jsonObject.getDouble("lat");
        lon = jsonObject.getDouble("lon");
        type = jsonObject.getString("type");
        iconUrl = jsonObject.getString("icon_url");
    }

    public static class Filter {
        public float radius;
        public float detail;
        public String type;

        public Filter() {
            radius = Constants.RADIUS_DEFAULT;
            detail = 3;
            type = null;
        }
    }
}
