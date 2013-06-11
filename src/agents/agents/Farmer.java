package agents;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Farmer extends MarketFieldAgent {

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.MANURE, numberOfFields * MarketConstants.FARMER_MANURE_NEEDED_FOR_FIELD);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.FARMER;
		numberOfFields = random(MarketConstants.FARMER_MIN_FIELD, MarketConstants.FARMER_MAX_FIELD);
		have.put(Products.GRAIN, randomDouble(0.5, 1) * numberOfFields
				* MarketConstants.FARMER_MAX_GRAIN_NEEDED_PER_FIELD);
	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.GRAIN, numberOfFields * MarketConstants.FARMER_TRESHOLD_SOLD_GRAIN); // he can't sell all
		pricePerItem.put(Products.GRAIN, MarketConstants.FARMER_GRAIN_COST);
		sell.put(Products.VEGETABLE, numberOfFields * MarketConstants.FARMER_TRESHOLD_SOLD_VEGETABLES);
		pricePerItem.put(Products.VEGETABLE, MarketConstants.FARMER_VEGETABLE_COST);
	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.KEEPER, Products.MANURE);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.KEEPER, Products.GRAIN);
		sellTo.put(Participants.BAKER, Products.GRAIN);
		sellTo.put(Participants.CLIENT, Products.VEGETABLE);
	}

	@Override
	protected double getNumberOfProductsPerField(Products product) {
		if (product == Products.GRAIN) {
			return MarketConstants.FARMER_GRAIN_FROM_FIELD;
		} else if (product == Products.VEGETABLE) {
			return MarketConstants.FARMER_VEGETABLE_FROM_FIELD;
		} else {
			return 0;
		}
	}
}
