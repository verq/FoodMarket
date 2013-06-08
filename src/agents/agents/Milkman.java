package agents;

import java.util.ArrayList;

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
		numberOfEmployees = randomInt(MarketConstants.MILKMAN_MIN_EMPLOYEE, MarketConstants.MILKMAN_MAX_EMPLOYEE);
		have.put(Products.MILK, randomDouble(0.5, 1) * numberOfEmployees
				* MarketConstants.MILKMAN_PRODUCTIVITY_CONSTANT);
		have.put(Products.MILK_PRODUCT,
				(double) randomInt(MarketConstants.MILKMAN_MIN_MILK_PRODUCT, MarketConstants.MILKMAN_MAX_MILK_PRODUCT));

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
	protected void decideAboutSellOffer(ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void decideAboutBuyOffer(ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		
	}

}
