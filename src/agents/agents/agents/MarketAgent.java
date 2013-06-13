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

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {
	/**
	 * invoked to maintain buying part of the agent
	 */
	// TODO: perhaps extract TickerBehaviour to another file
	private void buyAction() {
		if (buyFrom.isEmpty()) {
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": not buying from anyone!");
			return;
		}
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			@Override
			protected void onTick() {
				Iterator<Products> buyProductsIterator = buyFrom.values()
						.iterator();
				sellerAgentsList.clear();
				while (buyProductsIterator.hasNext()) {
					DFAgentDescription agentDescription = new DFAgentDescription();
					ServiceDescription serviceDescription = new ServiceDescription();
					String name = buyProductsIterator.next().name();
					serviceDescription.setType(name);
					serviceDescription
							.setName(OfferFormatUtilities.SELL_OFFER_TAG);
					agentDescription.addServices(serviceDescription);
					if (AgentsUtilities.DEBUG_ST_1)
						System.out.println(this.myAgent.getName()
								+ ": I'm looking for someone to buy " + name
								+ " from");

					try {
						DFAgentDescription[] sellingAgents = DFService.search(
								myAgent, agentDescription);

						for (int i = 0; i < sellingAgents.length; ++i) {
							sellerAgentsList.add(sellingAgents[i].getName());
						}
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}
				}

				if (!sellerAgentsList.isEmpty()) {
					System.out.println(this.myAgent.getName()
							+ " found the following " + sellerAgentsList.size()
							+ " seller agents:");
					for (int i = 0; i < sellerAgentsList.size(); ++i) {
						System.out.println("** "
								+ sellerAgentsList.get(i).getName());
					}
					myAgent.addBehaviour(new BuyRequestPerformer(
							MarketAgent.this));

				}
			}
		});
	}

	/**
	 * invoked to maintain selling part of the agent
	 */
	// TODO: perhaps extract TickerBehaviour to another file
	private void sellAction() {
		if (sellTo.isEmpty()) {
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": not selling to anyone!");
			return;
		}
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			@Override
			protected void onTick() {
				Iterator<Products> sellProductsIterator = sellTo.values()
						.iterator();
				DFAgentDescription[] buyingAgents;
				buyerAgentsList.clear();
				while (sellProductsIterator.hasNext()) {
					DFAgentDescription agentDescription = new DFAgentDescription();
					ServiceDescription serviceDescription = new ServiceDescription();
					String name = sellProductsIterator.next().name();
					serviceDescription.setType(name);
					serviceDescription
							.setName(OfferFormatUtilities.BUY_OFFER_TAG);
					agentDescription.addServices(serviceDescription);
					if (AgentsUtilities.DEBUG_ST_1)
						System.out.println(this.myAgent.getName()
								+ ": I'm looking for someone to sell " + name
								+ " to");
					try {
						buyingAgents = DFService.search(myAgent,
								agentDescription);
						for (int i = 0; i < buyingAgents.length; i++) {
							buyerAgentsList.add(buyingAgents[i].getName());
						}
					} catch (FIPAException fe) {
						fe.printStackTrace();
					}
				}

				if (!buyerAgentsList.isEmpty()) {
					System.out.println(this.myAgent.getName()
							+ " found the following " + buyerAgentsList.size()
							+ " buyer agents:");
					for (int i = 0; i < buyerAgentsList.size(); i++) {
						System.out.println("* "
								+ buyerAgentsList.get(i).getName());
					}
					myAgent.addBehaviour(new SellRequestPerformer(
							MarketAgent.this));
				}
			}

		});
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
	// TODO: implement this method
	protected abstract ArrayList<AgentOffer> decideAboutBuyOffer(
			ArrayList<AgentOffer> offers);

	/**
	 * 
	 * @param sellOffers
	 * @return map: to whose offer I respond and what is my answer
	 *         (accept/reject: true/false)
	 */
	// TODO: implement this method
	public abstract Map<String, Boolean> composeFinalBuyingDecision(
			ArrayList<AgentOffer> sellOffers);

	/**
	 * takes list of agents' offers, computes what I want to do and returns list
	 * of offers I want to make
	 * 
	 * @param offers
	 *            from sellers
	 * @return list of {@link AgentOffer} items containing sell offer details
	 */
	// TODO: implement this method
	protected abstract ArrayList<AgentOffer> decideAboutSellOffer(
			ArrayList<AgentOffer> offers);

	/**
	 * invoked at the very end of transaction to confirm it; don't forget to
	 * update my store here!
	 * 
	 * @param traderName
	 * @return return true if I can complete transaction with this buyer,
	 *         otherwise return false
	 */
	// TODO: implement this method
	public abstract boolean confirmSellTransactionWith(String traderName);

	/**
	 * buyer: update my supplies after positive transaction with seller
	 * traderName
	 * 
	 * @param traderName
	 */
	// TODO: implement this method
	public abstract void updateBuyerStore(String traderName);

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
	}

	/**
	 * register my both selling and buying services
	 */
	// TODO: refactor to shorten this method
	private void register() {
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		Set<Products> t = new HashSet<Products>(sellTo.values());
		// TODO: warning: [unchecked] unchecked call to
		// HashSet(java.util.Collection<? extends E>) as a member of the raw
		// type java.util.HashSet
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

		t = new HashSet<Products>(buyFrom.values());
		Iterator<Products> buyProductsIterator = t.iterator();
		while (buyProductsIterator.hasNext()) {
			String name = buyProductsIterator.next().toString();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType(name);
			serviceDescription.setName(OfferFormatUtilities.BUY_OFFER_TAG);
			agentDescription.addServices(serviceDescription);
			if (AgentsUtilities.DEBUG_ST_1)
				System.out.println(myType + ": I register for buying: " + name);
		}
		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException fe) {
			fe.printStackTrace();
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
			@Override
			protected void onTick() {
				produceAndUse();
				prepareForSelling();
			}
		});
	}

	protected void prepareForSelling() {
		for (Products product : sellTo.values()) {
			sell.put(product, have.get(product));
			have.put(product, 0D);
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
	 * how many weeks have passed; useful also to let eg. growers sell products
	 * only during the summer
	 */
	protected int weeks = 0;
}
