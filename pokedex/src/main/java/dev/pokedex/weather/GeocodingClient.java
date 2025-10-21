package dev.pokedex.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Optional;

@Component
public class GeocodingClient {
    private final RestClient http = RestClient.create();

    public Optional<Coords> locate(String city, String countryOrNull) {
        String q = city.trim();
        String url = "https://geocoding-api.open-meteo.com/v1/search"
                + "?name=" + encode(q)
                + "&count=1&language=fr&format=json";
        if (countryOrNull != null && !countryOrNull.isBlank()) {
            url += "&country=" + encode(countryOrNull.trim());
        }

        try {
            GeoDto dto = http.get().uri(url).retrieve().body(GeoDto.class);
            if (dto == null || dto.results() == null || dto.results().isEmpty()) return Optional.empty();
            var r = dto.results().get(0);
            if (r.latitude() == null || r.longitude() == null) return Optional.empty();
            return Optional.of(new Coords(r.latitude(), r.longitude()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    private String encode(String s) {
        return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    }

    public record Coords(double lat, double lon) implements java.io.Serializable {}

}
