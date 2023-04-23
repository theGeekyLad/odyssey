package com.thegeekylad.odyssey.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.thegeekylad.odyssey.model.Bus;
import com.thegeekylad.odyssey.model.Place;
import com.thegeekylad.odyssey.model.Stop;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivityViewModel extends ViewModel {
    public MutableLiveData<Boolean> showProgress = new MutableLiveData<>(false);
    public List<Stop> nearbyStopsList = new ArrayList<>();
    public int selectedStopIndex = 0;
    public List<Bus> nearbyBusesList = new ArrayList<>();
    public int selectedBusIndex = 0;
    public List<Place> placesList = new ArrayList<>();
    public Set<String> placeTypesSet = new HashSet<>();
}
