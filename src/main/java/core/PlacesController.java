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
            case "water":
                place.setFilter("water");
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
        } else if (place.getFilter().equals("water")) {
            resultList = placesRepository.findAllByFilter("water").orElse(null);
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

    @GetMapping(path = "/addWater")
    public boolean addWater() {
        String videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
        Place water = new Place();
        water.setAddress("Комарово, Санкт-Петербург");
        water.setClose("24:00");
        water.setOpen("00:00");
        water.setDescription("Один из самых старых пляжей северного побережья. Есть две ландшафтные зоны: песчаная и каменистая. Купаться здесь «Роспотребнадзор» не разрешает: воду в Финском заливе нельзя назвать чистой, к тому же где-то с июля она начинает цвести. Любые развлечения — волейбол, фрисби и прочее — придётся организовывать себе самостоятельно. Пляжная линия тянется вдоль трассы, поэтому сюда часто заглядывают проезжающие мимо дальнобойщики и туристы. Некоторые жители города остаются здесь на все выходные — разбивают палатки, жарят шашлыки, играют на свежем воздухе.\n" +
             "ИНФРАСТРУКТУРА: спасательная станция, парковка, кабинки для переодевания, туалеты, детская площадка, скамейки, урны.\n" +
             "СТОИМОСТЬ ВХОДА: бесплатно");
        water.setFilter("water");
        water.setLat(60.177633);
        water.setLng(29.786257);
        water.setName("Пляж в посёлке Комарово");
        water.setRating(4.7);
        water.setVideoUrl(videoUrl);
        placesRepository.save(water);
        
        Place water1 = new Place();
        water1.setAddress("Комарово, Санкт-Петербург");
        water1.setClose("22:00");
        water1.setOpen("07:00");
        water1.setDescription("Ещё один пляж в посёлке Комарово. Посетителей честно предупреждают, что купаться здесь нельзя: вода слишком грязная. Впрочем, в отличие от других комаровских пляжей здесь есть инфраструктура для активного отдыха: две волейбольные площадки, поле для игры в стритбол, пляжный футбол, настольный теннис и прочее. Случаются концерты и дискотеки, но отследить их расписание «онлайн», к сожалению, довольно сложно: группа «ВКонтакте» не обновлялась с 2012 года.\n" +
            "ИНФРАСТРУКТУРА: парковка, ресторан, летнее кафе, бар, детская площадка, туалеты, душ, танцпол с навесом, площадки для волейбола и футбола, кабинки для переодевания, урны.\n" +
            "СТОИМОСТЬ ВХОДА: бесплатно");
        water1.setFilter("water");
        water1.setLat(60.175146);
        water1.setLng(29.799882);
        water1.setName("Пляж High Drive");
        water1.setRating(4.9);
        water1.setVideoUrl(videoUrl);
        placesRepository.save(water1);

        Place water2 = new Place();
        water2.setAddress("оз. Щучье, Санкт-Петербург");
        water2.setClose("24:00");
        water2.setOpen("00:00");
        water2.setDescription("До неофициального, но популярного пляжа в четырёх километрах от посёлка Комарово лучше добираться на машине или велосипеде. По отзывам, вода в озере довольно чистая — правда, щуку в наши дни здесь встретить сложно (что не мешает рыбакам ежедневно заседать здесь с удочками). В 2011 году здесь официально открыли заказник «Щучье озеро».\n" +
            "ИНФРАСТРУКТУРА: парковка, спасательная станция\n" +
            "СТОИМОСТЬ ВХОДА: бесплатно");
        water2.setFilter("water");
        water2.setLat(60.211713);
        water2.setLng(29.788458);
        water2.setName("Пляж на Щучьем озере");
        water2.setRating(4.8);
        water2.setVideoUrl(videoUrl);
        placesRepository.save(water2);

        Place water3 = new Place();
        water3.setAddress("Московский пр. д.150, к.2, Бассейн \"Волна\"");
        water3.setOpen("08:00");
        water3.setClose("22:00");
        water3.setDescription("Дайвинг, обучение дайвингу, фри-дайвингу и подводной охоте в дайвинг клубе «Батискаф» в Санкт-Петербурге.   Обучение дайвингу (англ. diving) проводится по системам «CMAS», «PADI», «IANTD»\n" +
            "Обучение дайвингу: 14 000руб. - курс обучения (7 занятий - каждое занятие 1 час теории, 1 час. практики, вода) и 2 000руб. - комплект документов." +
            "Обучение подводной охоте: 12 000руб. - курс обучения (6 занятий 1 час теории, 1 час. практики, вода)");
        water3.setFilter("water");
        water3.setLat(59.885027);
        water3.setLng(30.321706);
        water3.setName("Батискаф, спортивно-технический клуб");
        water3.setRating(4.8);
        water3.setVideoUrl(videoUrl);
        placesRepository.save(water3);

        Place water4 = new Place();
        water4.setAddress("Береговая ул., 19, лит. А");
        water4.setOpen("07:00");
        water4.setClose("20:00");
        water4.setDescription("Академия парусного спорта Яхт-клуба Санкт-Петербурга - проект, нацеленный на долгосрочное развитие парусного спорта в России.\n" +
            "Деятельность Академии направлена на подготовку молодых и совсем юных спортсменов к профессиональному парусному спорту и соревнованиям международного уровня.\n" +
            "Чемпионами не рождаются – чемпионами становятся!\n" +
            "Вырастить будущих олимпийских призеров – дело уже сегодняшнего дня.");
        water4.setFilter("water");
        water4.setLat(59.986598);
        water4.setLng(30.166495);
        water4.setName("Академия парусного спорта Яхт-клуба Санкт-Петербурга");
        water4.setRating(4.8);
        water4.setVideoUrl(videoUrl);
        placesRepository.save(water4);

        Place water5 = new Place();
        water5.setAddress("Рощинское оз., Санкт-Петербург");
        water5.setOpen("00:00");
        water5.setClose("24:00");
        water5.setDescription("Кого ловить: на озере есть 8 небольших островов. Берега сильно заросли и ловля предпочтительней с лодки, но с гористого берега можно забросить удочку и спиннинг. Озеро мало посещается рыболовами, поскольку добраться сюда можно только на машине. Подъезды к озеру есть хорошие, вплотную к воде. Но их надо поискать. Здесь водится: окунь, плотва, щука, лещ, налим, карась, линь.");
        water5.setFilter("water");
        water5.setLat(60.802268);
        water5.setLng(29.951003);
        water5.setName("Рощинское озеро");
        water5.setRating(4.8);
        water5.setVideoUrl(videoUrl);
        placesRepository.save(water5);

        Place water6 = new Place();
        water6.setAddress("Приморский пр., 72");
        water6.setOpen("10:00");
        water6.setClose("22:30");
        water6.setDescription("Главное, что нужно знать об этом месте — это то, что аквапарк поистине огромен. Он включает громадное количество народу — до двух тысяч посетителей в сутки. Одних бань 12 видов. Много элементов, все они — сложные и требующие ухода. К нему ходит бесплатные автобусы от метро «Черная речка» и «Старая деревня».\n" +
            "Вы когда-нибудь видели космический корабль? Он настолько сложен, что каждая деталь по отдельности проходит проверку сотни раз, и все равно постоянно что-то где-то ломается. И так происходит со всеми сложными системами. Аквапарк «Питерленд» — не исключение. То одно, то другое сдает сбой. В центре сооружен огромный пиратский корабль. Из него выходят спуски. А по одной из водных горок даже можно подниматься вверх!\n" +
            "АКЦИЯ ДЛЯ ЛЮДЕЙ С ОГРАНИЧЕННЫМИ ВОЗМОЖНОСТЯМИ «РАДОСТЬ КАЖДОМУ» - 350 рублей.\n" +
            "Общая цена от 890 до 3000 р.");
        water6.setFilter("water");
        water6.setLat(59.981076);
        water6.setLng(30.210079);
        water6.setName("Питерлэнд");
        water6.setRating(4.4);
        water6.setVideoUrl(videoUrl);
        placesRepository.save(water6);

        Place water7 = new Place();
        water7.setAddress("улица Фучика, 10 к.2");
        water7.setOpen("18:00");
        water7.setClose("23:00");
        water7.setDescription("Миссия дайв-центра Global Diving — предоставить Вам комплекс услуг высшего класса в области дайвинга.\n" +
            "На нашей базе, одной из лучших в Санкт-Петербурге, Вы можете пройти обучение подводному плаванию по множеству различных программ международной системы SSI, с нуля и, постепенно, до уровня продвинутого дайвера-исследователя, настоящего покорителя глубин и пещер.\n" +
            "Обучение возможно как в малых группах, так и индивидуально, а также по программам VIP. Теоретическую часть Вы можете начать изучать БЕСПЛАТНО прямо сейчас!\n" +
            "Наши инструкторы — настоящие профессионалы в своём деле — помогут Вам подобрать и приобрести лучшее подводное снаряжение, подходящее именно Вам.\n" +
            "С нами Вы сможете погрузиться в самых экзотических уголках Земного шара, от Северного до Южного полюса, от Атлантического до Тихого океана.\n" +
            "Open Water Diver — групповое обучение (по расписанию дайвцентра) - 15 500 р.");
        water7.setFilter("water");
        water7.setLat(59.885319);
        water7.setLng(30.378205);
        water7.setName("Global Diving - обучение дайвингу, Клуб дайверов");
        water7.setRating(5.0);
        water7.setVideoUrl(videoUrl);
        placesRepository.save(water7);

        return true;
    }

    //temporary
    // @GetMapping(path = "/addEco")
    // public boolean addEco() {
    //     String videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
    //     Place eco = new Place();
    //     eco.setAddress("г. Пушкин, Фермский Императорский парк");
    //     eco.setClose("19:00");
    //     eco.setOpen("10:00");
    //     eco.setDescription("Цель движения Мусора.Больше.Нет чтобы не было мусора! Чтобы на газонах и во дворах, в парках, лесах, на полях и полянках, на пляжах, на тротуарах, на детских площадках, на спортивных полях, дорогах и пустырях НЕ БЫЛО МУСОРА! Мусор может быть в урнах, мусорных баках, мусорных машинах и на станциях переработки мусора!");
    //     eco.setFilter("eco");
    //     eco.setLat(59.729262);
    //     eco.setLng(30.381110);
    //     eco.setName("Мусора. Больше. Нет.");
    //     eco.setRating(4.7);
    //     eco.setVideoUrl(videoUrl);
    //     placesRepository.save(eco);

    //     Place eco1 = new Place();
    //     eco1.setAddress("ул. Рылеева, 17-19 лит.А");
    //     eco1.setClose("18:30");
    //     eco1.setOpen("10:00");
    //     eco1.setDescription("Greenpeace - это общественная неправительственная и некоммерческая экологическая организация. Она объединяет людей, живущих на разных континентах, с различным цветом кожи, говорящих на множестве языков озабоченных проблемой защиты окружающей среды.");
    //     eco1.setFilter("eco");
    //     eco1.setLat(59.942325);
    //     eco1.setLng(30.356412);
    //     eco1.setName("Гринпис. Санкт-Петербург.");
    //     eco1.setRating(4.3);
    //     eco1.setVideoUrl(videoUrl);
    //     placesRepository.save(eco1);

    //     Place eco2 = new Place();
    //     eco2.setAddress("3-я лин. В. О., 2");
    //     eco2.setClose("18:00");
    //     eco2.setOpen("10:00");
    //     eco2.setDescription("Зеленый Крест является инициатором и организатором ежегодного конкурса для экологических журналистов «Экостиль». Одно из направлений нашей деятельности — проведение пресс-туров по наиболее интересным местам Ленинградской области, памятникам истории и культуры. Зеленый Крест принимает участие в деятельности Общественной Палаты Ленинградской области, Общественного экологического Совета при Губернаторе Ленинградской области, Общественного Совета по охране окружающей среды при Правительстве Санкт–Петербурга");
    //     eco2.setFilter("eco");
    //     eco2.setLat(59.939492);
    //     eco2.setLng(30.288688);
    //     eco2.setName("Зелёный крест");
    //     eco2.setRating(4.5);
    //     eco2.setVideoUrl(videoUrl);
    //     placesRepository.save(eco2);

    //     return true;
    // }

    // @GetMapping(path = "/addVol")
    // public boolean addVol() {
    //     String videoUrl = "https://res.cloudinary.com/deyh0dll3/video/upload/v1573342642/ChIJ52xnOQAwlkYRv4Z1NFTueqM_qkqruw.mp4";
    //     Place eco = new Place();
    //     eco.setAddress("Санкт-Петербург, Приморский пр., 33");
    //     eco.setClose("19:00");
    //     eco.setOpen("10:00");
    //     eco.setDescription("ЭвриЧайлд - это международная благотворительная организация, которая оказывает помощь социально незащищенным детям в 15 странах мира. В России работает с 1994 года. Цель - снижение количества детей, попадающих в сиротские учреждения, и повышение числа детей, размещаемых из этих учреждений в семью.");
    //     eco.setFilter("vol");
    //     eco.setLat(59.983903);
    //     eco.setLng(30.284728);
    //     eco.setName("ЭвриЧайлд");
    //     eco.setRating(5.0);
    //     eco.setVideoUrl(videoUrl);
    //     placesRepository.save(eco);

    //     Place eco1 = new Place();
    //     eco1.setAddress("Миллионная ул., 11");
    //     eco1.setClose("18:00");
    //     eco1.setOpen("10:00");
    //     eco1.setDescription("Кратко о деятельности-помощь нуждающимся в независимости от расовых этнических различий. Донорство, профилактика ВИЧ инфекции, обучение первой помощи, помощь детям.");
    //     eco1.setFilter("vol");
    //     eco1.setLat(59.944122);
    //     eco1.setLng(30.324616);
    //     eco1.setName("Российский Красный Крест");
    //     eco1.setRating(4.2);
    //     eco1.setVideoUrl(videoUrl);
    //     placesRepository.save(eco1);

    //     Place eco2 = new Place();
    //     eco2.setAddress("ул. Оружейника Фёдорова, 2");
    //     eco2.setClose("20:00");
    //     eco2.setOpen("12:00");
    //     eco2.setDescription("Теплый дом-Благотворительный фонд помощи детям «Тёплый дом» работает с семьями группы риска. Как правило, у таких семей целый комплекс проблем – социальных, психологических, материальных. В неблагополучных семьях дети не всегда получают должное внимание от родителей, пропускают занятия в школе, подвергаются риску насилия, плохо питаются. Условия проживания детей в этих семьях в ряде случаев является неудовлетворительным – и существует риск изъятия детей из семьи.");
    //     eco2.setFilter("vol");
    //     eco2.setLat(59.945830);
    //     eco2.setLng(30.339726);
    //     eco2.setName("Тёплый дом");
    //     eco2.setRating(4.5);
    //     eco2.setVideoUrl(videoUrl);
    //     placesRepository.save(eco2);
        
    //     Place eco3 = new Place();
    //     eco3.setAddress("13-я лин. В.О, 22 В");
    //     eco3.setClose("18:00");
    //     eco3.setOpen("10:00");
    //     eco3.setDescription("Центр городских волонтеров Санкт-Петербурга – это структура Комитета по молодежной политике и взаимодействию с общественными организациями и ГБУ «Дом Молодежи Санкт-Петербурга», целью которой является создание и поддержание эффективной системы просвещения, отбора и подготовки (обучения) волонтеров для качественного проведения мероприятий любого уровня. Открыт как программа наследия Центра подготовки городских волонтеров Кубка Конфедераций FIFA 2017 и Чемпионата мира по футболу FIFA 2018™ города-организатора Санкт-Петербург согласно протоколу заседания организационного комитета по проведению в Санкт-Петербурге Года добровольца (волонтера).");
    //     eco3.setFilter("vol");
    //     eco3.setLat(59.938613);
    //     eco3.setLng(30.272274);
    //     eco3.setName("Центр городских волонтеров Санкт-Петербурга");
    //     eco3.setRating(4.5);
    //     eco3.setVideoUrl(videoUrl);
    //     placesRepository.save(eco3);

    //     return true;
    // }
}
