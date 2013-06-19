package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Keeper extends MarketFieldAgent {
	@Override
	protected void fillInitialBuy() {
		buy.put(Products.GRAIN, numberOfFields
				* MarketConstants.KEEPER_GRAIN_NEEDED_FOR_ANIMAL);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.KEEPER;
		numberOfFields = AgentsUtilities.randomInt(
				MarketConstants.KEEPER_MIN_ANIMAL,
				MarketConstants.KEEPER_MAX_ANIMAL);
		have.put(Products.GRAIN, AgentsUtilities.randomDouble(0.5, 1)
				* numberOfFields
				* MarketConstants.KEEPER_GRAIN_NEEDED_FOR_ANIMAL);
		have.put(Products.MANURE, (double) AgentsUtilities.randomInt(
				MarketConstants.KEEPER_MIN_MANURE,
				MarketConstants.KEEPER_MAX_MANURE));
		have.put(Products.MEAT, (double) AgentsUtilities.randomInt(
				MarketConstants.KEEPER_MIN_MEAT,
				MarketConstants.KEEPER_MAX_MEAT));
		have.put(Products.MILK, (double) AgentsUtilities.randomInt(
				MarketConstants.KEEPER_MIN_MILK,
				MarketConstants.KEEPER_MAX_MILK));

	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.MEAT, numberOfFields
				* MarketConstants.KEEPER_TRESHOLD_KILLED_ANIMALS
				* MarketConstants.KEEPER_MEET_PER_ANIMAL); // he can't sell all
		pricePerItem.put(Products.MEAT, MarketConstants.KEEPER_MEAT_COST);
		sell.put(Products.MILK, have.get(Products.MILK));
		pricePerItem.put(Products.MILK, MarketConstants.KEEPER_MILK_COST);
		sell.put(Products.MANURE, have.get(Products.MANURE));
		pricePerItem.put(Products.MANURE, MarketConstants.KEEPER_MANURE_COST);

	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.FARMER, Products.GRAIN);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.CLIENT, Products.MEAT);
		sellTo.put(Participants.MILKMAN, Products.MILK);
		sellTo.put(Participants.GROWER, Products.MANURE);
		sellTo.put(Participants.FARMER, Products.MANURE);
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
		if (product == Products.MANURE) {
			return MarketConstants.KEEPER_MANURE_FROM_ANIMAL;
		} else if (product == Products.MILK) {
			return MarketConstants.KEEPER_MILK_FROM_ANIMAL;
		} else if (product == Products.MEAT) {
			return MarketConstants.KEEPER_MEET_PER_ANIMAL;
		} else {
			return 0;
		}
	}

}
