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
 * main inner class responsible for selling actions, more info within {@link SellRequestPerformer.action} method
 * 
 */
// TODO: perhaps extract SellRequestPerformer to another file

class SellRequestPerformer extends Behaviour {
	/**
	 * 
	 */
	private final MarketAgent marketAgent;

	/**
	 * @param marketAgent
	 */
	SellRequestPerformer(MarketAgent marketAgent) {
		this.marketAgent = marketAgent;
	}

	private final int STEP_START_ACTION = 0;
	private final int STEP_SENT_PRICE_TO_INTERESTED_BUYERS = 1;
	private final int STEP_RECIVED_PROPOSALS_AND_REFUSALS_FROM_BUYERS = 2;
	private final int STEP_MADE_DECISION_ABOUT_SELLING = 3;
	private final int STEP_GOT_CONFIRMATION_FROM_BUYERS = 4;
	private final int STEP_BLOCK = 5;

	private int repliesCnt = 0; // The counter of replies from seller agents
	private MessageTemplate mt; // The template to receive replies
	private int step = 0;
	private Map<String, String> buyOffers; // seller name : content
	private Map<String, AID> buyTraders; // seller name : where to send
											// reply

	private int sendPriceToInterestedBuyers(String conversationID) {
		ACLMessage offer_inform = new ACLMessage(ACLMessage.INFORM);
		for (int i = 0; i < this.marketAgent.buyerAgentsList.size(); ++i) {
			if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
				System.out.println(myAgent.getName()
						+ " 1) sell: sending sell offer to: "
						+ this.marketAgent.buyerAgentsList.get(i).getName());
			offer_inform.addReceiver(this.marketAgent.buyerAgentsList.get(i));
		}
		offer_inform.setContent(this.marketAgent.composeSellContent());
		offer_inform.setConversationId(conversationID);
		offer_inform.setReplyWith(conversationID
				+ System.currentTimeMillis());
		myAgent.send(offer_inform);
		return STEP_SENT_PRICE_TO_INTERESTED_BUYERS;
	}

	private int reciveProposalsAndRefusalsFromBuyers() {
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
			buyOffers.put(reply.getSender().getName(), reply.getContent());
			buyTraders.put(reply.getSender().getName(), reply.getSender());
			repliesCnt++;
			if (repliesCnt >= this.marketAgent.buyerAgentsList.size()) {
				// We received all CFPs
				return STEP_RECIVED_PROPOSALS_AND_REFUSALS_FROM_BUYERS;
			}
		} else {
			return STEP_BLOCK;
		}
		return STEP_SENT_PRICE_TO_INTERESTED_BUYERS;
	}

	private int makeDecisionAboutSelling() {
		Map<String, String> responsesToSend = this.marketAgent.createAnswerToBuyOffer(buyOffers);
		// Respond to buyers with your decision
		if (AgentsUtilities.PRINT_COMMUNICATION_STAGE) {
			System.out
					.println(myAgent.getName()
							+ " 5) sell: got all offers - responding with decision");
		}
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
			cfp.setContent(this.marketAgent.composeSellContent());
			cfp.setConversationId("propose" + this.marketAgent.myType);
			cfp.setReplyWith("propose" + System.currentTimeMillis());
			myAgent.send(cfp);
		}
		return STEP_MADE_DECISION_ABOUT_SELLING;
	}

	private int getConfirmationFromBuyers() {
		// confirm back
		mt = MessageTemplate.or(MessageTemplate
				.MatchPerformative(ACLMessage.ACCEPT_PROPOSAL),
				MessageTemplate
						.MatchPerformative(ACLMessage.REJECT_PROPOSAL));
		ACLMessage msg = myAgent.receive(mt);
		if (msg != null) {
			if (AgentsUtilities.PRINT_COMMUNICATION_STAGE)
				System.out.println(myAgent.getName()
						+ " 7) sell: got accepted confirmation from "
						+ msg.getSender().getName());
			ACLMessage confirm = msg.createReply();
			boolean confirmed = this.marketAgent.confirmSellTransactionWith(msg.getSender()
					.getName());
			if (confirmed) {
				confirm.setPerformative(ACLMessage.CONFIRM);
			} else {
				confirm.setPerformative(ACLMessage.REFUSE);
			}
			confirm.setReplyWith("cfp" + System.currentTimeMillis());
			myAgent.send(confirm);
			repliesCnt++;
			if (repliesCnt >= this.marketAgent.buyerAgentsList.size()) {
				return STEP_GOT_CONFIRMATION_FROM_BUYERS;
			}
		} else {
			return STEP_BLOCK;
		}
		return STEP_MADE_DECISION_ABOUT_SELLING;
	}

	public void action() {
		String conversationID = this.marketAgent.myType + "=inform";
		try {
			switch (step) {
			case STEP_START_ACTION:
				buyOffers.clear();
				buyTraders.clear();
				step = sendPriceToInterestedBuyers(conversationID);
				break;
			case STEP_SENT_PRICE_TO_INTERESTED_BUYERS:
				step = reciveProposalsAndRefusalsFromBuyers();
				break;
			case STEP_RECIVED_PROPOSALS_AND_REFUSALS_FROM_BUYERS:
				repliesCnt = 0;
				step = makeDecisionAboutSelling();
				break;
			case STEP_MADE_DECISION_ABOUT_SELLING:
				step = getConfirmationFromBuyers();
				break;
			case STEP_GOT_CONFIRMATION_FROM_BUYERS:
				if (AgentsUtilities.DEBUG_ST_1) {
					System.out.println("sell: step = end");
				}
				step = 0;
				repliesCnt = 0;
				buyOffers.clear();
				buyTraders.clear();
				block();
				break;
			case STEP_BLOCK:
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