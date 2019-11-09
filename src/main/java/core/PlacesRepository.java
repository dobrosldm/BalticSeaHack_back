package core;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import entities.Place;

// @Repository
public interface PlacesRepository extends MongoRepository<Place, String> {
    
}