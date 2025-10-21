package dev.pokedex.weather;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GeocodingCacheService {
    private final GeocodingClient client;
    public GeocodingCacheService(GeocodingClient client) { this.client = client; }

    @Cacheable(
            cacheNames = "geo",
            key = "((#p0 == null ? '' : #p0).toLowerCase().trim()) + ':' + ((#p1 == null ? '' : #p1).toLowerCase().trim())",
            unless = "#result == null" // on évite de cacher les échecs
    )
    public GeocodingClient.Coords locate(String city, String country) {
        return client.locate(city, country).orElse(null);
    }
}

