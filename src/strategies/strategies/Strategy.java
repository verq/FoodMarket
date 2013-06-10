package strategies;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import constants.Participants;
import constants.Products;

import agents.AgentOffer;

public abstract class Strategy {
	/**
	 * @author beatka
	 *
	 */
	public class SimpleBuyingStrategy {

	}

	/**
	 * buying stage 1:
	 * 
	 * takes list of agents' offers, computes what I want to do and returns list
	 * of offers I want to make
	 * 
	 * @param offers
	 *            from buyers
	 * @return list of {@link AgentOffer} items containing buy offer details
	 */
	protected abstract ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers);

	/**
	 * buying stage 2:
	 * 
	 * decide wether to agree for the offer or not  
	 * 
	 * @param sellOffers
	 * @return map: to whose offer I respond and what is my answer
	 *         (accept/reject: true/false)
	 */
	public abstract Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers);

	/**
	 * buying stage 3:
	 * 
	 * update supplies after transaction
	 * 
	 * buyer: update my supplies after positive transaction with seller
	 * traderName
	 * 
	 * @param traderName
	 */
	public abstract void updateBuyerStore(String traderName);
	
	/**
	 * selling stage 1:
	 * 
	 * takes list of agents' offers, computes what I want to do and returns list
	 * of offers I want to make
	 * 
	 * @param offers
	 *            from sellers
	 * @return list of {@link AgentOffer} items containing sell offer details
	 */
	protected abstract ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers);

	/**
	 * selling stage 2:
	 * 
	 * invoked at the very end of transaction to confirm it; don't forget to
	 * update the store here!
	 * 
	 * @param traderName
	 * @return return true if I can complete transaction with this buyer,
	 *         otherwise return false
	 */
	public abstract boolean confirmSellTransactionWith(String traderName);

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
	 * whom from can I buy {@link Products}
	 * name of the agent : offers from every week
	 */
	protected Map<String, ArrayList<AgentOffer>> buyOffersHistory;
	/**
	 * to whom I sell {@link Products}
	 */
	protected Map<String, ArrayList<AgentOffer>> sellOffersHistory;
	
	protected double myMoney;
}
