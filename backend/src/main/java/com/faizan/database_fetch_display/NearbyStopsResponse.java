package com.faizan.database_fetch_display;

import java.util.List;

public class NearbyStopsResponse {
    private List<NearbyStop> stops;
    // Add any other properties as needed

    // Getters and setters for each property
    public List<NearbyStop> getStops() {
        return stops;
    }

    public void setStops(List<NearbyStop> stops) {
        this.stops = stops;
    }
}
