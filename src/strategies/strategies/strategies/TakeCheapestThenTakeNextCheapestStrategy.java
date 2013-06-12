package strategies.strategies;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import agents.AgentOffer;
import constants.Products;

public class TakeCheapestThenTakeNextCheapestStrategy extends Strategy {

	@Override
	public ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers) {
		ArrayList<AgentOffer> answer = new ArrayList<AgentOffer>();
		Map<String, AgentOffer> offersMap = new HashMap<String, AgentOffer>();
		Map<String, AgentOffer> myAnswers = new HashMap<String, AgentOffer>();

		for (AgentOffer agentOffer : offers) {
			offersMap.put(agentOffer.getAgentName(), agentOffer);
			myAnswers.put(agentOffer.getAgentName(),
					new AgentOffer(agentOffer.getAgentName(), null));
		}
		double totalPriceToPay = 0.0, nonemptyOffersNum = 0.0;
		for (Iterator<Products> buyProdIterator = buy.keySet().iterator(); buyProdIterator
				.hasNext();) {
			Products currentBuyProduct = buyProdIterator.next();
			TreeMap<String, Double> sortedProductOffers = returnSortedOffersForSingleProduct(
					offers, currentBuyProduct);

			double needToBuy = buy.get(currentBuyProduct);
			for (Iterator<Entry<String, Double>> iterator = sortedProductOffers
					.entrySet().iterator(); iterator.hasNext();) {
				Entry<String, Double> entry = iterator.next();
				if (needToBuy <= 0.0 || totalPriceToPay >= myMoney)
					break;
				String cheapestNowName = entry.getKey();
				double cheapestNowPrice = entry.getValue();
				AgentOffer cheapestNow = offersMap.get(cheapestNowName);
				AgentOffer currentAnswer = myAnswers.get(cheapestNowName);
				/*
				 * System.out.println(currentBuyProduct.name() + "  " +
				 * cheapestNowPrice + " " +
				 * sortedProductOffers.containsKey(cheapestNowName) + " " +
				 * cheapestNowName + " " + sortedProductOffers);
				 */
				currentAnswer.addItemPrice(currentBuyProduct.name(),
						cheapestNowPrice);
				// System.out.println(cheapestNow.getItemAmount());
				double hisAmount = cheapestNow.getItemAmount().get(
						currentBuyProduct);
				double amountToBuy = Math.min(hisAmount, needToBuy);
				if (amountToBuy * cheapestNowPrice + totalPriceToPay > myMoney) {
					amountToBuy = (myMoney - totalPriceToPay)
							/ cheapestNowPrice;
				}
				/*
				 * double newAmount = agentOffer.getItemPrice().get(currProd) >
				 * 0 ? itemAmount .get(currProd) - decreaseMoneyToSpend /
				 * agentOffer.getItemPrice().get(currProd) : 0;
				 */
				currentAnswer.addItemAmount(currentBuyProduct.name(),
						amountToBuy);
				totalPriceToPay += amountToBuy * cheapestNowPrice;
				nonemptyOffersNum += 1;
				/*
				 * System.out.println("buying " + amountToBuy + " of " +
				 * currentBuyProduct + " for " + cheapestNowPrice + " from " +
				 * cheapestNowName);
				 */
				needToBuy -= Math.min(hisAmount, needToBuy);
			}
		}
		double decreaseMoneyToSpend = (totalPriceToPay > myMoney) ? (totalPriceToPay - myMoney)
				: 0.0;

		/*
		 * System.out.println("price to pay: " + totalPriceToPay +
		 * " num of offers: " + nonemptyOffersNum +
		 * " decreasing each offer by: " + decreaseMoneyToSpend);
		 */
		answer = new ArrayList<AgentOffer>(myAnswers.values());
		saveOffersToHistory(myAnswers, decreaseMoneyToSpend);
		/*
		 * for (AgentOffer agentOffer : answer) {
		 * System.out.println(agentOffer.getAgentName() + ": " +
		 * agentOffer.getItemAmount() + " " + agentOffer.getItemPrice()); }
		 */
		return answer;
	}

	private void saveOffersToHistory(Map<String, AgentOffer> myAnswers,
			double decreaseMoneyToSpend) {
		for (AgentOffer agentOffer : myAnswers.values()) {
			if (!currentWeekBuyOffersHistory.containsKey(agentOffer
					.getAgentName())) {
				currentWeekBuyOffersHistory.put(agentOffer.getAgentName(),
						new ArrayList<AgentOffer>());
			}
			currentWeekBuyOffersHistory.get(agentOffer.getAgentName()).add(
					agentOffer);
		}
	}

	private TreeMap<String, Double> returnSortedOffersForSingleProduct(
			ArrayList<AgentOffer> offers, Products currentBuyProduct) {
		HashMap<String, Double> productOffers = new HashMap<String, Double>();
		ValueComparator bvc = new ValueComparator(productOffers);
		TreeMap<String, Double> sortedProductOffers = new TreeMap<String, Double>(
				bvc);
		for (AgentOffer singleOffer : offers) {
			for (Iterator<Products> offerProductsIterator = singleOffer
					.getItemPrice().keySet().iterator(); offerProductsIterator
					.hasNext();) {
				Products currentOfferProduct = offerProductsIterator.next();
				if (currentOfferProduct.equals(currentBuyProduct)) {
					productOffers.put(singleOffer.getAgentName(), singleOffer
							.getItemPrice().get(currentOfferProduct));
				}
			}
		}
		sortedProductOffers.putAll(productOffers);
		return sortedProductOffers;
	}

	private class ValueComparator implements Comparator<String> {

		Map<String, Double> base;

		public ValueComparator(Map<String, Double> base) {
			this.base = base;
		}

		// Note: this comparator imposes orderings that are inconsistent with
		// equals.
		public int compare(String a, String b) {
			if (base.get(a) <= base.get(b)) {
				return -1;
			} else {
				return 1;
			} // returning 0 would merge keys
		}
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
				if (agentOffer.getItemAmount().size() == 0
						&& myAnswers.size() != 0) {
					agreeForThisOffer = false;

					for (AgentOffer ans : myAnswers) {
						for (Products product : ans.getItemAmount().keySet()) {
							ans.getItemAmount().put(product, 0.0);
							ans.getItemPrice().put(product, 0.0);
						}
					}
				}
				for (Products product : agentOffer.getItemAmount().keySet()) {

					// check every position in this offer
					for (AgentOffer ans : myAnswers) {
						/*
						 * System.out.println(agentOffer.getAgentName() + ": " +
						 * myAnswers.size() + ", " +
						 * agentOffer.getItemAmount().size() + " " +
						 * ans.getItemAmount() + "   " + ans.getItemPrice());
						 */
						if (!ans.getItemAmount().containsKey(product))
							continue;
						/*
						 * System.out.println(ans.getItemAmount().get(product)
						 * .doubleValue() + " " +
						 * agentOffer.getItemAmount().get(product)
						 * .doubleValue() + " " +
						 * ans.getItemPrice().get(product).doubleValue() + " " +
						 * agentOffer.getItemPrice().get(product)
						 * .doubleValue());
						 */
						double additionalProfit = agentOffer.getItemAmount().get(product).doubleValue() - ans.getItemAmount().get(product).doubleValue();
						if (additionalProfit <= 1.0 && additionalProfit >= 0 
								&& ans.getItemPrice().get(product).doubleValue() >= agentOffer
										.getItemPrice().get(product).doubleValue()) {

							ans.getItemAmount().put(
									product,
									agentOffer.getItemAmount().get(product)
											.doubleValue());
							ans.getItemPrice().put(
									product,
									agentOffer.getItemPrice().get(product)
											.doubleValue());
						} else { // if it's worse from the previous one - resign
							agreeForThisOffer = false;
							ans.getItemAmount().put(product, 0.0);
							ans.getItemPrice().put(product, 0.0);
							break;
						}
					}
				}
				decisions.put(agentOffer.getAgentName(), agreeForThisOffer);

			} else
				decisions.put(agentOffer.getAgentName(), false);
		}
		return decisions;
	}

	@Override
	public void updateBuyerStore(String traderName) {
		ArrayList<AgentOffer> transactions = currentWeekBuyOffersHistory
				.get(traderName);
		for (Iterator<AgentOffer> iterator = transactions.iterator(); iterator
				.hasNext();) {
			AgentOffer agentOffer = (AgentOffer) iterator.next();
			double moneyPaid = 0.0;
			Map<Products, Double> prices = agentOffer.getItemPrice();
			for (Iterator<Products> prodIter = prices.keySet().iterator(); prodIter
					.hasNext();) {
				Products currProd = prodIter.next();
				moneyPaid += prices.get(currProd)
						* agentOffer.getItemAmount().get(currProd);
				/*
				 * System.out.println("updating transacion with: " + traderName
				 * + ": money paid =" + moneyPaid + "price = " +
				 * prices.get(currProd) + " amount = " +
				 * agentOffer.getItemAmount().get(currProd));
				 */
				// System.out.println("--> " + buy.get(currProd) + "-" +
				// agentOffer.getItemAmount().get(currProd)
				// +"=" + (buy.get(currProd) -
				// agentOffer.getItemAmount().get(currProd)));

				buy.put(currProd, buy.get(currProd)
						- agentOffer.getItemAmount().get(currProd));
				have.put(currProd, have.get(currProd)
						+ agentOffer.getItemAmount().get(currProd));
			}
			myMoney -= moneyPaid;
		}
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
				if (offer.getItemPrice().get(product) >= pricePerItem
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
