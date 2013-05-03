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
	protected int money;
	protected EnumMap<Products, Integer> buy;
	protected EnumMap<Products, Integer> have;
	protected EnumMap<Products, Integer> sell;
	protected EnumMap<Participants, Products> buyFrom;
	protected EnumMap<Participants, Products> sellTo;
	protected Random rand = new Random();

	protected int random(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
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
					AID[] sellerAgents = new AID[sellingAgents.length];
					for (int i = 0; i < sellingAgents.length; ++i) {
						sellerAgents[i] = sellingAgents[i].getName();
						System.out.println("/t" + sellerAgents[i].getName());
					}
				} catch (FIPAException fe) {
					fe.printStackTrace();
				}
			}
		});
	}
}
