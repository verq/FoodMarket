package agents;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Baker extends MarketEmployeeAgent {
	private int breadCost;

	@Override
	protected void fillInitialBuy() {
	}

	@Override
	protected void fillInitialHave() {
		numberOfEmployees = random(MarketConstants.BAKER_MIN_EMPLOYEE, MarketConstants.BAKER_MAX_EMPLOYEE);
		productivity = MarketConstants.BAKER_EMPLOYEE_PRODUCTIVITY;
		have.put(Products.GRAIN, random(MarketConstants.BAKER_MIN_BREAD, MarketConstants.BAKER_MAX_BREAD));
		have.put(Products.GRAIN, random(MarketConstants.BAKER_MIN_GRAIN, MarketConstants.BAKER_MAX_GRAIN));
	}

	@Override
	protected void fillInitialSell() {
		breadCost = MarketConstants.BAKER_BREAD_COST;
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
