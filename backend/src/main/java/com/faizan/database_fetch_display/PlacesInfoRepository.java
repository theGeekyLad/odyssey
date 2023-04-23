package com.faizan.database_fetch_display;

import com.model.PlaceInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlacesInfoRepository extends MongoRepository<PlaceInfo, String> {

}
