package core;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import se.walkercrou.places.GooglePlaces;
import se.walkercrou.places.Place;

import java.util.List;

@RestController
public class PlacesController {

    private static final String GOOGLE_API_KEY  = "AIzaSyCGeAZDkQz0REaiedemTMVajwLAZe_4wbM";

    GooglePlaces client = new GooglePlaces(GOOGLE_API_KEY);

    @GetMapping(path = "/examplePlaces", produces = "application/json")
    public List<Place> examplePlaces() {
        //59.851475, 30.320915
        double lat = 59.851475;
        double lng = 30.320915;
        double radius = 500;

        List<Place> places = client.getNearbyPlaces(lat, lng, radius, GooglePlaces.DEFAULT_RESULTS);

        return places;
    }
}
