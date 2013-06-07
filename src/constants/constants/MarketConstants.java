package constants;

import java.util.HashMap;
import java.util.Map;

public class MarketConstants {
	public static final int WEEK = 12000;
	// client
	public static final double CLIENT_MIN_INCOME = 1300;
	public static final double CLIENT_MAX_INCOME = 4000;

	public static Map<Products, Integer> CLIENT_NEEDS_MIN = new HashMap<Products, Integer>();
	static {
		CLIENT_NEEDS_MIN.put(Products.VEGETABLE, 1);
		CLIENT_NEEDS_MIN.put(Products.FRUIT, 1);
		CLIENT_NEEDS_MIN.put(Products.MILK_PRODUCT, 1);
		CLIENT_NEEDS_MIN.put(Products.MEAT, 1);
		CLIENT_NEEDS_MIN.put(Products.BREAD, 1);
	}

	public static Map<Products, Integer> CLIENT_NEEDS_MAX = new HashMap<Products, Integer>();
	static {
		CLIENT_NEEDS_MAX.put(Products.VEGETABLE, 5);
		CLIENT_NEEDS_MAX.put(Products.FRUIT, 5);
		CLIENT_NEEDS_MAX.put(Products.MILK_PRODUCT, 5);
		CLIENT_NEEDS_MAX.put(Products.MEAT, 4);
		CLIENT_NEEDS_MAX.put(Products.BREAD, 5);
	}

	// baker
	public static final int BAKER_MIN_EMPLOYEE = 1;
	public static final int BAKER_MAX_EMPLOYEE = 10;

	public static final double BAKER_PRODUCTIVITY_CONSTANT = 5;
	public static final double BAKER_EMPLOYEE_PRODUCTIVITY = 20;

	public static final double BAKER_BREAD_COST = 3;

	// milkman
	public static final int MILKMAN_MIN_EMPLOYEE = 1;
	public static final int MILKMAN_MAX_EMPLOYEE = 10;

	public static final double MILKMAN_PRODUCTIVITY_CONSTANT = 5;
	public static final double MILKMAN_EMPLOYEE_PRODUCTIVITY = 20 * 7; // per week

	public static final double MILKMAN_MIN_MILK_COST = 3;

	// keeper
	public static final int KEEPER_MIN_ANIMAL = 5;
	public static final int KEEPER_MAX_ANIMAL = 20;
	public static final double KEEPER_ANIMAL_COST = 12;

	public static final double KEEPER_MANURE_FROM_ANIMAL = 3; // per week
	public static final double KEEPER_MILK_FROM_ANIMAL = 7;
	public static final double KEEPER_TRESHOLD_KILLED_ANIMALS = 0.5;

	public static final double KEEPER_GRAIN_NEEDED_FOR_ANIMAL = 7;

	public static final double KEEPER_MEAT_COST = 6;
	public static final double KEEPER_MILK_COST = 3;

	// grower
	public static final double GROWER_MIN_FIELD = 100;
	public static final double GROWER_MAX_FIELD = 500;
	public static final double GROWER_FIELD_COST = 12;

	public static final double GROWER_FRUIT_PER_FIELD = 2;
	public static final double GROWER_MANURE_NEEDED_FOR_FIELD = 1.5;

	public static final double GROWER_FRUIT_COST = 3;

	// farmer
	public static final int FARMER_MIN_FIELD = 200;
	public static final int FARMER_MAX_FIELD = 1000;
	public static final double FARMER_FIELD_COST = 12;
	public static final double FARMER_TRESHOLD_SOLD_GRAIN = 0.5;
	public static final double FARMER_TRESHOLD_SOLD_VEGETABLES = 0.5;

	public static final double FARMER_GRAIN_FROM_FIELD = 2;
	public static final double FARMER_MANURE_NEEDED_FOR_FIELD = 1;
	public static final double FARMER_MAX_GRAIN_NEEDED_PER_FIELD = 2;

	public static final double FARMER_GRAIN_COST = 2;
	public static final double FARMER_VEGETABLE_COST = 2;


}
