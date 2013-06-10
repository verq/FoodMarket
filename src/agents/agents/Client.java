package agents;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;
import utilities.*;

public class Client extends MarketAgent {
	private double income;
	private EnumMap<Products, Integer> weekProductNeeds;

	@Override
	protected void fillInitialBuy() {
		for (Products p : Products.values()) {
			if (MarketConstants.CLIENT_NEEDS_MAX.containsKey(p) && MarketConstants.CLIENT_NEEDS_MIN.containsKey(p)) {
				double productNeed = AgentsUtilities.randomDouble(MarketConstants.CLIENT_NEEDS_MIN.get(p),
						MarketConstants.CLIENT_NEEDS_MAX.get(p));
				if (productNeed != 0) {
					buy.put(p, productNeed);
				}
			}
		}
	}

	@Override
	protected void fillInitialHave() {
		myType = Participants.CLIENT;
		income = AgentsUtilities.randomDouble(MarketConstants.CLIENT_MIN_INCOME, MarketConstants.CLIENT_MAX_INCOME);
		money = income;
	}

	@Override
	protected void fillInitialSell() {
		// nothing
	}

	@Override
	protected void fillBuyFrom() {
		buyFrom.put(Participants.BAKER, Products.BREAD);
		buyFrom.put(Participants.FARMER, Products.VEGETABLE);
		buyFrom.put(Participants.KEEPER, Products.MEAT);
		buyFrom.put(Participants.GROWER, Products.FRUIT);
		buyFrom.put(Participants.MILKMAN, Products.MILK_PRODUCT);
	}

	@Override
	protected void fillSellTo() {
		// nothing
	}

	@Override
	protected void updateResources() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<AgentOffer> decideAboutSellOffer(ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub
		
	}

	@Override
	protected ArrayList<AgentOffer> decideAboutBuyOffer(ArrayList<AgentOffer> offers) {
		return offers;
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean confirmSellTransactionWith(String traderName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers) {
		// TODO Auto-generated method stub
		return null;
	}
}
