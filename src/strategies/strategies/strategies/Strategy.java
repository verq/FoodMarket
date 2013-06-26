package strategies.strategies;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilities.AgentsUtilities;
import agents.AgentOffer;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public abstract class Strategy {
	/**
	 * buying stage 1:
	 * 
	 * takes list of agents' offers, computes what I want to do and returns list of offers I want to make
	 * 
	 * used by BUYER after getting first offer from SELLER
	 * 
	 * @param offers
	 *            from sellers
	 * @return list of {@link AgentOffer} items containing sell offer details
	 */
	public abstract ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers);

	/**
	 * buying stage 2:
	 * 
	 * decide whether to agree for the offer or not
	 * 
	 * used by BUYER after getting final decision from SELLER
	 * 
	 * @param sellOffers
	 * @return map: to whose offer I respond and what is my answer (accept/reject: true/false)
	 */
	public abstract Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers);

	/**
	 * buying stage 3:
	 * 
	 * update supplies after transaction
	 * 
	 * used by BUYER after getting confirmation from SELLER
	 * 
	 * buyer: update my supplies after positive transaction with seller traderName
	 * 
	 * @param traderName
	 */
	public abstract void updateBuyerStore(String traderName);

	/**
	 * selling stage 1:
	 * 
	 * takes list of buying agents' offers, computes what I want to do and returns list of offers I want to make
	 * 
	 * used by SELLER to decide about BUYER offer
	 * 
	 * @param offers
	 *            from buyers
	 * @return list of {@link AgentOffer} items containing buy offer details
	 */
	public ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers) {
		informAboutNeeds();
		ArrayList<AgentOffer> answer = new ArrayList<AgentOffer>();
		Iterator<Products> sellIterator = sell.keySet().iterator();
		Iterator<AgentOffer> offersIterator = offers.iterator();
		while (offersIterator.hasNext()) {
			AgentOffer offer = offersIterator.next();
			AgentOffer currentAnswer = new AgentOffer(offer.getAgentName(), "");
			while (sellIterator.hasNext()) {
				Products product = sellIterator.next();
				currentAnswer.setAgentType(myType);
				currentAnswer.setOfferType(OfferFormatUtilities.SELL_OFFER_TAG);
				if (pricePerItem.containsKey(product)
						&& offer.getItemPrice().containsKey(product)
						&& getSellingCondition(offer.getItemPrice()
								.get(product), pricePerItem.get(product))) {
					currentAnswer.addItemAmount(product, offer.getItemAmount()
							.get(product));
					currentAnswer.addItemPrice(product, offer.getItemPrice()
							.get(product));
				}
			}
		answer.add(currentAnswer);
		}
		return answer;
	}

	protected abstract boolean getSellingCondition(double buyerItemPrice,
			double sellerItemPrice);

	/**
	 * selling stage 2:
	 * 
	 * invoked at the very end of transaction to confirm it; don't forget to update the store here!
	 * 
	 * used by SELLER to confirm transaction with BUYER
	 * 
	 * @param buyerOffer
	 * @return return true if I can complete transaction with this buyer, otherwise return false
	 */
	public boolean confirmSellTransactionWith(AgentOffer buyerOffer, boolean accepted) {
		if(!accepted) return false;
		// check if you can complete the offer:
		for (Products product : buyerOffer.getItemAmount().keySet()) {
			if (sell.get(product) < buyerOffer.getItemAmount().get(product)) {
				return false;
			}
		}
		// update store:
		for (Products product : buyerOffer.getItemAmount().keySet()) {
			sell.put(product, sell.get(product)
					- buyerOffer.getItemAmount().get(product));
			// TODO: nie działa dla bakerów i milkmanów - whatever
			have.put(product, have.get(product)
					- buyerOffer.getItemAmount().get(product));
			myMoney -= buyerOffer.getItemPrice().get(product);
		}
		return true;
	}

	public Strategy() {
		buy = new EnumMap<Products, Double>(Products.class);
		have = new EnumMap<Products, Double>(Products.class);
		sell = new EnumMap<Products, Double>(Products.class);
		pricePerItem = new EnumMap<Products, Double>(Products.class);
		currentWeekBuyOffersHistory = new HashMap<String, ArrayList<AgentOffer>>();
		currentWeekSellOffersHistory = new HashMap<String, ArrayList<AgentOffer>>();
		myMoney = 0.0;
		myType = Participants.MILKMAN;
	}

	protected void informAboutNeeds() {
		if(AgentsUtilities.INFORM_ABOUT_AGENT_NEEDS) 
			System.out.println(myType + " has " + myMoney + " money;\nneeds to buy: " + buy + "\nsell:" + sell + "\nhas: " + have);
	}
	public EnumMap<Products, Double> getBuy() {
		return buy;
	}

	public void setBuy(EnumMap<Products, Double> buy) {
		this.buy = buy;
	}

	public EnumMap<Products, Double> getHave() {
		return have;
	}

	public void setHave(EnumMap<Products, Double> have2) {
		this.have = have2;
	}

	public EnumMap<Products, Double> getSell() {
		return sell;
	}

	public void setSell(EnumMap<Products, Double> sell) {
		this.sell = sell;
	}

	public EnumMap<Products, Double> getPricePerItem() {
		return pricePerItem;
	}

	public void setPricePerItem(EnumMap<Products, Double> pricePerItem) {
		this.pricePerItem = pricePerItem;
	}

	public Map<String, ArrayList<AgentOffer>> getCurrentWeekBuyOffersHistory() {
		return currentWeekBuyOffersHistory;
	}

	public void setCurrentWeekBuyOffersHistory(
			Map<String, ArrayList<AgentOffer>> buyOffersHistory) {
		this.currentWeekBuyOffersHistory = buyOffersHistory;
	}

	public Map<String, ArrayList<AgentOffer>> getCurrentWeekSellOffersHistory() {
		return currentWeekSellOffersHistory;
	}

	public void setCurrentWeekSellOffersHistory(
			Map<String, ArrayList<AgentOffer>> sellOffersHistory) {
		this.currentWeekSellOffersHistory = sellOffersHistory;
	}

	public double getMyMoney() {
		return myMoney;
	}
	
	public Participants getMyType() {
		return myType;
	}

	public void setMyType(Participants myType) {
		this.myType = myType;
	}
	public void setMyMoney(double myMoney) {
		this.myMoney = myMoney;
	}

	protected EnumMap<Products, Double> buy;
	/**
	 * how much of everything I have
	 */
	protected EnumMap<Products, Double> have;
	/**
	 * how much of everything I can to sell
	 */
	protected EnumMap<Products, Double> sell;
	/**
	 * what is this week's the price per item
	 */
	protected EnumMap<Products, Double> pricePerItem;
	/**
	 * whom from I bought {@link Products} this week name of the agent : answers to offers from current week
	 */
	protected Map<String, ArrayList<AgentOffer>> currentWeekBuyOffersHistory;
	/**
	 * to whom I sell {@link Products} this week
	 */
	protected Map<String, ArrayList<AgentOffer>> currentWeekSellOffersHistory;

	/**
	 * whom from can I buy {@link Products} name of the agent : offers from every week
	 */
	protected Map<String, ArrayList<AgentOffer>> suyOffersHistory;
	/**
	 * to whom I sell {@link Products} - all time offers
	 */
	protected Map<String, ArrayList<AgentOffer>> sellOffersHistory;
	protected double myMoney;
	protected Participants myType;
}
