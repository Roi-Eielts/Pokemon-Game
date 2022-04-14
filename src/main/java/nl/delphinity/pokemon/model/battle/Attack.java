package nl.delphinity.pokemon.model.battle;

import java.io.Serializable;

public class Attack implements Serializable{

    private final String name;
    private final int power;
    private final int accuracy;

    public Attack(String name, int power, int accuracy) {
        this.name = name;
        this.power = power;
        this.accuracy = accuracy;
    }

    public String getName() {
        return name;
    }

    public int getPower() {
        return power;
    }
}
