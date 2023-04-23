package com.faizan.database_fetch_display;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class StopDeparturesResponse {
    @JsonProperty("route_departures")
    private List<RouteDeparture> routeDepartures;

    // Getters and setters
}
