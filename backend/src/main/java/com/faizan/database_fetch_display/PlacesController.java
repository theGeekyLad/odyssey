package com.faizan.database_fetch_display;
import com.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/places")
public class PlacesController {

    @Autowired
    private PlacesService placesService;

    @Autowired
    private TransitService transitService;

    @PostMapping("/saveAll")
    public String saveAll() {
        List<RouteDetailsDocument> routeDetailsDocuments = transitService.findAll();

        Set<NearbyStop> nearbyStops = new HashSet<>();
        for (RouteDetailsDocument routeDetail : routeDetailsDocuments) {
            Set<NearbyStop> temp = routeDetail.getRoutesStopsMap().values().stream().flatMap(Collection::stream)
                    .collect(Collectors.toSet());

            for (NearbyStop a : temp) {
                nearbyStops.add(a);
            }
        }

        for (NearbyStop nearbyStop : nearbyStops) {
            StopInfo stopInfo = StopInfo.builder()
                    .global_stop_id(nearbyStop.getId())
                    .lat(nearbyStop.getLat())
                    .lon(nearbyStop.getLon())
                    .name(nearbyStop.getName()).build();
            String result = saveByStopInfo(stopInfo);
            System.out.println(result);
        }

        return "Successfully updated locations";
    }
    public String saveByStopInfo(StopInfo stopInfo) {
        return placesService.saveByStopInfo(stopInfo);
    }

    @GetMapping("/placesByCondition")
    public List<Place> getPlacesInfoByCondition(@RequestBody GetStopView getStopView) {
        return placesInfoByCondition(getStopView);
    }
    public List<Place> placesInfoByCondition(GetStopView getStopView) {
        List<String> stopIds = getStopView.getStopInfos().stream()
                .map(StopInfo::getGlobal_stop_id)
                .collect(Collectors.toList());
        List<PlaceInfo> placeInfos = placesService.findByIdIn(stopIds);
        List<Place> filteredPlaces = new ArrayList<>();

        for (PlaceInfo placeInfo : placeInfos) {

            StopInfo stopInfo = getStopView.getStopInfos().stream()
                    .filter(si -> si.getGlobal_stop_id().equals(placeInfo.getStopId()))
                    .findFirst().orElse(null);
            List<Place> places = placeInfo.getPlaces();
            List<Place> filteredStopPlaces = new ArrayList<>();

            for (Place place : places) {

                if (calculateDistance(stopInfo.getLat(), stopInfo.getLon(), place.getLat(), place.getLng()) <= getStopView.getRadius()) {
                    if (getStopView.getTypes() != null && getStopView.getTypes().size() > 0) {
                        if (getStopView.getTypes().contains(place.getPlaceTypes()))
                            filteredStopPlaces.add(place);
                    } else {
                        filteredStopPlaces.add(place);
                    }

                }
                if (filteredStopPlaces.size() >= getStopView.getItemPerStop() && getStopView.getItemPerStop() > 0) {
                    filteredStopPlaces = filteredStopPlaces.subList(0, getStopView.getItemPerStop());
                }
            }
            filteredPlaces.addAll(filteredStopPlaces);
        }
        return filteredPlaces;
    }

    @GetMapping("/placesByFilter")
    public List<Place> getPlacesByCondition(@RequestParam(value = "bus_id") String bus_id,
                                            @RequestParam(value = "stop_id") String stop_id,
                                            @RequestParam(value = "radius") double radius,
                                            @RequestParam(value = "items_per_stop") int items_per_stop) {
        PlacesView placesView = new PlacesView();
        placesView.setBus_id(bus_id);
        placesView.setStop_id(stop_id);
        placesView.setPlace_type(null);
        placesView.setRadius(radius);
        placesView.setItems_per_stop(items_per_stop);
        List<NearbyStop> allStopsFromCurrent = transitService.findStopsByRoute(placesView.getBus_id(), placesView.getStop_id());
        List<StopInfo> stopInfos = new ArrayList<>();

        for (NearbyStop stop : allStopsFromCurrent) {
            StopInfo stopInfo = StopInfo.builder()
                    .global_stop_id(stop.getId())
                    .lat(stop.getLat())
                    .lon(stop.getLon())
                    .name(stop.getName()).build();

            stopInfos.add(stopInfo);
        }
        GetStopView getStopView = GetStopView.builder()
                .stopInfos(stopInfos)
                .itemPerStop(placesView.getItems_per_stop())
                .radius(placesView.getRadius())
                .types(placesView.getPlace_type()).build();

        return placesInfoByCondition(getStopView);
    }

    private double calculateDistance(double lat1, double lng1, double lat2, double lng2) {
        double R = 6371; // Earth radius in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lng2 - lng1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c; // Distance in km
        return d / 1.609; // Distance in miles
    }

}
