package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Random;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {
	protected double money;
	protected Participants myType;
	protected EnumMap<Products, Double> buy; // how much of everything he needs to buy next time
	protected EnumMap<Products, Double> have; // how much of everything he has
	protected EnumMap<Products, Double> sell; // how much of everything he has to sell
	protected EnumMap<Products, Double> pricePerItem; // how much of everything he has to sell
	protected EnumMap<Participants, Products> buyFrom;
	protected EnumMap<Participants, Products> sellTo;
	protected Random rand = new Random();
	private AID[] sellerAgents;
	private AID[] buyerAgents;
	
	protected int random(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	protected double randomDouble(double min, double max)
	{
		return (max - min) * rand.nextDouble() + min;
	}
	
	protected void initializeProducts() {
		buy = new EnumMap<Products, Double>(Products.class);
		have = new EnumMap<Products, Double>(Products.class);
		sell = new EnumMap<Products, Double>(Products.class);
		pricePerItem = new EnumMap<Products, Double>(Products.class);
		buyFrom = new EnumMap<Participants, Products>(Participants.class);
		sellTo = new EnumMap<Participants, Products>(Participants.class);
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

	protected abstract void fillInitialBuy();

	protected abstract void fillInitialHave();

	protected abstract void fillInitialSell();

	protected abstract void fillBuyFrom();

	protected abstract void fillSellTo();

	protected void takeDown() {
		System.out.println("Agent " + this.getClass().getName() + " " + getAID().getName() + " is terminating.");
	}

	protected void setup() {
		initializeProducts();
		System.out.println("Agent " + this.getClass().getName() + " " + getAID().getName() + " is starting");
		register();
		buyAction();
		sellAction();
	}

	private void register() {
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(getAID());
		Set t = new HashSet(sellTo.values());
		// TODO: warning: [unchecked] unchecked call to HashSet(java.util.Collection<? extends E>) as a member of the raw type java.util.HashSet
		Iterator sellProductsIterator = t.iterator();
		while (sellProductsIterator.hasNext()) {
			String name = sellProductsIterator.next().toString();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("sell");
			serviceDescription.setName(name);
			agentDescription.addServices(serviceDescription);
			System.out.println(myType + ": I register for selling: " + name);
		}
		
		t = new HashSet(buyFrom.values());
		Iterator buyProductsIterator = t.iterator();
		while (buyProductsIterator.hasNext()) {
			String name = buyProductsIterator.next().toString();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType("buy");
			serviceDescription.setName(name);
			agentDescription.addServices(serviceDescription);
			System.out.println(myType + ": I register for buying: " + name);
		}
		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private void buyAction() {
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			protected void onTick() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				Iterator<Products> buyProductsIterator = buyFrom.values().iterator();

				while (buyProductsIterator.hasNext()) {
					ServiceDescription serviceDescription = new ServiceDescription();
					String name = buyProductsIterator.next().name();
					serviceDescription.setType("sell");
					serviceDescription.setName(name);
					agentDescription.addServices(serviceDescription);
					System.out.println(this.myAgent.getName() + ": I'm looking for someone to buy from: " + serviceDescription.getName());
				}
				try {
					DFAgentDescription[] sellingAgents = DFService.search(myAgent, agentDescription);
					System.out.println(this.myAgent.getName() + " found the following " + sellingAgents.length + " seller agents:");
					sellerAgents = new AID[sellingAgents.length];
					for (int i = 0; i < sellingAgents.length; ++i) {
						//if(sellerAgents[i].getName().compareTo(this.myAgent.getName()) != 0) continue;
						sellerAgents[i] = sellingAgents[i].getName();
						System.out.println("** " + sellerAgents[i].getName());
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				myAgent.addBehaviour(new BuyRequestPerformer());
			}
		});
	}

	private void sellAction() {
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			protected void onTick() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				Iterator<Products> sellProductsIterator = sellTo.values().iterator();

				while (sellProductsIterator.hasNext()) {
					ServiceDescription serviceDescription = new ServiceDescription();
					String name = sellProductsIterator.next().name();
					serviceDescription.setType("sell");
					serviceDescription.setName(name);
					agentDescription.addServices(serviceDescription);
					System.out.println(this.myAgent.getName() + ": I'm looking for someone to sell to: " + serviceDescription.getName());
				}
				try {
					DFAgentDescription[] buyingAgents = DFService.search(myAgent, agentDescription);
					System.out.println(this.myAgent.getName() + " found the following " + buyingAgents.length + " buyer agents:");
					//if(buyingAgents.length > 0) 
					buyerAgents = new AID[buyingAgents.length];//Math.max(0,buyingAgents.length-1)];
					for (int i = 0; i < buyingAgents.length;) {
						//if(buyingAgents[i].getName().getName().compareTo(this.myAgent.getName()) == 0) continue;
						buyerAgents[i] = buyingAgents[i].getName();
						System.out.println("* " + buyerAgents[i].getName() + " all: " + buyerAgents[i]);
						i ++;
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				myAgent.addBehaviour(new SellRequestPerformer());
			}
		});
	}
	
	private String composeSellContent() {
		if(sell == null || sell.size() == 0) return "";
		String content = myType + "|sell|";
		Iterator<Products> sellProductsIterator = sell.keySet().iterator();
		boolean nonzero = false;
		while (sellProductsIterator.hasNext()) {
			Products p = sellProductsIterator.next();
			if(sell.get(p) > 0.0) {
				content += p + " " + sell.get(p) + " " + pricePerItem.get(p) + ":";
				nonzero = true;
			}
		}
		if(!nonzero) return "";
		return content;
	}
	
	private String composeBuyContent() {
		if(buy.size() == 0 || buy.size() == 0) return "";
		String content = myType + "|buy|";
		Iterator<Products> buyProductsIterator = buy.keySet().iterator();
		while (buyProductsIterator.hasNext()) {
			Products p = buyProductsIterator.next();
			if(buy.get(p) > 0.0)
				content += p + " " + buy.get(p) + " " + pricePerItem.get(p) + ":";
		}
		return content;
	}
	// returns price agent is ready to pay or -1 if he doesn't agree for the offer at all
	protected int decideAboutSellOffer(Products product, int price) { 
		return 15;
	}

	// returns amount of money agent wants to get or -1 if he doesn't agree for the offer at all
	protected int decideAboutBuyOffer(Products product, int price) { 
		return 15;
	}

	private class SellRequestPerformer extends CyclicBehaviour {
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private Map<String, String> buyOffers; // seller name : content
		private Map <String, AID> buyTraders;
		
		public void onStart() {
			buyOffers =  new HashMap<String, String>();
			buyTraders = new HashMap<String, AID>();
		}
		public void action() {
			//System.out.println("afsfsfdef" + step);
			switch (step) {
			case 0:
				// Send your price to everyone
				//if(buyerAgents.length == 0) block();
				ACLMessage offer_inform = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < buyerAgents.length; ++i) {
					System.out.println(myAgent.getName() + " 1) sell: sending sell offer to: " + buyerAgents[i]);
					offer_inform.addReceiver(buyerAgents[i]);
				} 
				offer_inform.setContent(composeSellContent());
				offer_inform.setConversationId(myType + "|inform"); // this should be type of the agent
				offer_inform.setReplyWith(myType + "|inform"+System.currentTimeMillis());
				myAgent.send(offer_inform);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("sell"),
						MessageTemplate.MatchInReplyTo(offer_inform.getReplyWith()));
				 step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from buyers agents 
				mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// This is a proposal 
					System.out.println(myAgent.getName() + " 4) sell: got offer from " + reply.getSender() + "  :  " + reply.getContent());
					buyOffers.put(reply.getSender().getName(), reply.getContent());
					buyTraders.put(reply.getSender().getName(), reply.getSender());
					repliesCnt++;
					if (repliesCnt >= buyerAgents.length) {
						// We received all CFPs
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				// Respond buyers with your decision
				System.out.println(myAgent.getName() + " 5) sell: got all offers - responding with decision");
				ACLMessage cfp = new ACLMessage(ACLMessage.PROPOSE);
				for (int i = 0; i < buyerAgents.length; ++i) {
					System.out.println(myAgent.getName() + " 5a) sell: sending decision to: " + buyerAgents[i]);
					cfp.addReceiver(buyerAgents[i]); // buyOffers
				} 
				cfp.setContent(composeSellContent());
				cfp.setConversationId("propose" + myType);
				cfp.setReplyWith("propose"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				step = 3;
				break;
			case 3:
				// Get confirmation from buyers, update supplies store and confirm back
				mt = MessageTemplate.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL);
				ACLMessage msg = myAgent.receive(mt);
				if (msg != null) {
					String title = msg.getContent();
					System.out.println(myAgent.getName() + " 7) sell: " + title+"got accepted confirmation from "+msg.getSender().getName());
					ACLMessage confirm = msg.createReply();
					confirm.setPerformative(ACLMessage.CONFIRM);
					confirm.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
					myAgent.send(confirm);
					step = 4;
				}
				else {
					block();
				}
				break;	
			case 4:
				System.out.println("sell: step = end");
				step = 0;
				block();
				break;
				
			}        
		}
	}
	
	private class BuyRequestPerformer extends CyclicBehaviour {
		private int step = 0;
		private MessageTemplate mt;
		private ACLMessage msg;
		private int offersCnt = 0;
		private Map<String, String> sellOffers;
		private Map<String, AID> sellTraders;

		public void onStart() {
			sellOffers =  new HashMap<String, String>();
			sellTraders = new HashMap<String, AID>();
		}
		public void action() {
			switch(step) {
			case 0:
				// Get current prices from sellers
				mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
				ACLMessage sell_offer = myAgent.receive(mt);
				if (sell_offer != null) {
					// Reply received
						System.out.println(myAgent.getName() + " 2) buy: got offer from " + sell_offer.getSender() + "  :  " + sell_offer.getContent());
						sellOffers.put(sell_offer.getSender().getName(), sell_offer.getContent());
						sellTraders.put(sell_offer.getSender().getName(), sell_offer.getSender());
					offersCnt++;
					if (offersCnt >= sellerAgents.length) {
						// We received all information
						step = 1; 
					}
				}
				break;
			case 1:
				// Send CFP to sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				System.out.println(myAgent.getName() + " 3) buy: received all info, sending cfp");
				Iterator<String> tradersIterator = sellTraders.keySet().iterator();
				while(tradersIterator.hasNext()) {
					String name = tradersIterator.next();
					System.out.println("3) buy: sending to " + name);
					// make some decision...
					cfp.addReceiver(sellTraders.get(name)); 
				}
				cfp.setContent(composeBuyContent());
				myAgent.send(cfp);
				step = 2;
				break;
			case 2:
				// Receive sellers' decision and accept or reject it
				mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
				msg = myAgent.receive(mt);
				if (msg != null) {
					// Receive decisions from sellers
					String title = msg.getContent();
					ACLMessage reply = msg.createReply();
					reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
					System.out.println(myAgent.getName() + " 6) buy: " + title+"accepted offer from "+msg.getSender().getName());
					myAgent.send(reply);
					step = 3;
				}
				else {
					block();
				}
				break;
			case 3:
				// Receive confirmation of transaction and update supplies
				mt = MessageTemplate.MatchPerformative(ACLMessage.CONFIRM);
				msg = myAgent.receive(mt);
				if (msg != null) {
					System.out.println(myAgent.getName() + " 8) buy: received confirmation from "+msg.getSender().getName());
					step = 3;
				}
				break;
			case 4:
				System.out.println("buy: step = end");
				step = 0;
				block();
				break;

			}
		}
	}
	
}