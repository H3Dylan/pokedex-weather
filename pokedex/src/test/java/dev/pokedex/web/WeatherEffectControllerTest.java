package dev.pokedex.web;

import dev.pokedex.app.EffectService;
import dev.pokedex.app.EffectService.Effect;
import dev.pokedex.app.EffectService.EffectView;
import dev.pokedex.domain.Pokemon;
import dev.pokedex.domain.Type;
import dev.pokedex.repo.PokemonRepository;
import dev.pokedex.weather.WeatherKind;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(WeatherEffectController.class)
class WeatherEffectControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PokemonRepository repo;

    @MockBean
    EffectService effects;

    @Test
    void preview_effect_by_coords() throws Exception {
        var p = new Pokemon("Pikachu", Type.ELECTRIC, 55, 40);
        when(repo.findById(1L)).thenReturn(Optional.of(p));
        when(effects.evaluate(eq("Pikachu"), eq(Type.ELECTRIC), eq(48.8566), eq(2.3522)))
                .thenReturn(new EffectView("Pikachu", Type.ELECTRIC, WeatherKind.CLEAR, Effect.NEUTRE));

        mvc.perform(get("/api/weather/effect")
                        .param("pokemonId", "1")
                        .param("lat", "48.8566")
                        .param("lon", "2.3522"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pokemonName").value("Pikachu"))
                .andExpect(jsonPath("$.weather").value("CLEAR"))
                .andExpect(jsonPath("$.effect").value("NEUTRE"));
    }

    @Test
    void preview_effect_404_when_pokemon_missing() throws Exception {
        when(repo.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/weather/effect")
                        .param("pokemonId", "999")
                        .param("lat", "0")
                        .param("lon", "0"))
                .andExpect(status().isNotFound());
    }
}
