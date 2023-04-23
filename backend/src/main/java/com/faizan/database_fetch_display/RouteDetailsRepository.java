package com.faizan.database_fetch_display;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RouteDetailsRepository extends MongoRepository<RouteDetailsDocument, String> {
    @Query("{'routesStopsMap.?0': {'$exists': true}}")
    RouteDetailsDocument findByRouteName(String routeName);
}

