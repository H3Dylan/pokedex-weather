package dev.pokedex.app;

import dev.pokedex.domain.Type;
import dev.pokedex.weather.GeocodingCacheService;
import dev.pokedex.weather.WeatherCacheService;
import dev.pokedex.weather.WeatherKind;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EffectServiceTest {

    @Test
    void evaluate_clear_weather_fire_is_resistant() {
        // mocks
        WeatherCacheService weather = mock(WeatherCacheService.class);
        GeocodingCacheService geocoding = mock(GeocodingCacheService.class);
        when(weather.getWeatherKind(48.8566, 2.3522)).thenReturn(WeatherKind.CLEAR);

        var svc = new EffectService(weather, geocoding);
        var res = svc.evaluate("Charmander", Type.FIRE, 48.8566, 2.3522);

        assertEquals("Charmander", res.pokemonName());
        assertEquals(Type.FIRE, res.type());
        assertEquals(WeatherKind.CLEAR, res.weather());
        assertEquals(EffectService.Effect.RESISTANT, res.effect());
        verify(weather).getWeatherKind(48.8566, 2.3522);
        verifyNoInteractions(geocoding);
    }

    @Test
    void evaluate_rain_fire_is_faible_water_is_resistant() {
        WeatherCacheService weather = mock(WeatherCacheService.class);
        GeocodingCacheService geocoding = mock(GeocodingCacheService.class);
        when(weather.getWeatherKind(0.0, 0.0)).thenReturn(WeatherKind.RAIN);

        var svc = new EffectService(weather, geocoding);

        var fire = svc.evaluate("Charmander", Type.FIRE, 0.0, 0.0);
        assertEquals(EffectService.Effect.FAIBLE, fire.effect());

        var water = svc.evaluate("Squirtle", Type.WATER, 0.0, 0.0);
        assertEquals(EffectService.Effect.RESISTANT, water.effect());
    }

    @Test
    void evaluateByCity_uses_geocoding_then_weather() {
        WeatherCacheService weather = mock(WeatherCacheService.class);
        GeocodingCacheService geocoding = mock(GeocodingCacheService.class);
        // on a pris la version qui retourne Coords (nullable), pas Optional, côté service cache
        var coords = new dev.pokedex.weather.GeocodingClient.Coords(48.8566, 2.3522);
        when(geocoding.locate("Paris", "FR")).thenReturn(coords);
        when(weather.getWeatherKind(48.8566, 2.3522)).thenReturn(WeatherKind.WINDY);

        var svc = new EffectService(weather, geocoding);
        var res = svc.evaluateByCity("Pikachu", Type.ELECTRIC, "Paris", "FR");

        assertEquals(WeatherKind.WINDY, res.weather());
        assertEquals(EffectService.Effect.NEUTRE, res.effect()); // ELECTRIC n’a pas de bonus en WINDY
        verify(geocoding).locate("Paris", "FR");
        verify(weather).getWeatherKind(48.8566, 2.3522);
    }

    @Test
    void evaluateByCity_when_not_found_returns_unknown_neutre() {
        WeatherCacheService weather = mock(WeatherCacheService.class);
        GeocodingCacheService geocoding = mock(GeocodingCacheService.class);
        when(geocoding.locate("Nowhere", null)).thenReturn(null);

        var svc = new EffectService(weather, geocoding);
        var res = svc.evaluateByCity("Pikachu", Type.ELECTRIC, "Nowhere", null);

        assertEquals(WeatherKind.UNKNOWN, res.weather());
        assertEquals(EffectService.Effect.NEUTRE, res.effect());
        verifyNoInteractions(weather);
    }
}
