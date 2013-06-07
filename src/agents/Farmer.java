package agents;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Farmer extends MarketFieldAgent {
	private int grainCost;

	@Override
	protected void fillInitialBuy() {
	}

	@Override
	protected void fillInitialHave() {
		have.put(Products.GRAIN, random(MarketConstants.FARMER_MIN_GRAIN, MarketConstants.FARMER_MAX_GRAIN));
		have.put(Products.MANURE, random(MarketConstants.FARMER_MIN_MANURE, MarketConstants.FARMER_MAX_MANURE));
		numberOfAvailableFields = random(MarketConstants.FARMER_MIN_FIELD, MarketConstants.FARMER_MAX_FIELD);
		numberOfUsedFields = numberOfAvailableFields;
	}

	@Override
	protected void fillInitialSell() {
		grainCost = MarketConstants.FARMER_GRAIN_COST;
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
}
