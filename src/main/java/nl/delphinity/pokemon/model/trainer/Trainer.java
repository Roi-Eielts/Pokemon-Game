package nl.delphinity.pokemon.model.trainer;

import nl.delphinity.pokemon.model.area.Area;
import nl.delphinity.pokemon.model.area.Pokecenter;
import nl.delphinity.pokemon.model.battle.Battle;
import nl.delphinity.pokemon.model.general.Pokemon;
import nl.delphinity.pokemon.model.general.PokemonType;
import nl.delphinity.pokemon.model.item.Inventory;
import nl.delphinity.pokemon.model.item.ItemType;


import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import javax.xml.stream.events.StartDocument;

public class Trainer implements Serializable{
	private final String name;
	private final ArrayList<Pokemon> pokemonCollection = new ArrayList<>();
	private final Inventory inventory = new Inventory();
	private final Random r = new Random();
	private final List<Badge> badges = new ArrayList<>();
	private Pokemon activePokemon;
	private Area currentArea;
	private double money = 100;

	public Trainer(String name, Area startingArea) {
		this.name = name;
		this.inventory.addItem(5, ItemType.POKEBALL);
		this.currentArea = startingArea;
	}
	
	public void newBadge(List<Badge> e) {
		badges.add((Badge) e);
	}

	public Inventory getInventory() {
		return inventory;
	}

	public Pokemon getActivePokemon() {
		return activePokemon;
	}

	public void setActivePokemon(Pokemon activePokemon) {
		this.activePokemon = activePokemon;
	}

	public String getName() {
		return name;
	}

	public ArrayList<Pokemon> getPokemonCollection() {
		return pokemonCollection;
	}


	public List<Badge> getBadges() {
		return badges;
	}

	public Area getCurrentArea() {
		return currentArea;
	}

	public double getMoney() {
		return money;
	}

	public void setMoney(double money) {
		this.money = money;
	}
	
	public void krijgen(Area currentArea) {
		this.currentArea = (Area) currentArea;
	}

	private void setCurrentArea(Area currentArea) {
		this.currentArea = currentArea;
	}

	// TODO: US-PKM-O-5:
	public Battle battle(Pokemon myPokemon, Pokemon otherPokemon) {
		if (myPokemon.getOwner() != null && myPokemon.getAttacks().equals(this)) {
			Battle battle = new Battle(myPokemon, otherPokemon, this);
			return battle;
		} else {
			return null;
		}
	}

	// TODO: US-PKM-O-7
	private boolean catchPokemon(Pokemon pokemon) {
		if (pokemon.getOwner() != null) {
			return false;
		}
		int catchChance = r.nextInt(100);
		if (catchChance > 50) {
			pokemonCollection.add(pokemon);

			pokemon.setOwner(this);
			if (activePokemon == null) {
				setActivePokemon(pokemon);
			}
			return true;
		}
		return false;
	}

	public List<Pokemon> getPokemonByType(PokemonType pokemonType) {
		return pokemonCollection.stream().filter(p -> p.getPokedata().pokemonType.equals(pokemonType))
				.collect(Collectors.toList());
	}

	public void useItem(ItemType item, Battle battle) {
		if (battle == null) {
			System.out.println("Used: " + item.name());
			return;
		}
		switch (item) {
		case POKEBALL:
			if (this.catchPokemon(battle.getEnemy())) {
				battle.setBattleComplete(true);
				battle.setWinner(battle.getMyPokemon());
			}
			this.inventory.removeItem(ItemType.POKEBALL);
			break;
		default:
			break;
		}
	}

	// TODO: US-PKM-O-8
	public Battle challengeTrainer(Trainer opponent) {
		Battle battle = new Battle(getActivePokemon(), opponent.getActivePokemon(), this);
		battle.start();
		return battle;

	}

	// TODO: US-PKM-O-11
	public void travel(Area area) {
		if(area.isUnlocked() == true) {
			System.out.println("traveling to: " + area.getName());
			setCurrentArea(area);
		} else {
			System.out.println("cant travel");
		}
	}

	// TODO: US-PKM-O-3
	public void showPokemonColletion() {
		for (Pokemon p : pokemonCollection) {
			p.status();
		}
	}

	// TODO: US-PKM-O-6
	public Pokemon findPokemon() {
		boolean isSearching = true;
		while (isSearching) {
			int findChance = r.nextInt(100);
			if (findChance > 80) {
				Pokemon foundPokemon = currentArea.getRandomPokemonFromArea(activePokemon.getLevel());
				return foundPokemon;
			} else {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}
		return null;
	}

	// TODO: US-PKM-O-10
	public void showBadges() {
		for (Badge a : badges) {
			System.out.println(a.getName());
		}

	}

	// TODO: US-PKM-O-9
	public void addBadge(Badge newBadge) {
		badges.add(newBadge);
	}

	// TODO: US-PKM-O-5:
	public boolean canBattle() {
		for (Pokemon p : pokemonCollection) {
			if (!p.isKnockout()) {
				return true;
			}
		}
		return false;
	}

	// TODO: US-PKM-O-12
	public void visitPokeCenter(Pokecenter pokecenter) {
		if (pokecenter != null) {
			ArrayList<Pokemon> pokemonToHeal = getPokemonCollection();
			pokecenter.healPokemon(pokemonToHeal);
			System.out.println("pokemons are healed");
		}
	}
}
