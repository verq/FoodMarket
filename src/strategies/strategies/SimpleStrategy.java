package strategies;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import constants.OfferFormatUtilities;
import constants.Products;

import agents.AgentOffer;

/**
 * agent using this strategy will agree for the cheapest sell offer he gets
 * @author beatka
 *
 */
public class SimpleStrategy extends Strategy {

	@Override
	public ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers) {
		ArrayList<AgentOffer> answer = new ArrayList<AgentOffer>();
		// needed to choose best offers:
		EnumMap<Products, Double> lowestPrices = new EnumMap<Products, Double>(Products.class);
		EnumMap<Products, String> cheapestAgents = new EnumMap<Products, String>(Products.class);
		Iterator<Products> buyIterator = buy.keySet().iterator();
		while(buyIterator.hasNext()) {
			Products prod = buyIterator.next();
			lowestPrices.put(prod, Double.MAX_VALUE);
			cheapestAgents.put(prod, "");
		}
		for (AgentOffer agentOffer : offers) { // checking every offer
			buyIterator = buy.keySet().iterator();
			if (agentOffer.getOfferType() != null
					&& !agentOffer.getOfferType().equals(
							OfferFormatUtilities.SELL_OFFER_TAG))
				continue;
			while (buyIterator.hasNext()) {
				Products prod = buyIterator.next();
				// choosing the best offer:
				if (agentOffer.getItemAmount().containsKey(prod)) {
					if (lowestPrices.get(prod) > agentOffer.getItemPrice().get(
							prod)) {
						lowestPrices.put(prod,
								agentOffer.getItemPrice().get(prod));
						cheapestAgents.put(prod, agentOffer.getAgentName());
					}
				}
			}
		}
		for (AgentOffer agentOffer : offers) {
			AgentOffer currentAnswer = new AgentOffer(
					agentOffer.getAgentName(), "");
			if(cheapestAgents.containsValue(agentOffer.getAgentName())){ // checking if this agent offered cheapest price somewhere
				Iterator<Products> iter = cheapestAgents.keySet().iterator();
				while(iter.hasNext()) {
					Products currProd = iter.next();
					if(cheapestAgents.get(currProd).equals(agentOffer.getAgentName())) { // adding products he offered lowest price for
						currentAnswer.addItemPrice(currProd.name(), lowestPrices.get(currProd));
						currentAnswer.addItemAmount(currProd.name(), Math.min(agentOffer.getItemAmount().get(currProd), buy.get(currProd)));
					}
				}
			}
			 // remember our answer:
			if(!currentWeekBuyOffersHistory.containsKey(agentOffer.getAgentName())){
				currentWeekBuyOffersHistory.put(agentOffer.getAgentName(), new ArrayList<AgentOffer>());
			}
			ArrayList<AgentOffer> ao = currentWeekBuyOffersHistory.get(agentOffer.getAgentName());
			ao.add(currentAnswer);
			currentWeekBuyOffersHistory.put(agentOffer.getAgentName(), ao);
			answer.add(currentAnswer);
		}
		return answer;
	}

	@Override
	public Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers) {
		Map<String, Boolean> decisions = new HashMap<String, Boolean>();
		for (AgentOffer agentOffer : sellOffers) {
			boolean agreeForThisOffer = true;
			if (currentWeekBuyOffersHistory.containsKey(agentOffer
					.getAgentName())) {
				ArrayList<AgentOffer> myAnswers = currentWeekBuyOffersHistory
						.get(agentOffer.getAgentName()); // list of offers
															// completed this
															// week with this
															// agent
				for (Products product : agentOffer.getItemAmount().keySet()) { // check every position in this offer
					for (AgentOffer ans : myAnswers) {
						if (ans.getItemAmount().get(product).equals(agentOffer
										.getItemAmount().get(product))
								&& ans.getItemPrice().get(product).doubleValue() >= agentOffer
										.getItemPrice().get(product).doubleValue()) {} // if it's different from the previous one - resign
						// TODO: when price stays the same but the amount increases
						else {
							agreeForThisOffer = false;
							break;
						}
					}
				}
				decisions.put(agentOffer.getAgentName(), agreeForThisOffer);

			}
			else decisions.put(agentOffer.getAgentName(), false);
		}
		return decisions;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean confirmSellTransactionWith(String traderName) {
		// TODO Auto-generated method stub
		return false;
	}
}
