package agents;

import java.util.EnumMap;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Client extends MarketAgent {
	private double income;
	private EnumMap<Products, Integer> weekProductNeeds;

	@Override
	protected void fillInitialBuy() {
		for (Products p : Products.values()) {
			if (MarketConstants.CLIENT_NEEDS_MAX.containsKey(p) && MarketConstants.CLIENT_NEEDS_MIN.containsKey(p)) {
				double productNeed = randomDouble(MarketConstants.CLIENT_NEEDS_MIN.get(p),
						MarketConstants.CLIENT_NEEDS_MAX.get(p));
				if (productNeed != 0) {
					buy.put(p, productNeed);
				}
			}
		}
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.CLIENT;
		income = randomDouble(MarketConstants.CLIENT_MIN_INCOME, MarketConstants.CLIENT_MAX_INCOME);
		money = income;
	}

	@Override
	protected void fillInitialSell() {
		// nothing
	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.BAKER, Products.BREAD);
		buyFrom.put(Participants.FARMER, Products.VEGETABLE);
		buyFrom.put(Participants.KEEPER, Products.MEAT);
		buyFrom.put(Participants.GROWER, Products.FRUIT);
		buyFrom.put(Participants.MILKMAN, Products.MILK_PRODUCT);
	}

	@Override
	protected void fillSellTo() {
		// nothing
	}

	@Override
	protected void produceAndUse() {
		money += money + income;
		use();
	}

	protected void use() {
		// TODO
	}

}
