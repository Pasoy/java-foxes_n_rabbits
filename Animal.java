import java.util.List;
import java.util.Random;

public abstract class Animal {
	// Individual characteristics (instance fields).

	// The rabbit's age.
	private int age;
	// Whether the rabbit is alive or not.
	private boolean alive;
	// The rabbit's position.
	private Location location;
	// The field occupied.
	private Field field;
	// A shared random number generator to control breeding.
	private static final Random rand = Randomizer.getRandom();

	public Animal(boolean randomAge, Field field, Location location) {
		age = 0;
		alive = true;
		this.field = field;
		setLocation(location);
		if (randomAge) {
			age = rand.nextInt(getMaxAge());
		}

	}

	public abstract void act(List<Animal> newAnimals);

	public abstract int getBreedingAge();

	public abstract int getMaxAge();

	public abstract double getBreedingProbability();

	public abstract int getMaxLitterSize();

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public Field getField() {
		return field;
	}

	public void setField(Field field) {
		this.field = field;
	}

	public void setAlive(boolean alive) {
		this.alive = alive;
	}

	/**
	 * Check whether the rabbit is alive or not.
	 * 
	 * @return true if the rabbit is still alive.
	 */
	public boolean isAlive() {
		return alive;
	}

	/**
	 * Return the rabbit's location.
	 * 
	 * @return The rabbit's location.
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Place the fox at the new location in the given field.
	 * 
	 * @param newLocation The fox's new location.
	 */
	protected void setLocation(Location newLocation) {
		if (location != null) {
			field.clear(location);
		}
		location = newLocation;
		field.place(this, newLocation);
	}

	/**
	 * Increase the age. This could result in the rabbit's death.
	 */
	protected void incrementAge() {
		age++;
		if (age > getMaxAge()) {
			setDead();
		}
	}

	/**
	 * Generate a number representing the number of births, if it can breed.
	 * 
	 * @return The number of births (may be zero).
	 */
	protected int breed() {
		int births = 0;
		if (canBreed() && rand.nextDouble() <= getBreedingProbability()) {
			births = rand.nextInt(getMaxLitterSize()) + 1;
		}
		return births;
	}

	/**
	 * A rabbit can breed if it has reached the breeding age.
	 * 
	 * @return true if the rabbit can breed, false otherwise.
	 */
	protected boolean canBreed() {
		return age >= getBreedingAge();
	}

	/**
	 * Indicate that the rabbit is no longer alive. It is removed from the field.
	 */
	public void setDead() {
		alive = false;
		if (location != null) {
			field.clear(location);
			location = null;
			field = null;
		}
	}

	/**
	 * Check whether or not this fox is to give birth at this step. New births will
	 * be made into free adjacent locations.
	 * 
	 * @param newFoxes A list to return newly born foxes.
	 */
	protected void giveBirth(List<Animal> newAnimals) {
		// New foxes are born into adjacent locations.
		// Get a list of adjacent free locations.
		List<Location> free = field.getFreeAdjacentLocations(location);
		int births = breed();
		for (int b = 0; b < births && free.size() > 0; b++) {
			Location loc = free.remove(0);
			Animal young = animalFactory(false, field, loc);
			newAnimals.add(young);
		}
	}

	protected abstract Animal animalFactory(boolean randomAge, Field field, Location location);

}
