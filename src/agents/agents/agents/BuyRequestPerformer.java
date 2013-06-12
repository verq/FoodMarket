package agents;

import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import utilities.AgentsUtilities;

/**
 * main inner class responsible for buying actions, more info within {@link SellRequestPerformer.action} method
 * 
 */
// TODO: perhaps extract BuyRequestPerformer to another file
class BuyRequestPerformer extends Behaviour {
	/**
	 * 
	 */
	private final MarketAgent marketAgent;

	/**
	 * @param marketAgent
	 */
	BuyRequestPerformer(MarketAgent marketAgent) {
		this.marketAgent = marketAgent;
	}

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
					if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
						// we received all offers
						offersCnt = 0;
						step = 1;
					}
				}
				break;
			case 1:
				// got offers from everyone, send CFP to all sellers

				// make some decision first
				Map<String, String> responsesToSend = this.marketAgent.createAnswerToSellOffer(sellOffers);

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
					if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
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
				Map<String, Boolean> myDecisions = this.marketAgent.createFinalBuyingDecision(sellOffers);
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
						this.marketAgent.updateBuyerStore(msg.getSender().getName());
					}
					offersCnt++;
					if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
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