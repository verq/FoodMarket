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

final class BuyTickerBehaviour extends TickerBehaviour {
	/**
	 * 
	 */
	private final MarketAgent marketAgent;
	private BuyRequestPerformer buyRequestPerformer;
	BuyTickerBehaviour(MarketAgent marketAgent, Agent a, long period) {
		super(a, period);
		this.marketAgent = marketAgent;
	}

	@Override
	protected void onTick() {
		if(AgentsUtilities.CREATE_LOGS) Main.getExcelLogger().writeAgent(marketAgent);
		if(buyRequestPerformer != null && !buyRequestPerformer.done()) return;
		Iterator<Products> buyProductsIterator = this.marketAgent.buyFrom.values()
				.iterator();
		this.marketAgent.sellerAgentsList.clear();
		while (buyProductsIterator.hasNext()) {
			DFAgentDescription agentDescription = new DFAgentDescription();
			ServiceDescription serviceDescription = new ServiceDescription();
			String name = buyProductsIterator.next().name();
			serviceDescription.setType(name);
			serviceDescription
					.setName(OfferFormatUtilities.SELL_OFFER_TAG);
			agentDescription.addServices(serviceDescription);
			if (AgentsUtilities.DEBUG_ST_1) {
				System.out.println(this.myAgent.getName()
						+ ": I'm looking for someone to buy " + name
						+ " from");
			}

			try {
				DFAgentDescription[] sellingAgents = DFService.search(
						myAgent, agentDescription);

				for (int i = 0; i < sellingAgents.length; ++i) {
					if(this.marketAgent.sellerAgentsList.contains(sellingAgents[i].getName())) continue;
					this.marketAgent.sellerAgentsList.add(sellingAgents[i].getName());
				}
			} catch (FIPAException fe) {
				System.out.println(fe.getMessage());
			}
		}

		if (!this.marketAgent.sellerAgentsList.isEmpty()) {
			if (AgentsUtilities.PRINT_FINDING_STAGE) {
			System.out.println(this.myAgent.getName()
					+ " found the following " + this.marketAgent.sellerAgentsList.size()
					+ " seller agents:");
			}
			for (int i = 0; i < this.marketAgent.sellerAgentsList.size(); ++i) {
				if (AgentsUtilities.PRINT_FINDING_STAGE) {
				System.out.println("** "
						+ this.marketAgent.sellerAgentsList.get(i).getName());
				}
			}
			buyRequestPerformer = new BuyRequestPerformer(
					this.marketAgent);
			myAgent.addBehaviour(buyRequestPerformer);

		}
	}
}