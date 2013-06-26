package strategies.strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import agents.AgentOffer;
import constants.Products;

@Deprecated
public class TakeCheapestThenOfferLowestOffer extends Strategy {

	@Override
	public ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		// TODO Auto-generated method stub

	}

	protected boolean getSellingCondition(double buyItemPrice,
			double sellItemPrice) {
		return buyItemPrice >= 0.9 * sellItemPrice;
	}

}
