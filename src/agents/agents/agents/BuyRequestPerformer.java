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
 * main inner class responsible for buying actions, more info within
 * {@link SellRequestPerformer.action} method
 * 
 */
class BuyRequestPerformer extends Behaviour {
	/**
	 * 
	 */
	private final MarketAgent marketAgent;
	private final int STEP_START_ACTION = 0;
	private final int STEP_RECIVED_CURRENT_PRICES = 1;
	private final int STEP_SENT_CFP_TO_SELLERS = 2;
	private final int STEP_RECIVED_SELLERS_DECISIONS = 3;
	private final int STEP_SENDED_CONFIRMATION = 4;
	private final int STEP_RECIVED_CONFIRMATION = 5;
	private final int STEP_BLOCK = 6;
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

	private int getCurrentPricesFromSellers() {
		// get current prices from sellers
		mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM);
		ACLMessage sell_offer = myAgent.receive(mt);
		if (sell_offer != null) {
			// reply received
			if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
				System.out.println(myAgent.getName() + " 2) buy: got offer from " + sell_offer.getSender().getName()
						+ ": " + sell_offer.getContent());
			}
			// remember this offer
			sellOffers.put(sell_offer.getSender().getName(), sell_offer.getContent());
			sellTraders.put(sell_offer.getSender().getName(), sell_offer.getSender());
			offersCnt++;
			if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
				// we received all offers
				offersCnt = 0;
				return STEP_RECIVED_CURRENT_PRICES;
			}
		}
		return STEP_START_ACTION;
	}

	private int sendCFPToSellers() {
		// got offers from everyone, send CFP to all sellers

		// make some decision first
		Map<String, String> responsesToSend = this.marketAgent.createAnswerToSellOffer(sellOffers);

		if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
			System.out.println(myAgent.getName() + " 3) buy: received all offers, sending cfp with decision");
		}
		Iterator<String> sellersIterator = responsesToSend.keySet().iterator();
		System.out.println(responsesToSend);
		// send offer specific response to every agent
		while (sellersIterator.hasNext()) {
			ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
			String name = sellersIterator.next();

			cfp.addReceiver(sellTraders.get(name));
			cfp.setContent(responsesToSend.get(name));
			myAgent.send(cfp);

			if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
				System.out.println(myAgent.getName() + "3a) buy: sending to " + name + " my response: "
						+ responsesToSend.get(name));
			}
		}
		sellTraders.clear();
		sellOffers.clear();
		return STEP_SENT_CFP_TO_SELLERS;
	}

	private int reciveSellersDecision() {
		// receive sellers' decision
		mt = MessageTemplate.MatchPerformative(ACLMessage.PROPOSE);
		ACLMessage sellerDecision = myAgent.receive(mt);
		if (sellerDecision != null) {
			// receive decisions from sellers
			if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
				System.out.println(myAgent.getName() + " 6) buy: got decision from "
						+ sellerDecision.getSender().getName() + ": " + sellerDecision.getContent());
			}
			// remember decisions and content
			sellOffers.put(sellerDecision.getSender().getName(), sellerDecision.getContent());
			sellTraders.put(sellerDecision.getSender().getName(), sellerDecision.getSender());
			offersCnt++;
			if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
				// we received all decisions
				offersCnt = 0;
				return STEP_RECIVED_SELLERS_DECISIONS;
			} else {
				return STEP_SENT_CFP_TO_SELLERS;
			}
		} else {
			block(); //return STEP_BLOCK;
		}
		return STEP_SENT_CFP_TO_SELLERS;
	}

	private int sendConfirmation() {
		// received all decisions: sending confirmation
		// (accept/reject the offer)
		Map<String, Boolean> myDecisions = this.marketAgent.createFinalBuyingDecision(sellOffers);
		Iterator<String> decisionIterator = myDecisions.keySet().iterator();

		// send your decision to every seller
		while (decisionIterator.hasNext()) {
			String recipientName = decisionIterator.next();
			ACLMessage dec;
			if (myDecisions.get(recipientName) == true) {
				dec = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
				if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
					System.out.println(myAgent.getName() + " 6a) buy: accepted offer from " + recipientName);
			} else {
				dec = new ACLMessage(ACLMessage.REJECT_PROPOSAL);
				if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
					System.out.println(myAgent.getName() + " 6a) buy: rejected offer from " + recipientName);
				}
			}
			dec.addReceiver(sellTraders.get(recipientName));
			dec.setContent(sellOffers.get(recipientName));
			myAgent.send(dec);
		}
		offersCnt = 0;
		return STEP_SENDED_CONFIRMATION;
	}

	private int reciveConfirmationOfTransaction() {
		// receive confirmation of transaction and update supplies
		mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.CONFIRM),
				MessageTemplate.MatchPerformative(ACLMessage.REFUSE));
		msg = myAgent.receive(mt);
		if (msg != null) {
			if (msg.getPerformative() == ACLMessage.CONFIRM) {
				if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
					System.out.println(myAgent.getName() + " 8) buy: received confirmation from "
							+ msg.getSender().getName());
				}
				this.marketAgent.updateBuyerStore(msg.getSender().getName());

			}
			offersCnt++;
			if (offersCnt >= this.marketAgent.sellerAgentsList.size()) {
				return STEP_RECIVED_CONFIRMATION;
			}
		}
		return STEP_SENDED_CONFIRMATION;
	}

	private void endOfTransaction() {
		if (AgentsUtilities.DEBUG_ST_1) {
			System.out.println("buy: step = end");
		}
		step = 0;
		offersCnt = 0;
		sellOffers.clear();
		sellTraders.clear();
		block();
	}
	public void action() {
		switch (step) {
			case STEP_START_ACTION:
				step = getCurrentPricesFromSellers();
				break;
			case STEP_RECIVED_CURRENT_PRICES:
				step = sendCFPToSellers();
				break;
			case STEP_SENT_CFP_TO_SELLERS:
				step = reciveSellersDecision();
				break;
			case STEP_RECIVED_SELLERS_DECISIONS:
				step = sendConfirmation();
				break; // TODO: specjalnie nie by�o tu wcze�niej break?
			case STEP_SENDED_CONFIRMATION:
				step = reciveConfirmationOfTransaction();
				break;
			case STEP_RECIVED_CONFIRMATION:
				endOfTransaction();
				break;
			case STEP_BLOCK:
				block();
				break;
		}
	}

	@Override
	public void onStart() {
		sellOffers = new HashMap<String, String>();
		sellTraders = new HashMap<String, AID>();
	}

	@Override
	public boolean done() {
		return step == STEP_RECIVED_CONFIRMATION;
	}
}