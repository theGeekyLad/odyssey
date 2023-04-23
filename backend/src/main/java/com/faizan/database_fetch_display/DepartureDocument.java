package com.faizan.database_fetch_display;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "departures")
public class DepartureDocument {
    @Id
    private String global_stop_id;
    private List<RouteDepartureInfo> departures;

    public DepartureDocument() {
    }

    public DepartureDocument(String global_stop_id, List<RouteDepartureInfo> departures) {
        this.global_stop_id = global_stop_id;
        this.departures = departures;
    }

    public String getGlobal_stop_id() {
        return global_stop_id;
    }

    public void setGlobal_stop_id(String global_stop_id) {
        this.global_stop_id = global_stop_id;
    }

    public List<RouteDepartureInfo> getDepartures() {
        return departures;
    }

    public void setDepartures(List<RouteDepartureInfo> departures) {
        this.departures = departures;
    }
}

