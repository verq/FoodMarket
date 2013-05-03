package agents;

import constants.Participants;
import constants.Products;

public class Farmer extends MarketFieldAgent {
	private int grainCost;

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
		buyFrom.put(Participants.KEEPER, Products.MANURE);
	}

	@Override
	protected void fillSellTo() {
		sellTo.put(Participants.KEEPER, Products.GRAIN);
		sellTo.put(Participants.BAKER, Products.GRAIN);
		sellTo.put(Participants.CLIENT, Products.VEGETABLE);
	}
}
