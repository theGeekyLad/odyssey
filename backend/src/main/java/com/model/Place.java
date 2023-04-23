package com.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Place {

    @JsonProperty("rating")
    private double rating;
    @JsonProperty("userRatingTotal")
    private int userRatingTotal;
    @JsonProperty("placeTypes")
    private String[] placeTypes;
    @JsonProperty("name")
    private String name;
    @JsonProperty("lat")
    private double lat;
    @JsonProperty("lng")
    private double lng;
    @JsonProperty("isOpen")
    private boolean isOpen;
    @JsonProperty("icon")
    private String icon;
}
