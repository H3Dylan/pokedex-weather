package dev.pokedex.weather;

import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class WeatherClient {
    private final RestClient http = RestClient.create();

    public WeatherKind fetch(double lat, double lon) {
        String url = "https://api.open-meteo.com/v1/forecast"
                + "?latitude=" + lat
                + "&longitude=" + lon
                + "&current=weather_code,precipitation,wind_speed_10m";
        try {
            WeatherDto dto = http.get().uri(url).retrieve().body(WeatherDto.class);
            if (dto == null || dto.current() == null) return WeatherKind.UNKNOWN;

            Integer codeObj = dto.current().weather_code();
            double wind = dto.current().wind_speed_10m() == null ? 0.0 : dto.current().wind_speed_10m();
            int code = codeObj == null ? -1 : codeObj;

            if (code >= 95) return WeatherKind.THUNDERSTORM;
            if (code >= 80) return WeatherKind.RAIN;
            if (code >= 70) return WeatherKind.SNOW;
            if (wind >= 8.0) return WeatherKind.WINDY;  // ~28 km/h
            if (code <= 1)  return WeatherKind.CLEAR;
            if (code <= 3)  return WeatherKind.CLOUDY;
            return WeatherKind.UNKNOWN;
        } catch (Exception e) {
            return WeatherKind.UNKNOWN;
        }
    }
}
