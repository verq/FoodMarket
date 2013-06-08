package utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import constants.OfferFormatUtilities;

import agents.AgentOffer;

public class AgentsUtilities {
	public static final boolean DEBUG_ST_1 = false;
	public static final boolean PRINT_COMMUNICATION_STAGE = true;
	protected static Random rand = new Random();

	public static int randomInt(int min, int max) {
		return rand.nextInt(max - min + 1) + min;
	}

	public static double randomDouble(double min, double max) {
		return (max - min) * rand.nextDouble() + min;
	}

	/**
	 * 
	 * @param offers
	 *            sent as String from remote agents and these agents' names
	 * @return list of offers converted to easily accesible format as
	 *         {@link AgentOffer}
	 */
	public static ArrayList<AgentOffer> createListOfOffers(
			Map<String, String> offers) {
		ArrayList<AgentOffer> agentsOffers = new ArrayList<AgentOffer>();
		try {
			Iterator<String> offerIterator = offers.keySet().iterator();
			while (offerIterator.hasNext()) {
				String name = offerIterator.next();
				AgentOffer agOf = new AgentOffer(name, offers.get(name));
				agentsOffers.add(agOf);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return agentsOffers;
	}

	/**
	 * 
	 * @param agents
	 *            response for offers (depending on agent's strategy) as list of
	 *            {@link AgentOffer} items
	 * @return map containing name of agent to answer and what should be send to
	 *         him
	 */
	public static Map<String, String> createMapOfOffers(
			ArrayList<AgentOffer> response) {
		Map<String, String> map = new HashMap<String, String>();
		try {
			for (AgentOffer agentOffer : response) {
				map.put(agentOffer.getAgentName(),
						OfferFormatUtilities.composeOfferContent(
								agentOffer.getItemAmount(),
								agentOffer.getItemPrice(),
								agentOffer.getAgentType(),
								agentOffer.getOfferType()));
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return map;
	}
}
