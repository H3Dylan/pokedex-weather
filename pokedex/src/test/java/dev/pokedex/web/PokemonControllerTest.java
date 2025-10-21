package dev.pokedex.web;

import dev.pokedex.domain.Pokemon;
import dev.pokedex.domain.Type;
import dev.pokedex.repo.PokemonRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PokemonController.class)
class PokemonControllerTest {

    @Autowired
    MockMvc mvc;

    @MockBean
    PokemonRepository repo;

    @Test
    void list_returns_all_pokemons() throws Exception {
        var list = List.of(
                new Pokemon("Charmander", Type.FIRE, 52, 43),
                new Pokemon("Squirtle", Type.WATER, 48, 65)
        );
        // inject ids (via réflexion simple si besoin) ou mock plus simple : renvoyer la liste telle quelle
        Mockito.when(repo.findAll()).thenReturn(list);

        mvc.perform(get("/api/pokemons"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].name").value("Charmander"))
                .andExpect(jsonPath("$[1].type").value("WATER"));
    }

    @Test
    void byId_ok_and_404() throws Exception {
        var p = new Pokemon("Pikachu", Type.ELECTRIC, 55, 40);
        // simule un id (en prod généré par JPA) : ce n’est pas nécessaire pour ce test car on ne le vérifie pas strictement
        Mockito.when(repo.findById(2L)).thenReturn(Optional.of(p));
        Mockito.when(repo.findById(999L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/pokemons/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Pikachu"))
                .andExpect(jsonPath("$.type").value("ELECTRIC"));

        mvc.perform(get("/api/pokemons/999"))
                .andExpect(status().isNotFound());
    }
}
