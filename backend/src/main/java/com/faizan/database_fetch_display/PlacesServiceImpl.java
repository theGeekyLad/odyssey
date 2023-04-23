package com.faizan.database_fetch_display;

import com.model.GooglePlacesResponse;
import com.model.Place;
import com.model.PlaceInfo;
import com.model.StopInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class PlacesServiceImpl implements PlacesService {

    @Autowired
    PlacesInfoRepository placesInfoRepository;

    // TODO: Always remove API_KEY before committing the code
    private static final String API_KEY = "XXXXXXXXXXXXXXXXXX";
    private static final String API_URL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json";

    @Override
    public String saveByStopInfo(StopInfo stopInfo) {
        PlaceInfo placeInfo = PlaceInfo.builder().stopId(stopInfo.getGlobal_stop_id())
                .places(getPlacesToExplore(stopInfo)).build();
        placesInfoRepository.save(placeInfo);
        return "Saved successfully all the places for given stop";
    }

    @Override
    public List<PlaceInfo> findByIdIn(List<String> stopIds) {
        return placesInfoRepository.findAllById(stopIds);
    }

    @Override
    public List<PlaceInfo> findAll() {
        return placesInfoRepository.findAll();
    }

    public List<Place> getPlacesToExplore(StopInfo stopInfo) {
        RestTemplate restTemplate = new RestTemplate();

        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(API_URL)
                .queryParam("location", String.format("%f,%f", stopInfo.getLat(), stopInfo.getLon()))
                .queryParam("radius", 3218) // 2 miles in meters
                .queryParam("type", "tourist_attraction")
                .queryParam("key", API_KEY);

        ResponseEntity<GooglePlacesResponse> responseEntity =
                restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null, GooglePlacesResponse.class);

        List<Place> places = new ArrayList<>();
        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            GooglePlacesResponse response = responseEntity.getBody();
            if (response != null && response.getResults() != null) {
                for (GooglePlacesResponse.Result result : response.getResults()) {
                    Place place = getPlace(result);
                    if (place != null) {
                        places.add(place);
                    }
                }
            }
        }
        return places;
    }

    private Place getPlace(GooglePlacesResponse.Result result) {
        if (result == null) {
            return null;
        }

        return Place.builder().rating(result.getRating())
                .userRatingTotal(result.getUser_ratings_total())
                .placeTypes(result.getTypes())
                .name(result.getName())
                .lat(result.getGeometry().getLocation().getLat())
                .lng(result.getGeometry().getLocation().getLng())
                .isOpen(result.getOpening_hours() != null && result.getOpening_hours().isOpen_now())
                .icon(result.getIcon()).build();
    }
}
