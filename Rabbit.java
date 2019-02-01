import java.util.List;

public class Rabbit extends Animal {
	// Characteristics shared by all rabbits (class variables).

	// The age at which a rabbit can start to breed.
	private static final int BREEDING_AGE = 5;
	// The age to which a rabbit can live.
	private static final int MAX_AGE = 40;
	// The likelihood of a rabbit breeding.
	private static final double BREEDING_PROBABILITY = 0.12;
	// The maximum number of births.
	private static final int MAX_LITTER_SIZE = 4;

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
		return new Rabbit(randomAge, field, location);
	}

	/**
	 * Create a new rabbit. A rabbit may be created with age zero (a new born) or
	 * with a random age.
	 * 
	 * @param randomAge If true, the rabbit will have a random age.
	 * @param field     The field currently occupied.
	 * @param location  The location within the field.
	 */
	public Rabbit(boolean randomAge, Field field, Location location) {
		super(randomAge, field, location);
	}

	/**
	 * This is what the rabbit does most of the time - it runs around. Sometimes it
	 * will breed or die of old age.
	 * 
	 * @param newRabbits A list to return newly born rabbits.
	 */
	@Override
	public void act(List<Animal> newAnimals) {
		incrementAge();
		if (isAlive()) {
			giveBirth(newAnimals);
			// Try to move into a free location.
			Location newLocation = getField().freeAdjacentLocation(getLocation());
			if (newLocation != null) {
				setLocation(newLocation);
			} else {
				// Overcrowding.
				setDead();
			}
		}
	}

}