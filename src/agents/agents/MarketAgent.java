package agents;

import utilities.*;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import constants.MarketConstants;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {
	/**
	 * main inner class responsible for selling actions, more info within
	 * {@link SellRequestPerformer.action} method
	 * 
	 */
	// TODO: perhaps extract SellRequestPerformer to another file
	private class SellRequestPerformer extends Behaviour {
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private Map<String, String> buyOffers; // seller name : content
		private Map<String, AID> buyTraders; // seller name : where to send
												// reply

		public void action() {
			String conversationID = myType + "=inform";
			try {
				switch (step) {
				case 0:
					buyOffers.clear();
					buyTraders.clear();
					// Send your price to everyone who's interested
					ACLMessage offer_inform = new ACLMessage(ACLMessage.INFORM);
					for (int i = 0; i < buyerAgentsList.size(); ++i) {
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ " 1) sell: sending sell offer to: "
									+ buyerAgentsList.get(i).getName());
						offer_inform.addReceiver(buyerAgentsList.get(i));
					}
					offer_inform.setContent(composeSellContent());
					offer_inform.setConversationId(conversationID);
					offer_inform.setReplyWith(conversationID
							+ System.currentTimeMillis());
					myAgent.send(offer_inform);
					step = 1;
					break;
				case 1:
					// Receive all proposals/refusals from buyers agents
					mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
					ACLMessage reply = myAgent.receive(mt);
					if (reply != null) {
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ " 4) sell: got offer from "
									+ reply.getSender().getName() + ": "
									+ reply.getContent());
						// remember buyers
						buyOffers.put(reply.getSender().getName(),
								reply.getContent());
						buyTraders.put(reply.getSender().getName(),
								reply.getSender());
						repliesCnt++;
						if (repliesCnt >= buyerAgentsList.size()) {
							// We received all CFPs
							step = 2;
						}
					} else {
						block();
					}
					break;
				case 2:
					// we got all cfp from buyers: make the decision and respond
					// decide:
					Map<String, String> responsesToSend = createAnswerToBuyOffer(buyOffers);
					// Respond to buyers with your decision
					if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
						System.out
								.println(myAgent.getName()
										+ " 5) sell: got all offers - responding with decision");
					Iterator<String> buyerIterator = responsesToSend.keySet()
							.iterator();
					while (buyerIterator.hasNext()) {
						ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
						String name = buyerIterator.next();
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ " 5a) sell: sending decision to: "
									+ buyTraders.get(name).getName());

						cfp.addReceiver(buyTraders.get(name));
						cfp.setContent(composeSellContent());
						cfp.setConversationId("propose" + myType);
						cfp.setReplyWith("propose" + System.currentTimeMillis());
						myAgent.send(cfp);
					}
					step = 3;
					repliesCnt = 0;
					break;
				case 3:
					// get confirmation from buyers, update supplies store and
					// confirm back
					mt = MessageTemplate
							.or(MessageTemplate
									.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
									MessageTemplate
											.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
					ACLMessage msg = myAgent.receive(mt);
					if (msg != null) {
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out
									.println(myAgent.getName()
											+ " 7) sell: got accepted confirmation from "
											+ msg.getSender().getName());
						ACLMessage confirm = msg.createReply();
						boolean confirmed = confirmSellTransactionWith(msg
								.getSender().getName());
						if (confirmed)
							confirm.setPerformative(ACLMessage.CONFIRM);
						else
							confirm.setPerformative(ACLMessage.REFUSE);
						confirm.setReplyWith("cfp" + System.currentTimeMillis());
						myAgent.send(confirm);
						repliesCnt++;
						if (repliesCnt >= buyerAgentsList.size()) {
							step = 4;
						}
					} else {
						block();
					}
					break;
				case 4:
					if (AgentsUtilities.DEBUG_ST_1)
						System.out.println("sell: step = end");
					step = 0;
					repliesCnt = 0;
					buyOffers.clear();
					buyTraders.clear();
					block();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
			buyOffers = new HashMap<String, String>();
			buyTraders = new HashMap<String, AID>();
		}

		@Override
		public boolean done() {
			return step == 4;
		}
	}

	// end of SellRequestPerformer

	/**
	 * main inner class responsible for buying actions, more info within
	 * {@link SellRequestPerformer.action} method
	 * 
	 */
	// TODO: perhaps extract BuyRequestPerformer to another file
	private class BuyRequestPerformer extends Behaviour {
		private int step = 0;
		private MessageTemplate mt;
		private ACLMessage msg;
		private int offersCnt = 0;
		private Map<String, String> sellOffers;
		private Map<String, AID> sellTraders;

		public void action() {
			try {
				switch (step) {
				case 0:
					// get current prices from sellers
					mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
					ACLMessage sell_offer = myAgent.receive(mt);
					if (sell_offer != null) {
						// reply received
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ " 2) buy: got offer from "
									+ sell_offer.getSender().getName() + ": "
									+ sell_offer.getContent());

						// remember this offer
						sellOffers.put(sell_offer.getSender().getName(),
								sell_offer.getContent());
						sellTraders.put(sell_offer.getSender().getName(),
								sell_offer.getSender());
						offersCnt++;
						if (offersCnt >= sellerAgentsList.size()) {
							// we received all offers
							offersCnt = 0;
							step = 1;
						}
					}
					break;
				case 1:
					// got offers from everyone, send CFP to all sellers

					// make some decision first
					Map<String, String> responsesToSend = createAnswerToSellOffer(sellOffers);

					if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
						System.out
								.println(myAgent.getName()
										+ " 3) buy: received all offers, sending cfp with decision");
					Iterator<String> sellersIterator = responsesToSend.keySet()
							.iterator();
					// send offer specific response to every agent
					while (sellersIterator.hasNext()) {
						ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
						String name = sellersIterator.next();

						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ "3a) buy: sending to " + name);

						cfp.addReceiver(sellTraders.get(name));
						cfp.setContent(responsesToSend.get(name));
						myAgent.send(cfp);
					}
					sellTraders.clear();
					sellOffers.clear();
					step = 2;
					break;
				case 2:
					// receive sellers' decision
					mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
					ACLMessage sellerDecision = myAgent.receive(mt);
					if (sellerDecision != null) {
						// receive decisions from sellers
						if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
							System.out.println(myAgent.getName()
									+ " 6) buy: got decision from "
									+ sellerDecision.getSender().getName()
									+ ": " + sellerDecision.getContent());

						// remember decisions and content
						sellOffers.put(sellerDecision.getSender().getName(),
								sellerDecision.getContent());
						sellTraders.put(sellerDecision.getSender().getName(),
								sellerDecision.getSender());
						offersCnt++;
						if (offersCnt >= sellerAgentsList.size()) {
							// we received all decisions
							offersCnt = 0;
							step = 3;
						}
					} else {
						block();
					}
					break;
				case 3:
					// received all decisions: sending confirmation
					// (accept/reject the offer)
					Map<String, Boolean> myDecisions = createFinalBuyingDecision(sellOffers);
					Iterator<String> decisionIterator = myDecisions.keySet()
							.iterator();

					// send your deficion to evey seller
					while (decisionIterator.hasNext()) {
						String recipientName = decisionIterator.next();
						ACLMessage dec;
						if (myDecisions.get(recipientName) == true) {
							dec = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
							if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
								System.out.println(myAgent.getName()
										+ " 6a) buy: accepted offer from "
										+ recipientName);
						} else {
							dec = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
							if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
								System.out.println(myAgent.getName()
										+ " 6a) buy: rejected offer from "
										+ recipientName);
						}
						dec.addReceiver(sellTraders.get(recipientName));
						myAgent.send(dec);
					}
					offersCnt = 0;
					step = 4;
				case 4:
					// receive confirmation of transaction and update supplies
					mt = MessageTemplate.or(MessageTemplate
							.MatchPerformative(ACLMessage.CONFIRM),
							MessageTemplate
									.MatchPerformative(ACLMessage.REFUSE));
					msg = myAgent.receive(mt);
					if (msg != null) {
						if (msg.getPerformative() == ACLMessage.CONFIRM) {
							if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
								System.out
										.println(myAgent.getName()
												+ " 8) buy: received confirmation from "
												+ msg.getSender().getName());
							updateBuyerStore(msg.getSender().getName());
						}
						offersCnt++;
						if (offersCnt >= sellerAgentsList.size()) {
							step = 5;
						}
					}
					break;
				case 5:
					if (AgentsUtilities.DEBUG_ST_1)
						System.out.println("buy: step = end");
					step = 0;
					offersCnt = 0;
					sellOffers.clear();
					sellTraders.clear();
					block();
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
			sellOffers = new HashMap<String, String>();
			sellTraders = new HashMap<String, AID>();
		}

		@Override
		public boolean done() {
			return step == 5;
		}
	}

	// end of SellRequestPerformer

	/**************** nothing interesting down there!!! ****************/

	/**
	 * invoked to mantain buying part of the agent
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
					myAgent.addBehaviour(new BuyRequestPerformer());

				}
			}
		});
	}

	/**
	 * invoked to mantain selling part of the agent
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
					myAgent.addBehaviour(new SellRequestPerformer());
				}
			}

		});
	}

	/**
	 * invoked to mantain weekly resource updates
	 */
	private void weeklyResourceUpdateAction() {
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			@Override
			protected void onTick() {
				updateResources();
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
	// TODO: implement this method
	protected abstract void updateResources();

	/*************** end of strategy related methods ***************/

	/************ beginning of offers formatters ************/
	/**
	 * invoked by buyer after receiving all sellers' offers
	 * 
	 * @param buy
	 *            offers containing offerer's name and offer as String
	 * @return answers to buy offers containing recepient name and answer as
	 *         String
	 */
	private Map<String, String> createAnswerToSellOffer(
			Map<String, String> offers) {
		return AgentsUtilities
				.createMapOfOffers(decideAboutSellOffer(AgentsUtilities
						.createListOfOffers(offers)));
	}

	/**
	 * invoked by seller after receiving all buyers' offers
	 * 
	 * @param buy
	 *            offers containing offerer's name and offer as String
	 * @return answers to buy offers containing recepient name and answer as
	 *         String
	 */
	private Map<String, String> createAnswerToBuyOffer(
			Map<String, String> offers) {
		return AgentsUtilities
				.createMapOfOffers(decideAboutBuyOffer(AgentsUtilities
						.createListOfOffers(offers)));
	}

	private Map<String, Boolean> createFinalBuyingDecision(
			Map<String, String> offers) {
		return composeFinalBuyingDecision(AgentsUtilities
						.createListOfOffers(offers));
	}
	/**
	 * format my sell offer content based on my current supplies
	 * 
	 * @return formated sell offer
	 */
	private String composeSellContent() {
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
		weeklyResourceUpdateAction();
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

	/* ============= just initialization down there ============= */
	/**
	 * Set how much of everything I need to buy first week
	 */
	protected abstract void fillInitialBuy();

	/**
	 * Set how much of everything I have in the begining
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
	private ArrayList<AID> buyerAgentsList;
	/**
	 * list of currently available agents I can buy items from
	 */
	private ArrayList<AID> sellerAgentsList;
	
	/**
	 * how many weeks have passed;
	 * useful also to let eg. growers sell products only during the summer
	 */
	protected int weeks = 0;
}
