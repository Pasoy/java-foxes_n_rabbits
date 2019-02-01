import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Fox extends Animal {
	// Characteristics shared by all foxes (class variables).

	// The age at which a fox can start to breed.
	private static final int BREEDING_AGE = 15;
	// The age to which a fox can live.
	private static final int MAX_AGE = 150;
	// The likelihood of a fox breeding.
	private static final double BREEDING_PROBABILITY = 0.08;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 2;
	// The food value of a single rabbit. In effect, this is the
	// number of steps a fox can go before it has to eat again.
	private static final int RABBIT_FOOD_VALUE = 9;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	// Individual characteristics (instance fields).

	// The fox's food level, which is increased by eating rabbits.
	private int foodLevel;

	@Override
	public int getBreedingAge() {
		return BREEDING_AGE;
	}

	@Override
	public int getMaxAge() {
		return MAX_AGE;
	}

	@Override
	public double getBreedingProbability() {
		return BREEDING_PROBABILITY;
	}

	@Override
	public int getMaxLitterSize() {
		return MAX_LITTER_SIZE;
	}

	@Override
	protected Animal animalFactory(boolean randomAge, Field field, Location location) {
		return new Fox(randomAge, field, location);
	}

	/**
	 * Create a fox. A fox can be created as a new born (age zero and not hungry) or
	 * with a random age and food level.
	 * 
	 * @param randomAge If true, the fox will have random age and hunger level.
	 * @param field     The field currently occupied.
	 * @param location  The location within the field.
	 */
	public Fox(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
		if (randomAge) {
			foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
		} else {
			// leave age at 0
			foodLevel = rand.nextInt(RABBIT_FOOD_VALUE);
		}
	}

	/**
	 * This is what the fox does most of the time: it hunts for rabbits. In the
	 * process, it might breed, die of hunger, or die of old age.
	 * 
	 * @param field    The field currently occupied.
	 * @param newFoxes A list to return newly born foxes.
	 */
	@Override
	public void act(List<Animal> newAnimals) {
		incrementAge();
		incrementHunger();
		if (isAlive()) {
			giveBirth(newAnimals);
			// Move towards a source of food if found.
			Location newLocation = findFood();
			if (newLocation == null) {
				// No food found - try to move to a free location.
				newLocation = getField().freeAdjacentLocation(getLocation());
			}
			// See if it was possible to move.
			if (newLocation != null) {
				setLocation(newLocation);
			} else {
				// Overcrowding.
				setDead();
			}
		}
	}

	/**
	 * Make this fox more hungry. This could result in the fox's death.
	 */
	private void incrementHunger() {
		foodLevel--;
		if (foodLevel <= 0) {
			setDead();
		}
	}

	/**
	 * Look for rabbits adjacent to the current location. Only the first live rabbit
	 * is eaten.
	 * 
	 * @return Where food was found, or null if it wasn't.
	 */
	private Location findFood() {
		List<Location> adjacent = getField().adjacentLocations(getLocation());
		Iterator<Location> it = adjacent.iterator();
		while (it.hasNext()) {
			Location where = it.next();
			Object animal = getField().getObjectAt(where);
			if (animal instanceof Rabbit) {
				Rabbit rabbit = (Rabbit) animal;
				if (rabbit.isAlive()) {
					rabbit.setDead();
					foodLevel = RABBIT_FOOD_VALUE;
					return where;
				}
			}
		}
		return null;
	}

}
