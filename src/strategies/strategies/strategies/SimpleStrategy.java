package strategies.strategies;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import constants.OfferFormatUtilities;
import constants.Products;

import agents.AgentOffer;

/**
 * agent using this strategy will agree for the cheapest sell offer he gets for every
 * available {@link Products} he needs to buy
 * 
 * when total price for everything is too high he reduces amount of each answer by
 * 1/number_of_transactions
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
		EnumMap<Products, Double> lowestPriceAmount = new EnumMap<Products, Double>(Products.class);
		EnumMap<Products, String> cheapestAgents = new EnumMap<Products, String>(Products.class);
		Iterator<Products> buyIterator = buy.keySet().iterator();
		while(buyIterator.hasNext()) {
			Products prod = buyIterator.next();
			lowestPrices.put(prod, Double.MAX_VALUE);
			lowestPriceAmount.put(prod, 0.0);
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
						lowestPriceAmount.put(prod, Math.min(agentOffer
								.getItemAmount().get(prod), buy.get(prod)));
					}
				}
			}
		}
		double nonEmptyOffers = 0.0;
		for (Products product : cheapestAgents.keySet()) {
			if(!cheapestAgents.get(product).equals("")) nonEmptyOffers += 1.0;
		}
		double amountToPay = 0.0;
		// checking if we can afford to buy that much
		for (Products product : cheapestAgents.keySet()) {
			//System.out.println(lowestPrices.get(product) + " " + lowestPriceAmount.get(product));
			amountToPay += lowestPrices.get(product) * lowestPriceAmount.get(product);
		}
		double decreaseMoneyToSpend =  (amountToPay > myMoney) ? (amountToPay - myMoney)/nonEmptyOffers : 0.0;
		for (AgentOffer agentOffer : offers) {
			AgentOffer currentAnswer = new AgentOffer(
					agentOffer.getAgentName(), "");
			if(cheapestAgents.containsValue(agentOffer.getAgentName())){ // checking if this agent offered cheapest price somewhere
				Iterator<Products> iter = cheapestAgents.keySet().iterator();
				while(iter.hasNext()) {
					Products currProd = iter.next();
					if(cheapestAgents.get(currProd).equals(agentOffer.getAgentName())) { // adding products he offered lowest price for
						currentAnswer.addItemPrice(currProd.name(), lowestPrices.get(currProd));
						double newAmount = lowestPriceAmount.get(currProd) > 0 ? lowestPriceAmount.get(currProd) - 
								decreaseMoneyToSpend/ lowestPrices.get(currProd) : 0;
						/*
								System.out.println("amountToPay: " + amountToPay
								+ "dec: " + decreaseMoneyToSpend
								+ " curr amount: "
								+ lowestPriceAmount.get(currProd)
								+ " new amount: " + newAmount);
						*/
						currentAnswer.addItemAmount(currProd.name(), newAmount);
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
						//System.out.println(ans.getItemAmount().get(product).doubleValue() + " " +agentOffer
						//				.getItemAmount().get(product).doubleValue()
						//		+ " " + ans.getItemPrice().get(product).doubleValue() + " " + agentOffer
						//				.getItemPrice().get(product).doubleValue());
						if (ans.getItemAmount().get(product).doubleValue() <= agentOffer
										.getItemAmount().get(product).doubleValue()
								&& ans.getItemPrice().get(product).doubleValue() >= agentOffer
										.getItemPrice().get(product).doubleValue()) {
							ans.getItemAmount().put(product, agentOffer
										.getItemAmount().get(product).doubleValue());
							ans.getItemPrice().put(product, agentOffer
										.getItemPrice().get(product).doubleValue());
							//System.out.println("after update: " + ans.getItemAmount().get(product).doubleValue() + " " +agentOffer
							//		.getItemAmount().get(product).doubleValue()
							//+ " " + ans.getItemPrice().get(product).doubleValue() + " " + agentOffer
							//		.getItemPrice().get(product).doubleValue());
						} // if it's worse from the previous one - resign
						// TODO: update history if offer changes!!
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
		ArrayList<AgentOffer> transactions = currentWeekBuyOffersHistory.get(traderName);
		for (Iterator<AgentOffer> iterator = transactions.iterator(); iterator.hasNext();) {
			AgentOffer agentOffer = (AgentOffer) iterator.next();
			double moneyPaid = 0.0;
			Map<Products, Double> prices = agentOffer.getItemPrice();
			for (Iterator<Products> prodIter = prices.keySet().iterator(); prodIter.hasNext();) {
				Products currProd = prodIter.next();
				moneyPaid += prices.get(currProd) * agentOffer.getItemAmount().get(currProd);
				//System.out.println("--> " + buy.get(currProd) + "-" + agentOffer.getItemAmount().get(currProd)
				//		+"=" + (buy.get(currProd) - agentOffer.getItemAmount().get(currProd)));
				
				buy.put(currProd, buy.get(currProd) - agentOffer.getItemAmount().get(currProd));
				have.put(currProd, have.get(currProd) + agentOffer.getItemAmount().get(currProd));
			}
			myMoney -= moneyPaid;
		}
		
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