package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.hamcrest.Matcher;
import org.junit.Before;
import org.junit.Test;

import agents.AgentOffer;

import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

import strategies.strategies.SimpleStrategy;

public class SimpleStrategyTest {
	private static final String offerAgentName = "name";
	SimpleStrategy ss = new SimpleStrategy();
	ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
	ArrayList<String> testNames = new ArrayList<String>();
	EnumMap<Products, Double> buy = new EnumMap<Products, Double>(Products.class);;
	
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
		ss.setMyMoney(100);
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
				assertTrue((Double)(howMuchHeNeeds) >= agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertTrue((Double)5.5 >= agentOffer.getItemPrice().get(Products.VEGETABLE));
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
	public void test_decideAboutSellOffer_should_remember_three_sell_history_entries_for_three_agents() {
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
		assertTrue(ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 1));
		assertTrue(ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 2));
		assertTrue(ss.getCurrentWeekBuyOffersHistory().keySet().contains(offerAgentName + 3));
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
	public void  test_composeFinalBuyingDecision_should_return_true_when_getting_cheaper_offer() {
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
	public void  test_composeFinalBuyingDecision_should_return_false_when_getting_worse_offer() {
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
		
		e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "VEGETABLE 1.0 5.5:FRUIT 2.0 3.9:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);
		
		assertEquals(3, result.size());
		assertEquals(true, result.get(offerAgentName + 1));
		assertEquals(true, result.get(offerAgentName + 3));
		assertEquals(false, result.get(offerAgentName + 2));
	} 
	
	@Test
	public void  test_composeFinalBuyingDecision_should_return_true_when_getting_same_price_more_stuff() {
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
		assertEquals(true, result.get(offerAgentName + 2));
	} 

	@Test
	public void test_decideAboutSellOffer_should_not_buy_more_than_he_can_afford() {
		double myMoney = 60;
		ss.setMyMoney(myMoney);
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 8.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName + 2)) {
				assertEquals(2, agentOffer.getItemAmount().size());
				assertEquals(2, agentOffer.getItemPrice().size());
				double totalPaid = agentOffer.getItemAmount().get(Products.VEGETABLE)
						* agentOffer.getItemPrice().get(Products.VEGETABLE)
						+ agentOffer.getItemAmount().get(Products.FRUIT)
						* agentOffer.getItemPrice().get(Products.FRUIT);
				assertTrue("buying " + agentOffer.getItemAmount().get(Products.VEGETABLE) + " vegetable for " + agentOffer.getItemPrice().get(Products.VEGETABLE)
						+ " and " + agentOffer.getItemAmount().get(Products.FRUIT) + " fruit for "+ agentOffer.getItemPrice().get(Products.FRUIT) + " paying total: " + totalPaid,
						 totalPaid <= myMoney);
				assertTrue(totalPaid >= 0);
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void  test_decideAboutSellOffer_should_remember_in_history_lower_amounts_when_he_doesnt_have_enough_money() {
		double myMoney = 60;
		ss.setMyMoney(myMoney);
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 8.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		
		ss.decideAboutSellOffer(firstStageSellOffers);
		ArrayList<AgentOffer> bestOffers = ss.getCurrentWeekBuyOffersHistory().get(offerAgentName + 2);
		assertTrue("he remembered: " + bestOffers.get(0).getItemAmount().get(Products.VEGETABLE), bestOffers.get(0).getItemAmount().get(Products.VEGETABLE) <= 8.28);
		assertTrue("he remembered: " + bestOffers.get(0).getItemAmount().get(Products.FRUIT), bestOffers.get(0).getItemAmount().get(Products.FRUIT) <= 4.84);
	} 
	
	@Test
	public void  test_composeFinalBuyingDecision_should_agree_for_lower_offers_when_he_reduced_amount_of_stuff() {
		double myMoney = 60;
		ss.setMyMoney(myMoney);
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 8.0);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		
		ss.decideAboutSellOffer(firstStageSellOffers);
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(offerAgentName + 2, sellOfferPrefix + "VEGETABLE 8.28 5.5:FRUIT 4.84 3.0:");
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
	public void  test_composeFinalBuyingDecision_should_update_store_money_correctly() {
		double myMoney = 61;
		ss.setMyMoney(myMoney);
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 8.0);
		EnumMap<Products, Double> have = new EnumMap<Products, Double>(Products.class);
		have.put(Products.VEGETABLE, 3.0);
		have.put(Products.FRUIT, 1.2);
		ss.setHave(have);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		String bestTraderName = offerAgentName + 2;
		AgentOffer e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		double totalPaid = 0.0;
		for (AgentOffer agentOffer : answers) {
			if (!agentOffer.getAgentName().equals(bestTraderName))
				continue;
			totalPaid += agentOffer.getItemAmount().get(Products.VEGETABLE)
					* agentOffer.getItemPrice().get(Products.VEGETABLE)
					+ agentOffer.getItemAmount().get(Products.FRUIT)
					* agentOffer.getItemPrice().get(Products.FRUIT);
		}
		
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "VEGETABLE 8.28 5.5:FRUIT 4.84 3.0:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);

		ss.updateBuyerStore(bestTraderName);
		assertEquals(myMoney - totalPaid, ss.getMyMoney(), 0.01);
	} 
	
	@Test
	public void  test_composeFinalBuyingDecision_should_update_store_buy_and_have_map_correctly() {
		SimpleStrategy strag = new SimpleStrategy();
		double myMoney = 61;
		strag.setMyMoney(myMoney);
		buy.put(Products.VEGETABLE, 20.0);
		buy.put(Products.FRUIT, 8.0);
		strag.setBuy(buy);
		EnumMap<Products, Double> have = new EnumMap<Products, Double>(Products.class);
		have.put(Products.VEGETABLE, 3.0);
		have.put(Products.FRUIT, 1.2);
		strag.setHave(have);
		
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		String bestTraderName = offerAgentName + 2;
		AgentOffer e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		AgentOffer e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = strag.decideAboutSellOffer(firstStageSellOffers);
		double totalPaid = 0.0;
		for (AgentOffer agentOffer : answers) {
			if (!agentOffer.getAgentName().equals(bestTraderName))
				continue;
			totalPaid += agentOffer.getItemAmount().get(Products.VEGETABLE)
					* agentOffer.getItemPrice().get(Products.VEGETABLE)
					+ agentOffer.getItemAmount().get(Products.FRUIT)
					* agentOffer.getItemPrice().get(Products.FRUIT);
		}
		
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(offerAgentName + 1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "VEGETABLE 8.4 5.5:FRUIT 5.1 3.0:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(offerAgentName + 3, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = strag.composeFinalBuyingDecision(secondStageSellOffers);

		strag.updateBuyerStore(bestTraderName);
		assertEquals(20.0 - 8.4, strag.getBuy().get(Products.VEGETABLE), 0.1);
		assertEquals(8.0 - 5.1, strag.getBuy().get(Products.FRUIT), 0.1);
		assertEquals(3.0 + 8.4, strag.getHave().get(Products.VEGETABLE), 0.1);
		assertEquals(1.2 + 5.1, strag.getHave().get(Products.FRUIT), 0.1);
	} 

}
