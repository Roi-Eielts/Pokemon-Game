package nl.delphinity.pokemon.model.general;

import nl.delphinity.pokemon.model.area.Area;
import nl.delphinity.pokemon.model.area.Pokecenter;
import nl.delphinity.pokemon.model.battle.Battle;
import nl.delphinity.pokemon.model.item.ItemType;
import nl.delphinity.pokemon.model.trainer.Badge;
import nl.delphinity.pokemon.model.trainer.GymLeader;
import nl.delphinity.pokemon.model.trainer.Trainer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import java.util.*;

public class Game implements Serializable{

	private static ArrayList<Area> areas = new ArrayList<>();
	private static final Scanner sc = new Scanner(System.in);
	private static Trainer trainer = null;

	// set up the game in this static block

	static {

		// PEWTER City
		Pokecenter pewterCenter = new Pokecenter("Pewter City's Pokecenter");
		Area pewterCity = new Area("Pewter city", null, true, null, pewterCenter);
		pewterCity.setContainsPokemon(
				Arrays.asList(PokemonType.GRASS, PokemonType.FLYING, PokemonType.BUG, PokemonType.GROUND));

		// VIRIDIAN City
		Pokecenter viridianCenter = new Pokecenter("Viridian City's Pokecenter");
		Area viridianCity = new Area("Viridian city", null, true, pewterCity, viridianCenter);
		viridianCity.setContainsPokemon(
				Arrays.asList(PokemonType.GRASS, PokemonType.FLYING, PokemonType.BUG, PokemonType.GROUND));

		// PALLET Town
		Pokecenter palletCenter = new Pokecenter("Pallet Town's Pokecenter");
		Area palletTown = new Area("Pallet town", null, true, viridianCity, palletCenter);
		palletTown.setContainsPokemon(
				Arrays.asList(PokemonType.GRASS, PokemonType.FLYING, PokemonType.BUG, PokemonType.GROUND));

		areas.add(palletTown);
		areas.add(viridianCity);
		areas.add(pewterCity);

		// SETUP gym leaders
		GymLeader pewterLeader = new GymLeader("Bram", new Badge("Boulder Badge"), pewterCity);
		Pokemon p = new Pokemon(PokemonData.ONIX);
		p.setLevel(5);
		p.setOwner(pewterLeader);
		pewterLeader.setActivePokemon(p);
		pewterLeader.getPokemonCollection().add(p);
		pewterCity.setGymLeader(pewterLeader);
	}

	public static void main(String args[]) {
		System.out.println("did you play this? (yes or no)");
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		if (input.equalsIgnoreCase("yes")) {
			try {
			FileInputStream file = new FileInputStream("C:\\Users\\Roi Eielts\\OneDrive - Scalda\\jaar 1\\prg\\spr7\\opdrachten.txt");
			ObjectInputStream in = new ObjectInputStream(file);

			trainer = (Trainer) in.readObject();
			areas = (ArrayList<Area>) in.readObject();

			System.out.println("welcome back " + trainer.getName());

			System.out.println("you are currently in " + trainer.getCurrentArea().getName());
			in.close();
			file.close();
				// game loop
				while (true) {
					showGameOptions();
				}
			} catch(Exception e) {

			}
		} if (input.equalsIgnoreCase("no")) {
			System.out.println("Welcome new trainer, what's your name?");
			String name = sc.nextLine();
			trainer = new Trainer(name, areas.get(0));
			System.out.println("Hi, " + trainer.getName());

			Pokemon firstPokemon = chooseFirstPokemon();
			firstPokemon.setOwner(trainer);
			trainer.getPokemonCollection().add(firstPokemon);
			System.out.println("You now have " + trainer.getPokemonCollection().size() + " pokemon in your collection!");
			// game loop
			while (true) {
				showGameOptions();
			}



			}if(input.equalsIgnoreCase("clear")) {
			clear();
			} if(!input.equalsIgnoreCase("yes") || !input.equalsIgnoreCase("no")){
			System.out.println("Invalid input, try again and please enter yes or no");
			Game g = new Game();
			g.main(args);
		}
	}

	private static void showGameOptions() {
		System.out.println("What do you want to do?");
		System.out.println("1 ) Find Pokemon");
		System.out.println("2 ) My Pokemon");
		System.out.println("3 ) Inventory");
		System.out.println("4 ) Badges");
		System.out.println("5 ) Challenge " + trainer.getCurrentArea().getName() + "'s Gym Leader");
		System.out.println("6 ) Travel");
		System.out.println("7 ) Visit Pokecenter");
		System.out.println("8 ) save game");
		System.out.println("9 ) Exit game");
		int action = sc.nextInt();
		switch (action) {
		case 1:
			findAndBattlePokemon();
			break;
		case 2:
			trainer.showPokemonColletion();
			break;
		case 3:
			ItemType item = showInventory();
			if (item != null) {
				trainer.useItem(item, null);
			}
			break;
		case 4:
			trainer.showBadges();
			break;
		case 5:
			if (trainer.getCurrentArea().getGymLeader() != null) {
				startGymBattle();
			} else {
				System.out.println("No Gym Leader in this town!");
			}
			break;
		case 6:
			Area area = showTravel();
			if (area != null) {
				trainer.travel(area);
			}
			break;
		case 7:
			trainer.visitPokeCenter(trainer.getCurrentArea().getPokecenter());
			break;
			case 8:
				save();
				break;
		case 9:
			quit();
			break;
		default:
			System.out.println("Sorry, that's not a valid option");
			break;
		}
	}

