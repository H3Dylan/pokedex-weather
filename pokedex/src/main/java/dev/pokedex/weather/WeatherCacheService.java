package dev.pokedex.weather;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class WeatherCacheService {
    private final WeatherClient client;
    private static final Logger log = LoggerFactory.getLogger(WeatherCacheService.class);
    public WeatherCacheService(WeatherClient client) { this.client = client; }

    // On arrondit à 0.1° pour éviter d'exploser les clés
    @Cacheable(cacheNames = "weatherKind",
            key = "#root.methodName + ':' + T(java.math.BigDecimal).valueOf(#p0).setScale(1, T(java.math.RoundingMode).HALF_UP).toPlainString() + ':' + T(java.math.BigDecimal).valueOf(#p1).setScale(1, T(java.math.RoundingMode).HALF_UP).toPlainString()",
            unless = "#result == T(dev.pokedex.weather.WeatherKind).UNKNOWN")
    public WeatherKind getWeatherKind(double lat, double lon) {
        log.info("🌦️ Appel API météo pour lat={}, lon={}", lat, lon);
        return client.fetch(lat, lon);
    }
}
