package agents;

import java.util.ArrayList;
import java.util.Map;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;
import utilities.*;

public class Milkman extends MarketEmployeeAgent {

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.MILK, numberOfEmployees * MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.MILKMAN;
		numberOfEmployees = AgentsUtilities.randomInt(MarketConstants.MILKMAN_MIN_EMPLOYEE, MarketConstants.MILKMAN_MAX_EMPLOYEE);
		have.put(Products.MILK, AgentsUtilities.randomDouble(0.5, 1) * numberOfEmployees
				* MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT);
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
	protected void updateResources() {
		// TODO Auto-generated method stub
		
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
