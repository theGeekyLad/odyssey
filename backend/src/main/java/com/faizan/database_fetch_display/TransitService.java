package com.faizan.database_fetch_display;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface TransitService {
    //List<RouteDetails> getAllRouteDetails();
    NearbyStopsResponse getNearbyStops(double lat, double lon, int radius);
    List<RouteDepartureInfo> getRouteDepartures(String globalStopId);
    //RouteDetails getRouteDetails(String globalRouteId);
    void saveRouteDetails(RouteDetails routeDetails);

    List<NearbyStop> findStopsByRoute(String routName, String globalStopId);

    void saveDepartures(String globalStopId, List<RouteDepartureInfo> departures);
    //Optional<Map<String, List<NearbyStop>>> findRouteStopsByRouteName(String routeName);
    RouteDetails getRouteDetails(String globalRouteId);

    List<RouteDetailsDocument> findAll();
}
