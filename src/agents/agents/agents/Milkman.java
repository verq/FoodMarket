package agents;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Milkman extends MarketEmployeeAgent {

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.MILK, numberOfEmployees * MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.MILKMAN;
		numberOfEmployees = AgentsUtilities.randomInt(MarketConstants.MILKMAN_MIN_EMPLOYEE, MarketConstants.MILKMAN_MAX_EMPLOYEE);
		have.put(Products.MILK, AgentsUtilities.randomDouble(0.5, 1) * numberOfEmployees * MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT);
		have.put(Products.MILK_PRODUCT,
				(double) AgentsUtilities.randomInt(MarketConstants.MILKMAN_MIN_MILK_PRODUCT, MarketConstants.MILKMAN_MAX_MILK_PRODUCT));

	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.MILK_PRODUCT, have.get(Products.MILK_PRODUCT));
		pricePerItem.put(Products.MILK_PRODUCT, MarketConstants.MILKMAN_MILK_COST);
	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.KEEPER, Products.MILK);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.CLIENT, Products.MILK_PRODUCT);
	}

	@Override
	protected ArrayList<AgentOffer> decideAboutSellOffer(ArrayList<AgentOffer> offers) {
		return null;
		// TODO Auto-generated method stub

	}

	@Override
	protected ArrayList<AgentOffer> decideAboutBuyOffer(ArrayList<AgentOffer> offers) {
		return null;
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
	public Map<String, Boolean> composeFinalBuyingDecision(ArrayList<AgentOffer> sellOffers) {
		// TODO Auto-generated method stub
		return new HashMap<String, Boolean>();
	}

	@Override
	protected double getUsedProductUsage() {
		return MarketConstants.MILKMAN_MILK_PER_PRODUCT;
	}

	@Override
	protected Products getProductUsedToProduce(Products product) {
		if (product == Products.MILK_PRODUCT) {
			return Products.MILK;
		}
		return null;
	}

	@Override
	protected double getNumberOfProductPerEmployee() {
		return MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT;
	}

}
