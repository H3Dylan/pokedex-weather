package dev.pokedex.web;

import dev.pokedex.domain.Pokemon;
import dev.pokedex.repo.PokemonRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pokemons")
public class PokemonController {

    private final PokemonRepository repo;

    public PokemonController(PokemonRepository repo) {
        this.repo = repo;
    }

    // 🔹 READ all
    @GetMapping
    public List<Pokemon> all() {
        return repo.findAll();
    }

    // 🔹 CREATE
    @PostMapping
    public Pokemon create(@RequestBody Pokemon p) {
        return repo.save(p);
    }

    // 🔹 READ by id
    @GetMapping("/{id}")
    public ResponseEntity<?> byId(@PathVariable("id") Long id) {
        return repo.findById(id)
                .<ResponseEntity<?>>map(p -> {
                    // On construit une réponse simple sans proxy Hibernate
                    java.util.Map<String, Object> out = new java.util.LinkedHashMap<>();
                    out.put("id", p.getId());
                    out.put("name", p.getName());
                    out.put("type", p.getType().name());
                    out.put("baseAttack", p.getBaseAttack());
                    out.put("baseDefense", p.getBaseDefense());
                    return ResponseEntity.ok(out);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // 🔹 UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Pokemon> update(@PathVariable("id") Long id, @RequestBody Pokemon in) {
        return repo.findById(id).map(p -> {
            p.setName(in.getName());
            p.setType(in.getType());
            p.setBaseAttack(in.getBaseAttack());
            p.setBaseDefense(in.getBaseDefense());
            return ResponseEntity.ok(repo.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // 🔹 DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // 🔹 DEBUG (facultatif)
    @GetMapping("/debug/{id}")
    public ResponseEntity<String> debugById(@PathVariable("id") Long id) {
        var opt = repo.findById(id);
        if (opt.isEmpty()) return ResponseEntity.status(404).body("not found");
        var p = opt.get();
        String out = "id=" + p.getId()
                + ", name=" + p.getName()
                + ", type=" + (p.getType() == null ? "null" : p.getType().name())
                + ", atk=" + p.getBaseAttack()
                + ", def=" + p.getBaseDefense();
        return ResponseEntity.ok(out);
    }
}
