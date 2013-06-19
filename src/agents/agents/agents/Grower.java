package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
	protected ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub

	}

	@Override
	public boolean confirmSellTransactionWith(String traderName, String offer) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		// TODO Auto-generated method stub

	}

	@Override
	public Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers) {
		// TODO Auto-generated method stub
		return new HashMap<String, Boolean>();
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
