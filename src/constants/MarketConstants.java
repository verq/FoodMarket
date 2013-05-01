package constants;

import java.util.HashMap;
import java.util.Map;

public class MarketConstants {
	// products id
	public static final int VEGETABLE = 0;
	public static final int FRUIT = 1;
	public static final int MILK_PRODUCT = 2;
	public static final int MEAT = 3;
	public static final int BREAD = 4;
	public static final int ANIMAL = 5;
	public static final int GRAIN = 6;
	public static final int MANURE = 7;
	public static final int MILK = 8;
	public static final int NUMBER_OF_PRODUCTS = 9;

	// participants id
	public static final int CLIENT = 0;
	public static final int BAKER = 1;
	public static final int MILKMAN = 2;
	public static final int GROWER = 3;
	public static final int KEEPER = 4;
	public static final int FARMER = 5;

	// client
	public static final int CLIENT_MIN_INCOME = 1300;
	public static final int CLIENT_MAX_INCOME = 4000;

	public static Map<Integer, Integer> CLIENT_NEEDS_MIN = new HashMap<Integer, Integer>();
	static {
		CLIENT_NEEDS_MIN.put(VEGETABLE, 1);
		CLIENT_NEEDS_MIN.put(FRUIT, 1);
		CLIENT_NEEDS_MIN.put(MILK_PRODUCT, 1);
		CLIENT_NEEDS_MIN.put(MEAT, 1);
		CLIENT_NEEDS_MIN.put(BREAD, 1);
	}

	public static Map<Integer, Integer> CLIENT_NEEDS_MAX = new HashMap<Integer, Integer>();
	static {
		CLIENT_NEEDS_MAX.put(VEGETABLE, 5);
		CLIENT_NEEDS_MAX.put(FRUIT, 5);
		CLIENT_NEEDS_MAX.put(MILK_PRODUCT, 5);
		CLIENT_NEEDS_MAX.put(MEAT, 4);
		CLIENT_NEEDS_MAX.put(BREAD, 5);
	}

	// baker
	public static final int BAKER_MIN_EMPLOYEE = 1;
	public static final int BAKER_MAX_EMPLOYEE = 10;
	public static final int BAKER_PRODUCTIVITY_CONSTANT = 5;
	public static final int BAKER_EMPLOYEE_PRODUCTIVITY = 20;

	// milkman

	// keeper
	// grower
	// farmer

}
