package dev.pokedex.weather;

import java.util.List;

public record GeoDto(List<Result> results) {
    public record Result(String name, String country, String country_code,
                         Double latitude, Double longitude) {}
}
