package dev.pokedex.weather;
public record WeatherDto(Current current) {
    public record Current(Integer weather_code, Double wind_speed_10m, Double precipitation) {}
}
