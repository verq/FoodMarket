package agents;

import constants.Participants;
import constants.Products;

public class Keeper extends MarketFieldAgent {
	private int numberOfAnimals;

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
		sellTo.put(Participants.CLIENT, Products.MEAT);
		sellTo.put(Participants.MILKMAN, Products.MILK);
		sellTo.put(Participants.GROWER, Products.MANURE);
		sellTo.put(Participants.FARMER, Products.MANURE);
	}

}
