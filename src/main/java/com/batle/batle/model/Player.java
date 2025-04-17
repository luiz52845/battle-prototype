package com.batle.batle.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Data
public class Player {


    private String id;
    private String name;
    private int vida;
    private Map<Habilidade, Long> cooldowns;

    public Player() {
        this.vida = 100;
        this.cooldowns = new HashMap<>();
    }

    public Player(String id, String name, int vida, Map<Habilidade, Long> cooldowns) {
        this.id = id;
        this.name = name;
        this.vida = vida;
        this.cooldowns = cooldowns != null ? cooldowns : new HashMap<>();
    }

    public boolean podeUsar(Habilidade h) {
        long agora = System.currentTimeMillis();
        return !cooldowns.containsKey(h) || cooldowns.get(h) < agora;
    }

    public void aplicarCooldown(Habilidade h, long segundos) {
        cooldowns.put(h, System.currentTimeMillis() + segundos * 1000);
    }

    // Getters e Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getVida() {
        return vida;
    }

    public void setVida(int vida) {
        this.vida = vida;
    }

    public Map<Habilidade, Long> getCooldowns() {
        return cooldowns;
    }

    public void setCooldowns(Map<Habilidade, Long> cooldowns) {
        this.cooldowns = cooldowns;
    }
}