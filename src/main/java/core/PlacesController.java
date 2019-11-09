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
import org.springframework.web.bind.annotation.*;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.validation.constraints.Null;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.net.URISyntaxException;
import java.util.Random;


@RestController
public class PlacesController {

    private final String API_KEY = "AIzaSyAXtd2wjXVe86zgXu176Z_-TwJ7FPexEpc";

    private final HttpClient client = new DefaultHttpClient();

    @PostMapping(path = "/places")
    public ArrayList<Place> getPlaces(@RequestBody Place place) throws ParseException, IOException, URISyntaxException {

        final URIBuilder builder = new URIBuilder().setScheme("https").setHost("maps.googleapis.com").setPath("/maps/api/place/search/json");

        if (place.getType().isEmpty())
            place.setType("museum");

        if (place.getRad().isNaN())
            place.setRad(3000D);

        builder.addParameter("location",  place.getLat() + "," + place.getLng());
        builder.addParameter("radius", String.valueOf(place.getRad()));
        builder.addParameter("type", place.getType());
        builder.addParameter("key", API_KEY);

        HttpUriRequest request = new HttpGet(builder.build());
        HttpResponse httpResponse = this.client.execute(request);
        String response = EntityUtils.toString(httpResponse.getEntity());

        ArrayList<Place> resultList;
        JSONArray jsonArray = new JSONObject(response).getJSONArray("results");

        Random r = new Random();
        DecimalFormat df = new DecimalFormat("#.#");
        resultList = new ArrayList<Place>(jsonArray.length());

        for (int i = 0; i < jsonArray.length(); i++) {
            Place tmpPlace = new Place();
            tmpPlace.setId(jsonArray.getJSONObject(i).getString("place_id"));
            tmpPlace.setName(jsonArray.getJSONObject(i).getString("name"));
            tmpPlace.setRating(Double.parseDouble(df.format(3D + (5D - 3D) * r.nextDouble())));
            tmpPlace.setAddress(jsonArray.getJSONObject(i).getString("vicinity"));
            tmpPlace.setClose((r.nextInt((21 - 17) + 1) + 17) +":00");
            tmpPlace.setOpen((r.nextInt((11 - 7) + 1) + 7) +":00");
            resultList.add(tmpPlace);
        }

        return resultList;
    }
}
