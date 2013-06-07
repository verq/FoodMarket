package agents;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Baker extends MarketEmployeeAgent {

	@Override
	protected void fillInitialBuy() {
		buy.put(Products.GRAIN, numberOfEmployees * MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.BAKER;
		numberOfEmployees = random(MarketConstants.BAKER_MIN_EMPLOYEE, MarketConstants.BAKER_MAX_EMPLOYEE);
		have.put(Products.GRAIN, randomDouble(0.5, 1) * numberOfEmployees * MarketConstants.BAKER_PRODUCTIVITY_CONSTANT);
		have.put(Products.BREAD,
				(double) random(MarketConstants.BAKER_MIN_BREAD_PRODUCT, MarketConstants.BAKER_MAX_BREAD_PRODUCT));

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

}
