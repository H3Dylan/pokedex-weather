package dev.pokedex;

import dev.pokedex.domain.Pokemon;
import dev.pokedex.domain.Type;
import dev.pokedex.repo.PokemonRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class PokedexApplication {
    public static void main(String[] args) {
        SpringApplication.run(PokedexApplication.class, args);
    }

    @Bean
    CommandLineRunner seed(PokemonRepository repo) {
        return args -> {
            if (repo.count() == 0) {
                repo.save(new Pokemon("Charmander", Type.FIRE, 52, 43));
                repo.save(new Pokemon("Squirtle", Type.WATER, 48, 65));
                repo.save(new Pokemon("Bulbasaur", Type.GRASS, 49, 49));
                repo.save(new Pokemon("Pikachu", Type.ELECTRIC, 55, 40));
            }
        };
    }
}
