package agents;

import constants.Participants;
import constants.Products;

public class Baker extends MarketEmployeeAgent {
	private int breadCost;

	@Override
	protected void fillInitialBuy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fillInitialHave() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fillInitialSell() {
		// TODO Auto-generated method stub

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
