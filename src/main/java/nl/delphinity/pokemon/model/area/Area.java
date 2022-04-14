package nl.delphinity.pokemon.model.area;

import nl.delphinity.pokemon.model.general.Pokemon;
import nl.delphinity.pokemon.model.general.PokemonData;
import nl.delphinity.pokemon.model.general.PokemonType;
import nl.delphinity.pokemon.model.trainer.GymLeader;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Area implements Serializable {

	private final String name;
	private final Area nextArea;
	private final Random r = new Random();
	private final Pokecenter pokecenter;
	private List<PokemonType> containsPokemon = new ArrayList<>();
	private boolean isUnlocked;
	private GymLeader gymLeader;

	public Area(String name, GymLeader gymLeader, boolean isUnlocked, Area nextArea, Pokecenter pokecenter) {
		this.name = name;
		this.gymLeader = gymLeader;
		this.isUnlocked = isUnlocked;
		this.nextArea = nextArea;
		this.pokecenter = pokecenter;
	}

	public void setContainsPokemon(List<PokemonType> containsPokemon) {
		this.containsPokemon = containsPokemon;
	}
	public String toString() { return name;}

	public String getName() {
		return name;
	}

	public boolean isUnlocked() {
		return isUnlocked;
	}

	public void setUnlocked(boolean unlocked) {
		isUnlocked = unlocked;
	}

	public GymLeader getGymLeader() {
		return gymLeader;
	}

	public void setGymLeader(GymLeader gymLeader) {
		this.gymLeader = gymLeader;
	}

	public Area getNextArea() {
		return nextArea;
	}

	public Pokemon getRandomPokemonFromArea(int level) {
		List<PokemonData> pokeData = Arrays.asList(PokemonData.values());
		List<PokemonData> collect = new ArrayList();

		for (PokemonData p : pokeData) {
			if (containsPokemon.contains(p.pokemonType)) {
				collect.add(p);
			}
		}
		int randomPokemonOnIndex = r.nextInt(collect.size());
		PokemonData randomPokemonData = collect.get(randomPokemonOnIndex);
		
		Pokemon randomPokemon = new Pokemon(randomPokemonData);
		
		
		int lowestLevel = level - 5 > 0 ? level - 5 : level;
		int HighestLevel = lowestLevel + 5;
		 
		int randomLevel = r.nextInt(HighestLevel - lowestLevel);
		
		randomPokemon.setLevel(randomLevel);
		randomPokemon.setMaxHp(randomPokemon.getLevel());
		randomPokemon.setCurrentHp(randomPokemon.getMaxHp());
		return randomPokemon;
	}

	public Pokecenter getPokecenter() {
		return pokecenter;
	}
}
