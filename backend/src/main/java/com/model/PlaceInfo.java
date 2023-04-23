package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "placesInfo")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlaceInfo {
    @Id
    @JsonProperty("globalStopId")
    private String stopId;
    private List<Place> places;
}
