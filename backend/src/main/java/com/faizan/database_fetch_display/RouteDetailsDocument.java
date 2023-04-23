package com.faizan.database_fetch_display;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "route_details")
public class RouteDetailsDocument {
    @Id
    private String id;
    private Map<String, List<NearbyStop>> routesStopsMap;

    public RouteDetailsDocument(Map<String, List<NearbyStop>> routesStopsMap) {
        this.routesStopsMap = routesStopsMap;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, List<NearbyStop>> getRoutesStopsMap() {
        return routesStopsMap;
    }

    public void setRoutesStopsMap(Map<String, List<NearbyStop>> routesStopsMap) {
        this.routesStopsMap = routesStopsMap;
    }

    @Override
    public String toString() {
        return "RouteDetailsDocument{" +
                "id='" + id + '\'' +
                ", routesStopsMap=" + routesStopsMap +
                '}';
    }
}
