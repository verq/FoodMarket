package strategies.strategies;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

import agents.AgentOffer;
import constants.Products;

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

	@Override
	public ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers) {
		ArrayList<AgentOffer> answer = new ArrayList<AgentOffer>();
		Iterator<Products> sellIterator = sell.keySet().iterator();
		while (sellIterator.hasNext()) {
			Products product = sellIterator.next();
			Iterator<AgentOffer> offersIterator = offers.iterator();
			while (offersIterator.hasNext()) {
				AgentOffer offer = offersIterator.next();
				AgentOffer currentAnswer = new AgentOffer(offer.getAgentName(),
						"");
				if (offer.getItemPrice().get(product) >= 0.9 * pricePerItem
						.get(product)) {
					currentAnswer.addItemAmount(product, sell.get(product));
					currentAnswer.addItemPrice(product, offer.getItemPrice()
							.get(product));
				}
				answer.add(currentAnswer);
			}

		}
		return answer;
	}

}
