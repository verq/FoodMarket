package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Random;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {
	protected double money;
	protected EnumMap<Products, Integer> buy;
	protected EnumMap<Products, Integer> have;
	protected EnumMap<Products, Integer> sell;
	protected EnumMap<Participants, Products> buyFrom;
	protected EnumMap<Participants, Products> sellTo;
	protected Random rand = new Random();

	protected int random(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	protected double randomDouble(double min, double max)
	{
		return (max - min) * rand.nextDouble() + min;
	}
	protected void initializeProducts() {
		buy = new EnumMap<Products, Integer>(Products.class);
		have = new EnumMap<Products, Integer>(Products.class);
		sell = new EnumMap<Products, Integer>(Products.class);
		buyFrom = new EnumMap<Participants, Products>(Participants.class);
		sellTo = new EnumMap<Participants, Products>(Participants.class);
		initializeAddProducts(buy);
		initializeAddProducts(have);
		initializeAddProducts(sell);
		fillInitialSell();
		fillInitialHave();
		fillInitialBuy();
		fillBuyFrom();
		fillSellTo();
	}

	protected void initializeAddProducts(EnumMap<Products, Integer> products) {
		for (Products p : Products.values()) {
			products.put(p, 0);
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
		Iterator<Products> sellProductsIterator = sellTo.values().iterator();
		while (sellProductsIterator.hasNext()) { // TODO: remove duplicats
			String name = sellProductsIterator.next().name();
			ServiceDescription serviceDescription = new ServiceDescription();
			serviceDescription.setType(name);
			serviceDescription.setName(name);

			agentDescription.addServices(serviceDescription);

			System.out.println("I sell type: " + serviceDescription.getType());
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
					serviceDescription.setType("buy:"+name);
					agentDescription.addServices(serviceDescription);
				}
				try {
					DFAgentDescription[] sellingAgents = DFService.search(myAgent, agentDescription);
					System.out.println(this.myAgent.getName() + " found the following " + sellingAgents.length + " seller agents:");
					AID[] sellerAgents = new AID[sellingAgents.length];
					for (int i = 0; i < sellingAgents.length; ++i) {
						sellerAgents[i] = sellingAgents[i].getName();
						System.out.println("** " + sellerAgents[i].getName());
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
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
					serviceDescription.setType("sell:"+name);
					agentDescription.addServices(serviceDescription);
				}
				try {
					DFAgentDescription[] buyingAgents = DFService.search(myAgent, agentDescription);
					System.out.println(this.myAgent.getName() + " found the following " + buyingAgents.length + " buyer agents:");
					AID[] buyerAgents = new AID[buyingAgents.length];
					Iterator<AID> buyingIterator = buyingAgents.getAllServices();
					while(buyingIterator.hasNext()) {
						System.out.println("******" + buyingIterator);
						buyingIterator.next();
					}
					for (int i = 0; i < buyingAgents.length; ++i) {
						buyerAgents[i] = buyingAgents[i].getName();
						if(buyerAgents[i].getName().compareTo(this.myAgent.getName()) != 0) continue;
						System.out.println("* " + buyerAgents[i].getName() + " all: " + buyerAgents[i]);
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
	}
	
		private class SellRequestPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer 
		private int bestPrice;  // The best offered price
        private AID secondBestSeller; // The agent who provides the best offer 
        private int secondBestPrice;  // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;

		public void action() {
			switch (step) {
			case 0:
                bestPrice = secondBestPrice = Integer.MAX_VALUE;
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					System.out.println("sending acl message to " + sellerAgents[i]);
					cfp.addReceiver(sellerAgents[i]);
				} 
				cfp.setContent("sell item");
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);
				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("sell"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from buyers agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer 
						int price = Integer.parseInt(reply.getContent());
						System.out.println("Buyer: got offer from " + reply.getSender() + " price: " + price);
						if (bestSeller == null) {
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 2; 
					}
				}
				else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
				System.out.println("Buyer: lowest offer is " + bestPrice + " from " + bestSeller.getName());
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.setContent("sialalala");
				order.setConversationId("book-trade");
				order.setReplyWith("order"+System.currentTimeMillis());
				myAgent.send(order);
				// Prepare the template to get the purchase order reply
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(order.getReplyWith()));
				step = 3;
				break;
			case 3:      
				// Receive the purchase order reply
				reply = myAgent.receive(mt);
				if (reply != null) {
					// Purchase order reply received
					if (reply.getPerformative() == ACLMessage.INFORM) {
						// Purchase successful. We can terminate
						System.out.println("successfully purchased from agent "+reply.getSender().getName());
						System.out.println("Price = "+bestPrice);
						myAgent.doDelete();
					}
					else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = 4;
				}
				else {
					block();
				}
				break;
			}        
		}
}
