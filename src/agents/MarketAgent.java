package agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Random;

import constants.MarketConstants;
import constants.Participants;
import constants.Products;

public abstract class MarketAgent extends Agent {
	protected int money;
	protected EnumMap<Products, Integer> buy;
	protected EnumMap<Products, Integer> have;
	protected EnumMap<Products, Integer> sell;
	protected EnumMap<Participants, Products> buyFrom;
	protected EnumMap<Participants, Products> sellTo;
	protected AID[] sellerAgents;
	protected Random rand = new Random();

	protected int random(int min, int max) {
		if (max - min + 1 > 0) {
			return rand.nextInt(max - min + 1) + min;
		} else {
			return rand.nextInt(min - max + 1) + max;
		}
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

			System.out.println("type: " + serviceDescription.getType());
		}
		try {
			DFService.register(this, agentDescription);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
	}

	private void buyAction() {
		searchForSellers();

	}

	private void searchForSellers() {
		addBehaviour(new TickerBehaviour(this, MarketConstants.WEEK) {
			protected void onTick() {
				DFAgentDescription agentDescription = new DFAgentDescription();
				Iterator<Products> buyProductsIterator = buyFrom.values().iterator();

				while (buyProductsIterator.hasNext()) {
					ServiceDescription serviceDescription = new ServiceDescription();
					String name = buyProductsIterator.next().name();
					serviceDescription.setType(name);
					agentDescription.addServices(serviceDescription);
				}
				try {
					DFAgentDescription[] sellingAgents = DFService.search(myAgent, agentDescription);
					System.out.println(this.myAgent.getName() + " found the following seller agents:");
					sellerAgents = new AID[sellingAgents.length];
					for (int i = 0; i < sellingAgents.length; ++i) {
						sellerAgents[i] = sellingAgents[i].getName();
						System.out.println("/t" + sellerAgents[i].getName());
					}
					System.out.println();
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
				buyProductsIterator = buyFrom.values().iterator();
				while (buyProductsIterator.hasNext()) {
					addBehaviour(new BuyPerformer(buyProductsIterator.next()));
				}
			}
		});
	}

	private class BuyPerformer extends Behaviour {
		private AID bestSeller; // The agent who provides the best offer
		private int bestPrice; // The best offered price
		private int repliesCnt = 0; // The counter of replies from seller agents
		private MessageTemplate mt; // The template to receive replies
		private int step = 0;
		private Products product;

		public BuyPerformer(Products p) {
			this.product = p;
		}

		public void action() {
			switch (step) {
			case 0:
				// Send the cfp to all sellers
				ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
				for (int i = 0; i < sellerAgents.length; ++i) {
					cfp.addReceiver(sellerAgents[i]);
				}
				cfp.setContent(product.name());
				cfp.setConversationId("book-trade");
				cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
				myAgent.send(cfp);

				// Prepare the template to get proposals
				mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
						MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
				step = 1;
				break;
			case 1:
				// Receive all proposals/refusals from seller agents
				ACLMessage reply = myAgent.receive(mt);
				if (reply != null) {
					// Reply received
					if (reply.getPerformative() == ACLMessage.PROPOSE) {
						// This is an offer
						int price = Integer.parseInt(reply.getContent());
						if (bestSeller == null || price < bestPrice) {
							// This is the best offer at present
							bestPrice = price;
							bestSeller = reply.getSender();
						}
					}
					repliesCnt++;
					if (repliesCnt >= sellerAgents.length) {
						// We received all replies
						step = 2;
					}
				} else {
					block();
				}
				break;
			case 2:
				// Send the purchase order to the seller that provided the best offer
				ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				order.addReceiver(bestSeller);
				order.setContent(product.name());
				order.setConversationId("book-trade");
				order.setReplyWith("order" + System.currentTimeMillis());
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
						System.out.println(product.name() + " successfully purchased from agent "
								+ reply.getSender().getName());
						System.out.println("Price = " + bestPrice);
						myAgent.doDelete();
					} else {
						System.out.println("Attempt failed: requested book already sold.");
					}

					step = 4;
				} else {
					block();
				}
				break;
			}
		}

		public boolean done() {
			if (step == 2 && bestSeller == null) {
				System.out.println("Attempt failed: " + product.name() + " not available for sale");
			}
			return ((step == 2 && bestSeller == null) || step == 4);
		}
	}
}
