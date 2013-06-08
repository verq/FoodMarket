package agents;

import java.util.ArrayList;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;
import utilities.*;

public class Baker extends MarketEmployeeAgent {	
	@Override
	protected void fillInitialBuy() {
		buy.put(Products.GRAIN, numberOfEmployees
				* MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.BAKER;
		numberOfEmployees = utilities.randomInt(MarketConstants.BAKER_MIN_EMPLOYEE,
				MarketConstants.BAKER_MAX_EMPLOYEE);
		have.put(Products.GRAIN, utilities.randomDouble(0.5, 1) * numberOfEmployees
				* MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
		have.put(
				Products.BREAD,
				(double) utilities.randomInt(MarketConstants.BAKER_MIN_BREAD_PRODUCT,
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
	protected void decideAboutSellOffer(ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void decideAboutBuyOffer(ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		
	}

}
