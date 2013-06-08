package utilities;

import java.util.Random;

public class AgentsUtilities {
	protected static Random rand = new Random();

	public int randomInt(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	public double randomDouble(double min, double max) {
		return (max - min) * rand.nextDouble() + min;
	}
}
