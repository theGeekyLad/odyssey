package com.faizan.database_fetch_display;

public class RouteDepartureInfo {
    private String globalRouteId;
    private String busName;
    private String directionHeader;

    public RouteDepartureInfo() {
    }

    public RouteDepartureInfo(String globalRouteId, String busName, String directionHeader) {
        this.globalRouteId = globalRouteId;
        this.busName = busName;
        this.directionHeader = directionHeader;
    }

    public String getGlobalRouteId() {
        return globalRouteId;
    }

    public void setGlobalRouteId(String globalRouteId) {
        this.globalRouteId = globalRouteId;
    }

    public String getBusName() {
        return busName;
    }

    public void setBusName(String busName) {
        this.busName = busName;
    }

    public String getDirectionHeader() {
        return directionHeader;
    }

    public void setDirectionHeader(String directionHeader) {
        this.directionHeader = directionHeader;
    }

    @Override
    public String toString() {
        return "RouteDepartureInfo{" +
                "globalRouteId='" + globalRouteId + '\'' +
                ", busName='" + busName + '\'' +
                ", directionHeader='" + directionHeader + '\'' +
                '}';
    }
}

