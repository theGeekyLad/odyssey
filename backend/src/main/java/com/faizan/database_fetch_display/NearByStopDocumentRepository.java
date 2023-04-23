package com.faizan.database_fetch_display;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NearByStopDocumentRepository extends MongoRepository<NearbyStopsResponse, String> {
}
