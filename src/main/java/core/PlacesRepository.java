package core;

import entities.Place;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PlacesRepository extends MongoRepository<Place, String> {
    Optional<ArrayList<Place>> findAllByFilter(String filter);
}