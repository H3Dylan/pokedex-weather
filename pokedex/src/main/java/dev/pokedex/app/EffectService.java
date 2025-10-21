package dev.pokedex.app;

import dev.pokedex.domain.Type;
import dev.pokedex.weather.WeatherCacheService;
import dev.pokedex.weather.WeatherKind;
import org.springframework.stereotype.Service;

@Service
public class EffectService {

    public enum Effect { FAIBLE, RESISTANT, NEUTRE }

    private final WeatherCacheService weather;
    private final dev.pokedex.weather.GeocodingCacheService geocoding;

    public EffectService(WeatherCacheService weather, dev.pokedex.weather.GeocodingCacheService geocoding) {
        this.weather = weather; this.geocoding = geocoding;
    }

    public record EffectView(String pokemonName, Type type, WeatherKind weather, Effect effect) {}

    public EffectView evaluate(String pokemonName, Type type, double lat, double lon) {
        WeatherKind w = weather.getWeatherKind(lat, lon);
        Effect e = switch (w) {
            case RAIN -> (type == Type.WATER) ? Effect.RESISTANT :
                    (type == Type.FIRE)  ? Effect.FAIBLE    : Effect.NEUTRE;
            case CLEAR -> (type == Type.FIRE)  ? Effect.RESISTANT : Effect.NEUTRE;
            case SNOW  -> (type == Type.ICE)   ? Effect.RESISTANT :
                    (type == Type.GROUND)? Effect.FAIBLE    : Effect.NEUTRE;
            case WINDY -> (type == Type.FLYING)? Effect.RESISTANT : Effect.NEUTRE;
            case THUNDERSTORM -> (type == Type.ELECTRIC)? Effect.RESISTANT :
                    (type == Type.WATER)   ? Effect.FAIBLE    : Effect.NEUTRE;
            default -> Effect.NEUTRE;
        };
        return new EffectView(pokemonName, type, w, e);
    }

    public EffectView evaluateByCity(String pokemonName, Type type, String city, String country) {
        var coords = geocoding.locate(city, country); // peut être null
        if (coords == null) {
            return new EffectView(pokemonName, type, WeatherKind.UNKNOWN, Effect.NEUTRE);
        }
        return evaluate(pokemonName, type, coords.lat(), coords.lon());
    }


}
