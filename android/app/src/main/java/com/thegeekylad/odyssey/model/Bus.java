package com.thegeekylad.odyssey.model;

import org.json.JSONException;
import org.json.JSONObject;

public class Bus {
    public String busId;
    public String busName;
    public String headSign;

    public Bus(String busId, String busName, String headSign) {
        this.busId = busId;
        this.busName = busName;
        this.headSign = headSign;
    }

    public Bus(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        busName = jsonObject.getString("busName");
        busId = jsonObject.getString("globalRouteId");
        headSign = jsonObject.getString("directionHeader");
    }
}
