package agents;

import java.util.ArrayList;
import java.util.Map;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;
import utilities.*;

public class Farmer extends MarketFieldAgent {
	private double grainCost;

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.MANURE, numberOfFields * MarketConstants.FARMER_MANURE_NEEDED_FOR_FIELD);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.FARMER;
		numberOfFields = AgentsUtilities.randomInt(MarketConstants.FARMER_MIN_FIELD, MarketConstants.FARMER_MAX_FIELD);
		have.put(Products.GRAIN, AgentsUtilities.randomDouble(0.5, 1) * numberOfFields
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
	protected void updateResources() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<AgentOffer> decideAboutSellOffer(ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<AgentOffer> decideAboutBuyOffer(ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Boolean> composeFinalBuyingDecision(
			Map<String, String> sellOffers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirmSellTransactionWith(String traderName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		// TODO Auto-generated method stub
		
	}
}
