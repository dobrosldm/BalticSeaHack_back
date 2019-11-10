package core;

import entities.Place;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Optional;
import java.net.URISyntaxException;
import java.util.Random;

@RestController
public class PlacesController {

    @Autowired
    private PlacesRepository placesRepository;

    private final String API_KEY = "AIzaSyAXtd2wjXVe86zgXu176Z_-TwJ7FPexEpc";

    private final HttpClient client = new DefaultHttpClient();

    @PostMapping(path = "/places")
    public ArrayList<Place> getPlaces(@RequestBody Place place) throws ParseException, IOException, URISyntaxException {

        String videoUrl;

        switch (place.getFilter()) {
            case "culture":
                place.setFilter("museum");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "food":
                place.setFilter("restaurant");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "learning":
                place.setFilter("university");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "parties":
                place.setFilter("night_club");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "nature":
                place.setFilter("park");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "zoo":
                place.setFilter("zoo");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "eco":
                place.setFilter("eco");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            case "vol":
                place.setFilter("vol");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
            default:
                place.setFilter("restaurant");
                videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
                break;
        }

        ArrayList<Place> resultList;

        if (place.getFilter().equals("vol")) {
            resultList = placesRepository.findAllByFilter("vol").orElse(null);
        } else if (place.getFilter().equals("eco")) {
            resultList = placesRepository.findAllByFilter("eco").orElse(null);
        } else {
            final URIBuilder builder = new URIBuilder().setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/place/search/json");

            builder.addParameter("location",  place.getLat() + "," + place.getLng());
            builder.addParameter("radius", String.valueOf(place.getRad()));
            builder.addParameter("types", place.getFilter());
            builder.addParameter("key", API_KEY);

            HttpUriRequest request = new HttpGet(builder.build());
            HttpResponse httpResponse = this.client.execute(request);
            String response = EntityUtils.toString(httpResponse.getEntity());

            JSONArray jsonArray = new JSONObject(response).getJSONArray("results");

            Random r = new Random();
            DecimalFormat df = new DecimalFormat("#.#");
            resultList = new ArrayList<Place>(jsonArray.length());

            for (int i = 0; i < jsonArray.length(); i++) {
                Optional<Place> optional = placesRepository.findById(jsonArray.getJSONObject(i).getString("place_id"));
                Place tmpPlace;
                if (optional.isPresent()) {
                    tmpPlace = optional.get();
                } else {
                    tmpPlace = new Place();
                    tmpPlace.setName(jsonArray.getJSONObject(i).getString("name"));
                    //tmpPlace.setLat(place.getLat());
                    tmpPlace.setLat(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
                    //tmpPlace.setLng(place.getLng());
                    tmpPlace.setLng(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
                    tmpPlace.setId(jsonArray.getJSONObject(i).getString("place_id"));
                    tmpPlace.setRating(Double.parseDouble(df.format(3D + (5D - 3D) * r.nextDouble())));
                    tmpPlace.setAddress(jsonArray.getJSONObject(i).getString("vicinity"));
                    tmpPlace.setClose((r.nextInt((21 - 17) + 1) + 17) +":00");
                    tmpPlace.setOpen((r.nextInt((11 - 7) + 1) + 7) +":00");
                    tmpPlace.setDescription("Some description here");
                    tmpPlace.setVideoUrl(videoUrl);
                    tmpPlace.setFilter(place.getFilter());
    
                    placesRepository.save(tmpPlace);
                }
                resultList.add(tmpPlace);
            }
        }

        return resultList;
    }

    @PostMapping(path = "/add")
    public Place add(@RequestBody Place place) {
        return placesRepository.save(place);
    }
}
