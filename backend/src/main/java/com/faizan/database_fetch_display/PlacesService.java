package com.faizan.database_fetch_display;

import com.model.PlaceInfo;
import com.model.StopInfo;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface PlacesService {
    public String saveByStopInfo(StopInfo stopInfo);


    List<PlaceInfo> findByIdIn(List<String> stopIds);

    List<PlaceInfo> findAll();
}
