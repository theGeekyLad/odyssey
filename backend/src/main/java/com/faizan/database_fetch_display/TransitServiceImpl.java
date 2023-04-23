package com.faizan.database_fetch_display;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;

import java.io.IOException;
import java.sql.SQLOutput;
import java.util.*;


@Service
public class TransitServiceImpl implements TransitService {
    private static final String API_URL = "https://external.transitapp.com/v3/public/nearby_stops";
    private static final String DEPARTURES_API_URL = "https://external.transitapp.com/v3/public/stop_departures";
    private static final String ROUTE_DETAILS_API_URL = "https://external.transitapp.com/v3/public/route_details";



    private final MongoTemplate mongoTemplate;

    @Autowired
    private RouteDetailsRepository routeDetailsRepository;

    @Autowired
    private DepartureDocumentRepository departureDocumentRepository;


    public TransitServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void saveRouteDetails(RouteDetails routeDetails) {
        RouteDetailsDocument routeDetailsDocument = new RouteDetailsDocument(routeDetails.getRoutesStopsMap());
        mongoTemplate.save(routeDetailsDocument);
    }

//    @Override
//    public List<RouteDetails> getAllRouteDetails() {
//        List<RouteDetailsDocument> routeDetailsDocuments = routeDetailsRepository.findAll();
//        List<RouteDetails> routeDetailsList = new ArrayList<>();
//        for (RouteDetailsDocument routeDetailsDocument : routeDetailsDocuments) {
//            routeDetailsList.add(new RouteDetails(routeDetailsDocument.getRoutesStopsMap()));
//        }
//        return routeDetailsList;
//    }

    public List<RouteDetailsDocument> findAllRouteDetails() {
        return mongoTemplate.findAll(RouteDetailsDocument.class);
    }
    @Override
    public NearbyStopsResponse getNearbyStops(double lat, double lon, int max_distance) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();

        // TODO: always remove API KEY

        headers.set("apiKey", "XXXXXXXXXXXXXXXXXXXX");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String requestUrl = API_URL + "?lat=" + lat + "&lon=" + lon + "&max_distance=" + max_distance;
        ResponseEntity<String> response = restTemplate.exchange(
                requestUrl, HttpMethod.GET, entity, String.class);
        String jsonResponse = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            NearbyStopsResponse nearbyStopsResponse = objectMapper.readValue(jsonResponse, NearbyStopsResponse.class);
            return nearbyStopsResponse;
        } catch (IOException e) {
            throw new RuntimeException("Error deserializing JSON response", e);
        }
    }

    @Override
    public List<RouteDepartureInfo> getRouteDepartures(String globalStopId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", "XXXXXXXXXXXXXXXXXXXX");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String requestUrl = DEPARTURES_API_URL + "?global_stop_id=" + globalStopId;
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode routeDeparturesNode = rootNode.path("route_departures");
            List<RouteDepartureInfo> routeDepartureInfos = new ArrayList<>();

            for (JsonNode routeDeparture : routeDeparturesNode) {
                String globalRouteId = routeDeparture.get("global_route_id").asText();
                String busName = routeDeparture.get("sorting_key").asText();
                String directionHeader = routeDeparture.get("itineraries").get(0).get("direction_headsign").asText();
                routeDepartureInfos.add(new RouteDepartureInfo(globalRouteId, busName, directionHeader));
            }

            return routeDepartureInfos;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing JSON response", e);
        }
    }

    @Override
    public void saveDepartures(String globalStopId, List<RouteDepartureInfo> departures) {
        DepartureDocument departureDocument = new DepartureDocument(globalStopId, departures);
        departureDocumentRepository.save(departureDocument);
    }

    @Override
    public RouteDetails getRouteDetails(String globalRouteId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("apiKey", "XXXXXXXXXXXXXXXXXXXX");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String requestUrl = ROUTE_DETAILS_API_URL + "?global_route_id=" + globalRouteId;
        ResponseEntity<String> response = restTemplate.exchange(requestUrl, HttpMethod.GET, entity, String.class);
        String responseBody = response.getBody();


        ObjectMapper objectMapper = new ObjectMapper();
        try {
            JsonNode rootNode = objectMapper.readTree(responseBody);
            JsonNode itinerariesNode = rootNode.path("itineraries");
            Map<String, List<NearbyStop>> routeDetailsMap = new HashMap<>();

            for (JsonNode itinerary : itinerariesNode) {
                String directionHeadSign = itinerary.get("direction_headsign").asText();
                System.out.println();
                System.out.println();
                System.out.println("Head Sign-"+directionHeadSign);
                System.out.println();
                System.out.println();
                String sortingKey = rootNode.path("route").get("sorting_key").asText();
                JsonNode stopsNode = itinerary.get("stops");
                List<NearbyStop> stopsList = new ArrayList<>();

                for (JsonNode stop : stopsNode) {
                    String id = stop.get("global_stop_id").asText();
                    double lat = stop.get("stop_lat").asDouble();
                    double lon = stop.get("stop_lon").asDouble();
                    String name = stop.get("stop_name").asText();
                    NearbyStop stopObj=new NearbyStop();
                    stopObj.setId(id);
                    stopObj.setLat(lat);
                    stopObj.setLon(lon);
                    stopObj.setName(name);
                    stopsList.add(stopObj);
                }
                String key=sortingKey +"-"+directionHeadSign;
                if (routeDetailsMap.containsKey(key)) {
                    if (stopsList.size() > routeDetailsMap.get(key).size()) {
                        routeDetailsMap.put(key, stopsList);
                    }
                } else {
                    routeDetailsMap.put(key, stopsList);
                }
            }
            RouteDetails routeDetails = new RouteDetails(routeDetailsMap);
            return routeDetails;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Error deserializing JSON response", e);
        }
    }

    @Override
    public List<RouteDetailsDocument> findAll() {
        return routeDetailsRepository.findAll();
    }

    //    public Optional<Map<String, List<NearbyStop>>> findRouteStopsByRouteName(String routeName) {
//        return routeDetailsRepository.findAll().stream()
//                .map(RouteDetails::getRoutesStopsMap)
//                .filter(map -> map.containsKey(routeName))
//                .findFirst();
//    }
//
//    public List<NearbyStop> findStopsByRoute(String routeName) {
//        RouteDetailsDocument routeDetailsDocument = routeDetailsRepository.findByRouteName(routeName);
//        if (routeDetailsDocument != null) {
//            return routeDetailsDocument.getRoutesStopsMap().getOrDefault(routeName, Collections.emptyList());
//        } else {
//            return Collections.emptyList();
//        }
//    }
    public List<NearbyStop> findStopsByRoute(String routeName, String globalStopId) {
        RouteDetailsDocument routeDetailsDocument = routeDetailsRepository.findByRouteName(routeName);
        if (routeDetailsDocument != null) {
            List<NearbyStop> nearbyStops = routeDetailsDocument.getRoutesStopsMap().getOrDefault(routeName, Collections.emptyList());
            if (globalStopId == null) {
                return nearbyStops;
            } else {
                int startIndex = -1;
                for (int i = 0; i < nearbyStops.size(); i++) {
                    if (nearbyStops.get(i).getId().equals(globalStopId)) {
                        startIndex = i;
                        break;
                    }
                }
                return startIndex != -1 ? nearbyStops.subList(startIndex, nearbyStops.size()) : Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }
    }







}