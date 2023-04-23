package com.faizan.database_fetch_display;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface DepartureDocumentRepository extends MongoRepository<DepartureDocument, String> {
}
