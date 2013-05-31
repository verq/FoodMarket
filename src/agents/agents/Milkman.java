package agents;

import constants.Participants;
import constants.Products;

public class Milkman extends MarketEmployeeAgent {
	private int milkCost;

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
		buyFrom.put(Participants.KEEPER, Products.MILK);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.CLIENT, Products.MILK_PRODUCT);
	}

}
