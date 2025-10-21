package dev.pokedex.web;

import dev.pokedex.domain.Type;

public class PokemonDto {
    private Long id;
    private String name;
    private Type type;
    private int baseAttack;
    private int baseDefense;

    public PokemonDto(Long id, String name, Type type, int baseAttack, int baseDefense) {
        this.id = id; this.name = name; this.type = type;
        this.baseAttack = baseAttack; this.baseDefense = baseDefense;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public Type getType() { return type; }
    public int getBaseAttack() { return baseAttack; }
    public int getBaseDefense() { return baseDefense; }
}
