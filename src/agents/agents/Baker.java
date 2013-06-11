package agents;

import java.util.ArrayList;
import java.util.Map;

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Baker extends MarketEmployeeAgent {
	@Override
	protected void fillInitialBuy() {
		buy.put(Products.GRAIN, numberOfEmployees
				* MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.BAKER;
		numberOfEmployees = AgentsUtilities.randomInt(
				MarketConstants.BAKER_MIN_EMPLOYEE,
				MarketConstants.BAKER_MAX_EMPLOYEE);
		have.put(Products.GRAIN, AgentsUtilities.randomDouble(0.5, 1)
				* numberOfEmployees
				* MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
		have.put(Products.BREAD, (double) AgentsUtilities.randomInt(
				MarketConstants.BAKER_MIN_BREAD_PRODUCT,
				MarketConstants.BAKER_MAX_BREAD_PRODUCT));

	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.BREAD, have.get(Products.BREAD));
		pricePerItem.put(Products.BREAD, MarketConstants.BAKER_BREAD_COST);

	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.FARMER, Products.GRAIN);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.CLIENT, Products.BREAD);
	}

	@Override
	protected void updateResources() {
		// TODO Auto-generated method stub

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
	public boolean confirmSellTransactionWith(String traderName) {
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
		return null;
	}

	@Override
	protected double getUsedProductUsage() {
		return MarketConstants.BAKER_GRAIN_PER_BREAD;
	}

	@Override
	protected Products getProductUsedToProduce(Products product) {
		if (product == Products.BREAD) {
			return Products.GRAIN;
		}
		return null;
	}

	@Override
	protected double getNumberOfProductPerEmployee() {
		return MarketConstants.BAKER_PRODUCTIVITY_CONSTANT;
	}

}
