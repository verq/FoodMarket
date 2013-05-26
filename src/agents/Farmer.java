package agents;

import constants.Participants;
import constants.Products;
import constants.MarketConstants;

public class Farmer extends MarketFieldAgent {
	private double grainCost, grainAmount;

	@Override
	protected void fillInitialBuy() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void fillInitialHave() {
		numberOfFields = random(MarketConstants.FARMER_MIN_FIELD, MarketConstants.FARMER_MAX_FIELD);
		grainAmount = randomDouble(0.5, 1) * numberOfFields * MarketConstants.FARMER_MAX_GRAIN_NEEDED_PER_FIELD;
		System.out.println("Farmer got " + numberOfFields + " fields and " + grainAmount + " grain");
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
