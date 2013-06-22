package agents;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

import java.util.Iterator;

import utilities.AgentsUtilities;
import utilities.Main;
import constants.OfferFormatUtilities;
import constants.Products;

final class SellTickerBehaviour extends TickerBehaviour {
	/**
	 * 
	 */
	private final MarketAgent marketAgent;

	SellTickerBehaviour(MarketAgent marketAgent, Agent a, long period) {
		super(a, period);
		this.marketAgent = marketAgent;
	}

	@Override
	protected void onTick() {
		Main.getExcelLogger().writeAgent(marketAgent);
		Iterator<Products> sellProductsIterator = this.marketAgent.sellTo.values()
				.iterator();
		DFAgentDescription[] buyingAgents;
		this.marketAgent.buyerAgentsList.clear();
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
					this.marketAgent.buyerAgentsList.add(buyingAgents[i].getName());
				}
			} catch (FIPAException fe) {
				fe.printStackTrace();
			}
		}

		if (!this.marketAgent.buyerAgentsList.isEmpty()) {
			System.out.println(this.myAgent.getName()
					+ " found the following " + this.marketAgent.buyerAgentsList.size()
					+ " buyer agents:");
			for (int i = 0; i < this.marketAgent.buyerAgentsList.size(); i++) {
				System.out.println("* "
						+ this.marketAgent.buyerAgentsList.get(i).getName());
			}
			myAgent.addBehaviour(new SellRequestPerformer(
					this.marketAgent));
		}
	}
}