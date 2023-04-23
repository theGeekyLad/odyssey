package com.faizan.database_fetch_display;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RouteDeparture {
    private String globalRouteId;
    private String busName;
    private String directionHeader;

    // Constructor
    public RouteDeparture(String globalRouteId, String busName, String directionHeader) {
        this.globalRouteId = globalRouteId;
        this.busName = busName;
        this.directionHeader = directionHeader;
    }

    // Getters
    public String getGlobalRouteId() {
        return globalRouteId;
    }

    public String getBusName() {
        return busName;
    }

    public String getDirectionHeader() {
        return directionHeader;
    }

    // Setters (optional)
    public void setGlobalRouteId(String globalRouteId) {
        this.globalRouteId = globalRouteId;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public void setDirectionHeader(String directionHeader) {
        this.directionHeader = directionHeader;
    }

    // toString (optional)
    @Override
    public String toString() {
        return "RouteDeparture{" +
                "globalRouteId='" + globalRouteId + '\'' +
                ", busName='" + busName + '\'' +
                ", directionHeader='" + directionHeader + '\'' +
                '}';
    }
}

