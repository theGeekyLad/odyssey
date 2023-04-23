package com.thegeekylad.odyssey.model;

import android.util.Log;

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
        title = jsonObject.getString("name");
        lat = jsonObject.getDouble("lat");
        lon = jsonObject.getDouble("lng");
        type = jsonObject.getJSONArray("placeTypes").getString(0);
        iconUrl = jsonObject.getString("icon");
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

        public Filter(float detail) {
            this();
            this.detail = detail;
        }
    }
}
