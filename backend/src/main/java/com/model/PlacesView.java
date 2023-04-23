package com.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlacesView {
    String bus_id;
    String stop_id;
    List<String> place_type;
    double radius;
    int items_per_stop;
}
