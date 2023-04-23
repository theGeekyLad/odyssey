package com.model;

import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GooglePlacesResponse {
    private String status;
    private List<Result> results;
    // getters and setters omitted for brevity

    @Data
    public static class Result {
        private String name;
        private Geometry geometry;
        private String[] types;
        private OpeningHours opening_hours;
        private double rating;
        private int user_ratings_total;

        private String icon;
        @Data
        public static class Geometry {
            private Location location;
            // getters and setters omitted for brevity

            @Data
            public static class Location {
                private double lat;
                private double lng;
                // getters and setters omitted for brevity
            }
        }

        @Data
        public static class OpeningHours {
            private boolean open_now;
            // getters and setters omitted for brevity
        }
    }
}