	public static void clear() {
		System.out.println("clearing stats from the file");
		System.exit(0);
	}
	
	public static void save() {
		try {
			FileOutputStream file = new FileOutputStream("C:\\Users\\Roi Eielts\\OneDrive - Scalda\\jaar 1\\prg\\spr7\\opdrachten.txt");
			ObjectOutputStream out = new ObjectOutputStream(file);
			System.out.println("saving game....");
			
			out.writeObject(trainer);
			out.writeObject(areas);
		} catch (Exception e) {
			System.out.println("something went wrong!");
		}
	}

	private static void findAndBattlePokemon() {
		Pokemon randomPokemon = trainer.findPokemon();
		Battle battle = new Battle(trainer.getActivePokemon(), randomPokemon, trainer);
		battle.start();
	}

	private static Area showTravel() {
		Area travelTo = null;
		int index = 1;
		List<Area> travelToAreas = new ArrayList<>();

		for (Area area : areas) {
			if (!area.equals(trainer.getCurrentArea()) && area.isUnlocked()
					&& ((area.getNextArea() != null && area.getNextArea().equals(trainer.getCurrentArea()))
							|| trainer.getCurrentArea().getNextArea() != null
									&& trainer.getCurrentArea().getNextArea().equals(area))) {
				travelToAreas.add(area);
			}
		}
		for (Area a : travelToAreas) {
			System.out.println(index + ") " + a.getName());
			index++;
		}
		System.out.println(index + ") Back");
		int choice = sc.nextInt();
		if (choice != index) {
			travelTo = travelToAreas.get(choice - 1);
		}
		return travelTo;
	}

	private static ItemType showInventory() {
		HashMap<ItemType, Integer> items = trainer.getInventory().getItems();
		Set<Map.Entry<ItemType, Integer>> entries = items.entrySet();
		int index = 1;
		for (Map.Entry<ItemType, Integer> entry : entries) {
			System.out.println(index + ") " + entry.getKey() + " " + entry.getValue());
			index++;
		}
		System.out.println(index + ") Back");
		int choice = sc.nextInt();
		if (choice != index) {
			return ItemType.values()[choice - 1];
		}
		return null;
	}

	private static Pokemon chooseFirstPokemon() {
		System.out.println("Please choose one of these three pokemon");
		System.out.println("1 ) Charmander");
		System.out.println("2 ) Bulbasaur");
		System.out.println("3 ) Squirtle");
		int choice = sc.nextInt();
		if (choice == 1) {
			Pokemon chosenPokemon = new Pokemon(PokemonData.CHARMANDER);
			chosenPokemon.setLevel(5);
			trainer.setActivePokemon(chosenPokemon);
			return chosenPokemon;
		}
		if (choice == 2) {
			Pokemon chosenPokemon = new Pokemon(PokemonData.BULBASAUR);
			chosenPokemon.setLevel(5);
			trainer.setActivePokemon(chosenPokemon);
			return chosenPokemon;
		}
		if (choice == 3) {
			Pokemon chosenPokemon = new Pokemon(PokemonData.SQUIRTLE);
			chosenPokemon.setLevel(5);
			trainer.setActivePokemon(chosenPokemon);
			return chosenPokemon;
		}

		return chooseFirstPokemon();

	}

	// TODO: US-PKM-O-8
	private static void startGymBattle() {
		Battle trainerBattle = trainer.challengeTrainer(Game.trainer.getCurrentArea().getGymLeader());
		if (trainerBattle != null && trainerBattle.getWinner().getOwner().equals(trainer)) {
			if (trainerBattle.getEnemy().getOwner().getClass().equals(GymLeader.class)) {
				Pokemon enemyPokemon = trainerBattle.getEnemy();
				Trainer gymLeader = enemyPokemon.getOwner();
				((GymLeader) gymLeader).setDefeated(true); // upcast want je gaat van een child naar een parent class
				awardBadge(((GymLeader) gymLeader).getBadge().getName());

				Area gymLeaderArea = gymLeader.getCurrentArea();
				Area nextArea = gymLeaderArea.getNextArea();
				if (nextArea != null) {
					nextArea.setUnlocked(true);
				}
			}
		}
	}

	// TODO: US-PKM-O-9
	public static void awardBadge(String badgeName) {
		Badge newBadge = new Badge(badgeName);
		trainer.addBadge(newBadge);
	}

	public static void gameOver(String message) {
		System.out.println(message);
		System.out.println("Game over");
		quit();
	}

	private static void quit() {
		save();
		System.out.println("ending game");
		// Saving of object in a file


		System.exit(0);
	}
}
