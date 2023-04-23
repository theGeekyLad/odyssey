package com.faizan.database_fetch_display;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
public class TransitController {

    @Autowired
    private TransitService transitService;

    @Autowired
    private DepartureDocumentRepository departureDocumentRepository;

    @Autowired
    private NearByStopDocumentRepository nearByStopDocumentRepository;

    public TransitController(TransitService transitService) {
        this.transitService = transitService;
    }
    @GetMapping("/nearby-stops")
    public NearbyStopsResponse getNearbyStops(
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lon") double lon,
            @RequestParam(value = "radius", defaultValue = "500") int radius) {
        return transitService.getNearbyStops(lat, lon, radius);
    }

    @GetMapping("/nearby-stops-db")
    public NearbyStopsResponse getNearbyStopsDB(
            @RequestParam(value = "lat") double lat,
            @RequestParam(value = "lon") double lon,
            @RequestParam(value = "radius", defaultValue = "500") int radius) {
        return nearByStopDocumentRepository.findAll().get(0);
    }
    @GetMapping("/stop-departures")
    public List<RouteDepartureInfo> getRouteDepartures(@RequestParam("global_stop_id") String globalStopId) {
        List<RouteDepartureInfo> departures = transitService.getRouteDepartures(globalStopId);
        transitService.saveDepartures(globalStopId, departures);
        return departures;
    }

    @PostMapping("/saveLocation")
    public String saveLocation(@RequestParam(value = "lat") double lat,
                               @RequestParam(value = "lon") double lon,
                               @RequestParam(value = "radius", defaultValue = "500") int radius) {
        NearbyStopsResponse response =  transitService.getNearbyStops(lat, lon, radius);
        nearByStopDocumentRepository.save(response);
        return "successfully saved";
    }

    @GetMapping("/getDeparturesFromDatabase")
    public ResponseEntity<?> getDeparturesFromDatabase(@RequestParam String global_stop_id) {
        Optional<DepartureDocument> departureDocumentOpt = departureDocumentRepository.findById(global_stop_id);

        if (departureDocumentOpt.isPresent()) {
            return ResponseEntity.ok(departureDocumentOpt.get().getDepartures());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Departures not found for the provided global_stop_id");
        }
    }

    @GetMapping("/route_details")
    public ResponseEntity<RouteDetails> getRouteDetails(@RequestParam("global_route_id") String globalRouteId) {
        RouteDetails routeDetails = transitService.getRouteDetails(globalRouteId);
        transitService.saveRouteDetails(routeDetails);
        return ResponseEntity.ok(routeDetails);
    }

//    @GetMapping("/new_route_details")
//    public ResponseEntity<Map<String, List<NearbyStop>>> new_getRouteDetails(@RequestParam("route_name") String routeName) {
//        return transitService.findRouteStopsByRouteName(routeName)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

//    @GetMapping("/get_route_details")
//    public ResponseEntity<List<RouteDetails>> getAllRouteDetails() {
//        List<RouteDetails> routeDetailsList = transitService.getAllRouteDetails();
//        return new ResponseEntity<>(routeDetailsList, HttpStatus.OK);
//    }

//    @GetMapping("/findRouteByName")
//    public ResponseEntity<Map<String, List<NearbyStop>>> getRouteDetailsByName(@RequestParam String routeName) {
//        return transitService.findRouteStopsByRouteName(routeName).map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


//    @GetMapping("/get_route_by_key")
//    public List<NearbyStop> getRouteByKey(@RequestParam(value = "key") String key) {
//        return transitService.findStopsByRoute(key);
//    }
    @GetMapping("/get_route_by_key")
    public List<NearbyStop> getRouteByKey(
        @RequestParam(value = "key") String key,
        @RequestParam(value = "global_stop_id", required = false) String globalStopId) {
        return transitService.findStopsByRoute(key, globalStopId);
}

}