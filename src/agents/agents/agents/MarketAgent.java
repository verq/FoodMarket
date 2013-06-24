package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import strategies.strategies.SimpleStrategy;
import strategies.strategies.Strategy;
import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * invoked to maintain buying part of the agent
	 */
	private void buyAction() {
		if (buyFrom.isEmpty()) {
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": not buying from anyone!");
			return;
		}
		addBehaviour(new BuyTickerBehaviour(this, this, MarketConstants.WEEK));
	}

	/**
	 * invoked to maintain selling part of the agent
	 */
	private void sellAction() {
		if (sellTo.isEmpty()) {
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": not selling to anyone!");
			return;
		}
		addBehaviour(new SellTickerBehaviour(this, this, MarketConstants.WEEK));
	}

	/************ beginning of strategy related methods ************/

	/**
	 * takes list of agents' offers, computes what I want to do and returns list
	 * of offers I want to make
	 * 
	 * @param offers
	 *            from buyers
	 * @return list of {@link AgentOffer} items containing buy offer details
	 */
	protected ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers) {
		return myStrategy.decideAboutBuyOffer(offers);
	}

	/**
	 * 
	 * @param sellOffers
	 * @return map: to whose offer I respond and what is my answer
	 *         (accept/reject: true/false)
	 */
	public Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers) {
		return myStrategy.composeFinalBuyingDecision(sellOffers);
	}

	/**
	 * takes list of agents' offers, computes what I want to do and returns list
	 * of offers I want to make
	 * 
	 * @param offers
	 *            from sellers
	 * @return list of {@link AgentOffer} items containing sell offer details
	 */
	protected ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers) {
		return myStrategy.decideAboutSellOffer(offers);
	}

	/**
	 * invoked at the very end of transaction to confirm it; don't forget to
	 * update my store here!
	 * 
	 * @param traderName
	 * @return return true if I can complete transaction with this buyer,
	 *         otherwise return false
	 */
	public boolean confirmSellTransactionWith(String traderName, String offer, boolean accepted) {
		return myStrategy.confirmSellTransactionWith(new AgentOffer(traderName, offer), accepted);
	}

	/**
	 * buyer: update my supplies after positive transaction with seller
	 * traderName
	 * 
	 * @param traderName
	 */
	public void updateBuyerStore(String traderName) {
		myStrategy.updateBuyerStore(traderName);
	}

	/**
	 * used to update my supplies in weekly manner (eg. add some money)
	 */
	protected abstract void produceAndUse();

	/*************** end of strategy related methods ***************/

	/************ beginning of offers formatters ************/
	/**
	 * invoked by buyer after receiving all sellers' offers
	 * 
	 * @param buy
	 *            offers containing offerer's name and offer as String
	 * @return answers to buy offers containing recipient name and answer as
	 *         String
	 */
	Map<String, String> createAnswerToSellOffer(Map<String, String> offers) {
		return AgentsUtilities
				.createMapOfOffers(decideAboutSellOffer(AgentsUtilities
						.createListOfOffers(offers)));
	}

	/**
	 * invoked by seller after receiving all buyers' offers
	 * 
	 * @param buy
	 *            offers containing offerer's name and offer as String
	 * @return answers to buy offers containing recipient name and answer as
	 *         String
	 */
	Map<String, String> createAnswerToBuyOffer(Map<String, String> offers) {
		return AgentsUtilities
				.createMapOfOffers(decideAboutBuyOffer(AgentsUtilities
						.createListOfOffers(offers)));
	}

	Map<String, Boolean> createFinalBuyingDecision(Map<String, String> offers) {
		return composeFinalBuyingDecision(AgentsUtilities
				.createListOfOffers(offers));
	}

	/**
	 * format my sell offer content based on my current supplies
	 * 
	 * @return formated sell offer
	 */
	String composeSellContent() {
		return OfferFormatUtilities.composeOfferContent(sell, pricePerItem,
				myType, OfferFormatUtilities.SELL_OFFER_TAG);
	}

	@SuppressWarnings("unused")
	private String composeBuyContent() {
		return OfferFormatUtilities.composeOfferContent(buy, pricePerItem,
				myType, OfferFormatUtilities.BUY_OFFER_TAG);
	}

	/*************** end of offers formatters ***************/

	@Override
	protected void takeDown() {
		try {
			DFService.deregister(this);
		} catch (Exception e) {
		}
		System.out.println("Agent " + this.getClass().getName() + " "
				+ getAID().getName() + " is terminating.");
	}

	@Override
	protected void setup() {
		initializeMaps();
		initializeProducts();
		System.out.println("Agent " + this.getClass().getName() + " "
				+ getAID().getName() + " is starting");
		register();
		buyAction();
		sellAction();
		timeAction();
		myStrategy = new SimpleStrategy();
		myStrategy.setBuy(buy);
		myStrategy.setHave(have);
		myStrategy.setSell(sell);
		myStrategy.setPricePerItem(pricePerItem);
		myStrategy.setMyMoney(money);
		myStrategy.setMyType(myType);
	}

	/**
	 * register my both selling and buying services
	 */
	private void register() {
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		Set<Products> t = new HashSet<Products>(sellTo.values());
		registerSellServices(agentDescription, t);

		registerBuyServices(agentDescription);
		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException fe) {
			System.out.println(fe.getMessage());
		}
	}

	private void registerSellServices(DFAgentDescription agentDescription,
			Set<Products> t) {
		Iterator<Products> sellProductsIterator = t.iterator();
		while (sellProductsIterator.hasNext()) {
			String name = sellProductsIterator.next().toString();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType(name);
			serviceDescription.setName(OfferFormatUtilities.SELL_OFFER_TAG);
			agentDescription.addServices(serviceDescription);
			if (AgentsUtilities.DEBUG_ST_1)
				System.out
						.println(myType + ": I register for selling: " + name);
		}
	}

	private void registerBuyServices(DFAgentDescription agentDescription) {
		Set<Products> t2 = new HashSet<Products>(buyFrom.values());
		Iterator<Products> buyProductsIterator = t2.iterator();
		while (buyProductsIterator.hasNext()) {
			String name = buyProductsIterator.next().toString();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType(name);
			serviceDescription.setName(OfferFormatUtilities.BUY_OFFER_TAG);
			agentDescription.addServices(serviceDescription);
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": I register for buying: " + name);
		}
	}

	/* ============= just initialisation down there ============= */
	/**
	 * Set how much of everything I need to buy first week
	 */
	protected abstract void fillInitialBuy();

	/**
	 * Set how much of everything I have in the beginning
	 */
	protected abstract void fillInitialHave();

	/**
	 * Set how much of everything I want to sell first week
	 */
	protected abstract void fillInitialSell();

	/**
	 * Set whom I buy from
	 */
	protected abstract void fillBuyFrom();

	/**
	 * Set who I sell to
	 */
	protected abstract void fillSellTo();

	private void initializeMaps() {
		buy = new EnumMap<Products, Double>(Products.class);
		have = new EnumMap<Products, Double>(Products.class);
		sell = new EnumMap<Products, Double>(Products.class);
		pricePerItem = new EnumMap<Products, Double>(Products.class);
		buyFrom = new EnumMap<Participants, Products>(Participants.class);
		sellTo = new EnumMap<Participants, Products>(Participants.class);
		buyerAgentsList = new ArrayList<AID>();
		sellerAgentsList = new ArrayList<AID>();
	}

	protected void initializeProducts() {
		initializeAddProducts(buy);
		initializeAddProducts(have);
		initializeAddProducts(sell);
		initializeAddProducts(pricePerItem);
		fillInitialHave();
		fillInitialSell();
		fillInitialBuy();
		fillBuyFrom();
		fillSellTo();
	}

	/**
	 * invoked to maintain weekly resource updates
	 */
	private void timeAction() {
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			@Override
			protected void onTick() {
				produceAndUse();
				prepareForSelling();
				}
		});
	}

	protected void prepareForSelling() {
		if (!(this instanceof Client)) {
		for (Products product : sellTo.values()) {
				sell.put(product, have.get(product));
				have.put(product, 0D);
		}
		}
	}

	protected void initializeAddProducts(EnumMap<Products, Double> products) {
		for (Products p : Products.values()) {
			products.put(p, 0.0);
		}
	}

	/**
	 * how much money do I have
	 */
	protected double money;
	/**
	 * what is my type
	 */
	protected Participants myType;
	/**
	 * how much of everything I need to buy next time
	 */
	protected EnumMap<Products, Double> buy;
	/**
	 * how much of everything I have
	 */
	protected EnumMap<Products, Double> have;
	/**
	 * how much of everything I can sell
	 */
	protected EnumMap<Products, Double> sell;
	/**
	 * what is this week's the price per item
	 */
	protected EnumMap<Products, Double> pricePerItem;
	/**
	 * whom from can I buy {@link Products}
	 */
	protected EnumMap<Participants, Products> buyFrom;
	/**
	 * to whom I sell {@link Products}
	 */
	protected EnumMap<Participants, Products> sellTo;
	/**
	 * list of currently available agents I can sell items to
	 */
	ArrayList<AID> buyerAgentsList;
	/**
	 * list of currently available agents I can buy items from
	 */
	ArrayList<AID> sellerAgentsList;

	/**
	 * how many weeks have passed; useful also to let eg. growers sell products only during the summer
	 */
	protected int weeks = 0;
	
	protected Strategy myStrategy;
	
	// getters needed in ExcelLogger
	public double getMoney() {
		return money;
	}

	public Participants getMyType() {
		return myType;
	}

	public EnumMap<Products, Double> getBuy() {
		return buy;
	}

	public EnumMap<Products, Double> getHave() {
		return have;
	}

	public EnumMap<Products, Double> getSell() {
		return sell;
	}

	public EnumMap<Products, Double> getPricePerItem() {
		return pricePerItem;
	}

	public EnumMap<Participants, Products> getBuyFrom() {
		return buyFrom;
	}

	public EnumMap<Participants, Products> getSellTo() {
		return sellTo;
	}

	public ArrayList<AID> getBuyerAgentsList() {
		return buyerAgentsList;
	}

	public ArrayList<AID> getSellerAgentsList() {
		return sellerAgentsList;
	}

	public int getWeeks() {
		return weeks;
	}

	public Strategy getMyStrategy() {
		return myStrategy;
	}
}
