package core;

import entities.Place;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;


@RestController
public class PlacesController {

    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyCGeAZDkQz0REaiedemTMVajwLAZe_4wbM";

    @GetMapping(path = "/examplePlaces")
    public ArrayList<Place> examplePlaces() {

        //59.851475, 30.320915
        double lat = 59.851475;
        double lng = 30.320915;
        double radius = 500;
        String type = "museum";

        ArrayList<Place> resultList = null;
        HttpURLConnection conn = null;

        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?location=" + lat + "," + lng);
            sb.append("&radius=" + radius);
            sb.append("&type=" + type);
            sb.append("&key=" + API_KEY);

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            int read;
            char[] buff = new char[1024];

            while ((read = in.read(buff)) != -1)
                jsonResults.append(buff, 0, read);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (conn != null)
                conn.disconnect();
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("results");

            // Extract the Place descriptions from the results
            resultList = new ArrayList<Place>(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                Place place = new Place();
                place.setName(predsJsonArray.getJSONObject(i).getString("name"));
                place.setRating(predsJsonArray.getJSONObject(i).getString("rating"));
                place.setAdress(predsJsonArray.getJSONObject(i).getString("vicinity"));
                resultList.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return resultList;
    }
}
