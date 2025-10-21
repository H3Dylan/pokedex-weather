package dev.pokedex.web;

import dev.pokedex.app.EffectService;
import dev.pokedex.repo.PokemonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/weather")
public class WeatherEffectController {

    private final PokemonRepository repo;
    private final EffectService effects;

    public WeatherEffectController(PokemonRepository repo, EffectService effects) {
        this.repo = repo; this.effects = effects;
    }

    // Exemple : /api/weather/effect?pokemonId=2&lat=48.8566&lon=2.3522
    @GetMapping("/effect")
    public ResponseEntity<?> effect(@RequestParam("pokemonId") Long pokemonId,
                                    @RequestParam("lat") double lat,
                                    @RequestParam("lon") double lon) {
        return repo.findById(pokemonId)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        effects.evaluate(p.getName(), p.getType(), lat, lon)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // /api/weather/effect/by-name?name=Pikachu&lat=48.8566&lon=2.3522
    @GetMapping("/effect/by-name")
    public ResponseEntity<?> effectByName(@RequestParam("name") String name,
                                          @RequestParam("lat") double lat,
                                          @RequestParam("lon") double lon) {
        return repo.findByNameIgnoreCase(name)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        effects.evaluate(p.getName(), p.getType(), lat, lon)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // /api/weather/effect/by-city?pokemonId=2&city=Paris&country=FR
    @GetMapping("/effect/by-city")
    public ResponseEntity<?> effectByCity(@RequestParam("pokemonId") Long pokemonId,
                                          @RequestParam("city") String city,
                                          @RequestParam(value = "country", required = false) String country) {
        return repo.findById(pokemonId)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        effects.evaluateByCity(p.getName(), p.getType(), city, country)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

    // /api/weather/effect/by-name-city?name=Pikachu&city=Lyon&country=FR
    @GetMapping("/effect/by-name-city")
    public ResponseEntity<?> effectByNameCity(@RequestParam("name") String name,
                                              @RequestParam("city") String city,
                                              @RequestParam(value = "country", required = false) String country) {
        return repo.findByNameIgnoreCase(name)
                .<ResponseEntity<?>>map(p -> ResponseEntity.ok(
                        effects.evaluateByCity(p.getName(), p.getType(), city, country)
                ))
                .orElse(ResponseEntity.notFound().build());
    }

}
