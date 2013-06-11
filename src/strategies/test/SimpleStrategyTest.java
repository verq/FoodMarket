package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import agents.AgentOffer;

import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

import strategies.SimpleStrategy;

public class SimpleStrategyTest {
	private static final String offerAgentName = "name";
	SimpleStrategy ss = new SimpleStrategy();
	ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
	ArrayList<String> testNames = new ArrayList<String>();
	EnumMap<Products, Double> buy;
	
	String sellOfferPrefix = Participants.BAKER
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER
			+ OfferFormatUtilities.SELL_OFFER_TAG
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER;
	String buyOfferPrefix = Participants.BAKER
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER
			+ OfferFormatUtilities.BUY_OFFER_TAG
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER;
	
	String sampleBuyOffer = buyOfferPrefix
			+ "VEGETABLE 12.0 4.0:FRUIT 1.0 5.5:MILK 9.0 8.9:";
	String sampleSellOffer = sellOfferPrefix
			+ "MANURE 12.0 4.0:FRUIT 1.0 5.5:MILK 9.0 8.9:";
	@Before
	public void setUp() throws Exception {
		ss.setMyMoney(100.0);
		buy = new EnumMap<Products, Double>(Products.class);
		buy.put(Products.ANIMAL, 12.0);
		ss.setBuy(buy);
		for (int i = 0; i < 10; i++) {
			String agentName = "ag" + Integer.toString(i);
			AgentOffer e = new AgentOffer(agentName, null);
			firstStageSellOffers.add(e);
			testNames.add(agentName);
		}
	}

	@Test
	public void test_decideAboutSellOffer_should_return_empty_array_for_zero_offers() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		assertEquals(0, ss.decideAboutSellOffer(firstStageSellOffers).size());
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_single_offer_for_one_offer_entry() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e = new AgentOffer(offerAgentName, sellOfferPrefix);
		firstStageSellOffers.add(e);
		assertEquals(1, ss.decideAboutSellOffer(firstStageSellOffers).size());
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_correct_name_for_every_offering_agent() {
		ArrayList<String> answer = new ArrayList<String>();
		for (AgentOffer of : ss.decideAboutSellOffer(firstStageSellOffers)) {
			answer.add(of.getAgentName());
		}
		assertEquals(testNames, answer);
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_empty_answer_for_not_interesting_offer() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e = new AgentOffer(offerAgentName, sampleSellOffer);
		firstStageSellOffers.add(e);
		for (AgentOffer of : ss.decideAboutSellOffer(firstStageSellOffers)) {
			assertEquals(0, of.getItemAmount().size());
			assertEquals(0, of.getItemPrice().size());
		}
	}
	
	@Test
	public void test_decideAboutSellOffer_should_not_respond_to_buy_offers() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e = new AgentOffer(offerAgentName, sampleBuyOffer);
		firstStageSellOffers.add(e);
		for (AgentOffer of : ss.decideAboutSellOffer(firstStageSellOffers)) {
			assertEquals(0, of.getItemAmount().size());
			assertEquals(0, of.getItemPrice().size());
		}
	}
	/*
	@Test
	public void test_decideAboutSellOffer_should_respond_to_sell_offers_only() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e = new AgentOffer(offerAgentName, sellOfferPrefix + "VEGETABLE 12.0 4.0:FRUIT 1.0 5.5:MILK 9.0 8.9:");
		firstStageSellOffers.add(e);
		for (AgentOffer of : ss.decideAboutSellOffer(firstStageSellOffers)) {
			assertEquals(1, of.getItemAmount().size());
			assertEquals(1, of.getItemPrice().size());
		}
	}*/
	
	@Test
	public void test_decideAboutSellOffer_should_return_cheapest_offer_for_single_product_kind() {
		buy.put(Products.VEGETABLE, 20.0);

		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 5.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName + 2)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_not_bigger_amount_than_he_needs() {
		double howMuchHeNeeds = 20.0;
		buy.put(Products.VEGETABLE, howMuchHeNeeds);

		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 5.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE " + (howMuchHeNeeds + 1.0) + " 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName + 2)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)(howMuchHeNeeds), agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_cheapest_offer_for_multiple_product_kinds_from_different_agents() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName + 2)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else if(agentOffer.getAgentName().equals(offerAgentName + 1)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.FRUIT));
				assertEquals((Double)3.5, agentOffer.getItemPrice().get(Products.FRUIT));
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}

	@Test
	public void test_decideAboutSellOffer_should_return_cheapest_offer_for_multiple_product_kinds_from_one_agent() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName + 2)) {
				assertEquals(2, agentOffer.getItemAmount().size());
				assertEquals(2, agentOffer.getItemPrice().size());
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
				assertEquals((Double)2.0, agentOffer.getItemAmount().get(Products.FRUIT));
				assertEquals((Double)2.9, agentOffer.getItemPrice().get(Products.FRUIT));
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void test_decideAboutSellOffer_should_remember_three_sell_history_entries() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, ss.getCurrentWeekBuyOffersHistory().size());
	}
	
	@Test
	public void test_decideAboutSellOffer_should_remember_all_traders() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, ss.getCurrentWeekBuyOffersHistory().size());
		ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 1);
		ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 2);
		ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 3);
	} 
	
	@Test
	public void test_composeFinalBuyingDecision_should_return_false_for_unknown_offers() {
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(firstStageSellOffers);
		assertEquals(3, result.size());
		for (boolean decision : result.values()) {
			assertEquals(false, decision);
		}
	}
	
	@Test
	public void  test_composeFinalBuyingDecision_should_return_true_for_exactly_the_same_offers() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		
		ss.decideAboutSellOffer(firstStageSellOffers);
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "VEGETABLE 1.0 5.5:FRUIT 2.0 2.9:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);
		
		assertEquals(3, result.size());
		assertEquals(true, result.get(offerAgentName + 1));
		assertEquals(true, result.get(offerAgentName + 3));
		assertEquals(true, result.get(offerAgentName + 2));

	} 

	@Test
	public void  test_composeFinalBuyingDecision_should_return_true_when_ggetting_cheaper_offer() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		
		ss.decideAboutSellOffer(firstStageSellOffers);
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "VEGETABLE 1.0 5.5:FRUIT 2.0 1.9:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);
		
		assertEquals(3, result.size());
		assertEquals(true, result.get(offerAgentName + 1));
		assertEquals(true, result.get(offerAgentName + 3));
		assertEquals(true, result.get(offerAgentName + 2));

	} 
	
	@Test
	public void  test_composeFinalBuyingDecision_should_return_false_when_getting_different_offer() {
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 2.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 1.0 5.5:FRUIT 9.0 2.9:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		
		ss.decideAboutSellOffer(firstStageSellOffers);
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "VEGETABLE 1.0 5.5:FRUIT 3.0 2.9:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);
		
		assertEquals(3, result.size());
		assertEquals(true, result.get(offerAgentName + 1));
		assertEquals(true, result.get(offerAgentName + 3));
		assertEquals(false, result.get(offerAgentName + 2));

	} 


}
