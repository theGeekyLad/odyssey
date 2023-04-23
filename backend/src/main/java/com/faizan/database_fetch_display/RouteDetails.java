package com.faizan.database_fetch_display;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RouteDetails {
    private Map<String, List<NearbyStop>> routesStopsMap;

    public RouteDetails() {
    }

    public RouteDetails(Map<String, List<NearbyStop>> routesStopsMap) {
        this.routesStopsMap = routesStopsMap;
    }

    public Map<String, List<NearbyStop>> getRoutesStopsMap() {
        return routesStopsMap;
    }

    public void setRoutesStopsMap(Map<String, List<NearbyStop>> routesStopsMap) {
        this.routesStopsMap = routesStopsMap;
    }

    @Override
    public String toString() {
        return "RouteDetails{" +
                "routesStopsMap=" + routesStopsMap +
                '}';
    }
}

