package constants;

public enum Products {
	VEGETABLE("vegetable"), FRUIT("fruit"), MILK_PRODUCT("milk_product"), MEAT("meat"), BREAD("bread"), ANIMAL("animal"), GRAIN(
			"grain"), MANURE("manure"), MILK("milk");

	private String productName;

	private Products(String productName) {
		this.productName = productName;
	}

	public int getNumberOfProducts() {
		return MILK.ordinal();
	}
}
