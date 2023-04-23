package com.thegeekylad.odyssey.model;

import java.util.HashMap;
import java.util.List;

public class StopPlaces {
    public StopPlaces(HashMap<Stop, List<Place>> stopPlacesList) {
        this.stopPlacesList = stopPlacesList;
    }

    public HashMap<Stop, List<Place>> stopPlacesList;
}
