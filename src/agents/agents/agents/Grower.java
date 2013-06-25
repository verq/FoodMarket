package agents;

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Grower extends MarketFieldAgent {

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.MANURE, numberOfFields
				* MarketConstants.GROWER_MANURE_NEEDED_FOR_FIELD);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.GROWER;
		numberOfFields = AgentsUtilities.randomInt(
				MarketConstants.GROWER_MIN_FIELD,
				MarketConstants.GROWER_MAX_FIELD);
		have.put(Products.MANURE, AgentsUtilities.randomDouble(0.5, 1)
				* numberOfFields
				* MarketConstants.GROWER_MANURE_NEEDED_FOR_FIELD);
		have.put(Products.FRUIT, (double) AgentsUtilities.randomInt(
				MarketConstants.GROWER_MIN_FRUIT,
				MarketConstants.GROWER_MAX_FRUIT));
		money = AgentsUtilities.randomDouble(MarketConstants.CLIENT_MIN_INCOME, MarketConstants.CLIENT_MAX_INCOME);
	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.FRUIT, have.get(Products.FRUIT));
		pricePerItem.put(Products.FRUIT, MarketConstants.GROWER_FRUIT_COST);
	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.KEEPER, Products.MANURE);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.CLIENT, Products.FRUIT);
	}
	@Override
	protected double getNumberOfProductsPerField(Products product) {
		if (product == Products.FRUIT) {
			return MarketConstants.GROWER_FRUIT_PER_FIELD;
		} else {
			return 0;
		}
	}

}
