package agents;

import java.util.ArrayList;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public class Keeper extends MarketFieldAgent {
	@Override
	protected void fillInitialBuy() {
		buy.put(Products.GRAIN, numberOfFields * MarketConstants.KEEPER_GRAIN_NEEDED_FOR_ANIMAL);
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.KEEPER;
		numberOfFields = randomInt(MarketConstants.KEEPER_MIN_ANIMAL, MarketConstants.KEEPER_MAX_ANIMAL);
		have.put(Products.GRAIN, randomDouble(0.5, 1) * numberOfFields * MarketConstants.KEEPER_GRAIN_NEEDED_FOR_ANIMAL);
		have.put(Products.MANURE, (double) randomInt(MarketConstants.KEEPER_MIN_MANURE, MarketConstants.KEEPER_MAX_MANURE));
		have.put(Products.MEAT, (double) randomInt(MarketConstants.KEEPER_MIN_MEAT, MarketConstants.KEEPER_MAX_MEAT));
		have.put(Products.MILK, (double) randomInt(MarketConstants.KEEPER_MIN_MILK, MarketConstants.KEEPER_MAX_MILK));

	}

	@Override
	protected void fillInitialSell() {
		sell.put(Products.MEAT, numberOfFields * MarketConstants.KEEPER_TRESHOLD_KILLED_ANIMALS
				* MarketConstants.KEEPER_MEET_PER_ANIMAL); // he can't sell all
		pricePerItem.put(Products.MEAT, MarketConstants.KEEPER_MEAT_COST);
		sell.put(Products.MILK, have.get(Products.MILK));
		pricePerItem.put(Products.MILK, MarketConstants.KEEPER_MILK_COST);
		sell.put(Products.MANURE, have.get(Products.MANURE));
		pricePerItem.put(Products.MANURE, MarketConstants.KEEPER_MANURE_COST);

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
