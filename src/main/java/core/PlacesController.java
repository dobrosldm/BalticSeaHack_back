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
                    tmpPlace.setLat(jsonArray.getJSONObject(i).getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
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

    //temporary
    @GetMapping(path = "/addEco")
    public boolean addEco() {
        String videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
        Place eco = new Place();
        eco.setAddress("г. Пушкин, Фермский Императорский парк");
        eco.setClose("19:00");
        eco.setOpen("10:00");
        eco.setDescription("Цель движения Мусора.Больше.Нет чтобы не было мусора! Чтобы на газонах и во дворах, в парках, лесах, на полях и полянках, на пляжах, на тротуарах, на детских площадках, на спортивных полях, дорогах и пустырях НЕ БЫЛО МУСОРА! Мусор может быть в урнах, мусорных баках, мусорных машинах и на станциях переработки мусора!");
        eco.setFilter("eco");
        eco.setLat(59.729262);
        eco.setLng(30.381110);
        eco.setName("Мусора. Больше. Нет.");
        eco.setRating(4.7);
        eco.setVideoUrl(videoUrl);
        placesRepository.save(eco);

        Place eco1 = new Place();
        eco1.setAddress("ул. Рылеева, 17-19 лит.А");
        eco1.setClose("18:30");
        eco1.setOpen("10:00");
        eco1.setDescription("Greenpeace - это общественная неправительственная и некоммерческая экологическая организация. Она объединяет людей, живущих на разных континентах, с различным цветом кожи, говорящих на множестве языков озабоченных проблемой защиты окружающей среды.");
        eco1.setFilter("eco");
        eco1.setLat(59.942325);
        eco1.setLng(30.356412);
        eco1.setName("Гринпис. Санкт-Петербург.");
        eco1.setRating(4.3);
        eco1.setVideoUrl(videoUrl);
        placesRepository.save(eco1);

        Place eco2 = new Place();
        eco2.setAddress("3-я лин. В. О., 2");
        eco2.setClose("18:00");
        eco2.setOpen("10:00");
        eco2.setDescription("Зеленый Крест является инициатором и организатором ежегодного конкурса для экологических журналистов «Экостиль». Одно из направлений нашей деятельности — проведение пресс-туров по наиболее интересным местам Ленинградской области, памятникам истории и культуры. Зеленый Крест принимает участие в деятельности Общественной Палаты Ленинградской области, Общественного экологического Совета при Губернаторе Ленинградской области, Общественного Совета по охране окружающей среды при Правительстве Санкт–Петербурга");
        eco2.setFilter("eco");
        eco2.setLat(59.939492);
        eco2.setLng(30.288688);
        eco2.setName("Зелёный крест");
        eco2.setRating(4.5);
        eco2.setVideoUrl(videoUrl);
        placesRepository.save(eco2);

        return true;
    }

    @GetMapping(path = "/addVol")
    public boolean addVol() {
        String videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
        Place eco = new Place();
        eco.setAddress("Санкт-Петербург, Приморский пр., 33");
        eco.setClose("19:00");
        eco.setOpen("10:00");
        eco.setDescription("ЭвриЧайлд - это международная благотворительная организация, которая оказывает помощь социально незащищенным детям в 15 странах мира. В России работает с 1994 года. Цель - снижение количества детей, попадающих в сиротские учреждения, и повышение числа детей, размещаемых из этих учреждений в семью.");
        eco.setFilter("vol");
        eco.setLat(59.983903);
        eco.setLng(30.284728);
        eco.setName("ЭвриЧайлд");
        eco.setRating(5.0);
        eco.setVideoUrl(videoUrl);
        placesRepository.save(eco);

        Place eco1 = new Place();
        eco1.setAddress("Миллионная ул., 11");
        eco1.setClose("18:00");
        eco1.setOpen("10:00");
        eco1.setDescription("Кратко о деятельности-помощь нуждающимся в независимости от расовых этнических различий. Донорство, профилактика ВИЧ инфекции, обучение первой помощи, помощь детям.");
        eco1.setFilter("vol");
        eco1.setLat(59.944122);
        eco1.setLng(30.324616);
        eco1.setName("Российский Красный Крест");
        eco1.setRating(4.2);
        eco1.setVideoUrl(videoUrl);
        placesRepository.save(eco1);

        Place eco2 = new Place();
        eco2.setAddress("ул. Оружейника Фёдорова, 2");
        eco2.setClose("20:00");
        eco2.setOpen("12:00");
        eco2.setDescription("Теплый дом-Благотворительный фонд помощи детям «Тёплый дом» работает с семьями группы риска. Как правило, у таких семей целый комплекс проблем – социальных, психологических, материальных. В неблагополучных семьях дети не всегда получают должное внимание от родителей, пропускают занятия в школе, подвергаются риску насилия, плохо питаются. Условия проживания детей в этих семьях в ряде случаев является неудовлетворительным – и существует риск изъятия детей из семьи.");
        eco2.setFilter("vol");
        eco2.setLat(59.945830);
        eco2.setLng(30.339726);
        eco2.setName("Тёплый дом");
        eco2.setRating(4.5);
        eco2.setVideoUrl(videoUrl);
        placesRepository.save(eco2);
        
        Place eco3 = new Place();
        eco3.setAddress("13-я лин. В.О, 22 В");
        eco3.setClose("18:00");
        eco3.setOpen("10:00");
        eco3.setDescription("Центр городских волонтеров Санкт-Петербурга – это структура Комитета по молодежной политике и взаимодействию с общественными организациями и ГБУ «Дом Молодежи Санкт-Петербурга», целью которой является создание и поддержание эффективной системы просвещения, отбора и подготовки (обучения) волонтеров для качественного проведения мероприятий любого уровня. Открыт как программа наследия Центра подготовки городских волонтеров Кубка Конфедераций FIFA 2017 и Чемпионата мира по футболу FIFA 2018™ города-организатора Санкт-Петербург согласно протоколу заседания организационного комитета по проведению в Санкт-Петербурге Года добровольца (волонтера).");
        eco3.setFilter("vol");
        eco3.setLat(59.938613);
        eco3.setLng(30.272274);
        eco3.setName("Центр городских волонтеров Санкт-Петербурга");
        eco3.setRating(4.5);
        eco3.setVideoUrl(videoUrl);
        placesRepository.save(eco3);

        return true;
    }
}
