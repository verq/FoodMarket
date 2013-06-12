package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import strategies.strategies.SimpleStrategy;
import strategies.strategies.TakeCheapestThenTakeNextCheapestStrategy;
import agents.AgentOffer;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public class TakeNextCheapestStrategyTest {
	private static final String offerAgentName = "name";
	private double money = 1000.0;
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
	
	public EnumMap<Products, Double> createSampleBuy() {
		EnumMap<Products, Double> tBuy = new EnumMap<Products, Double>(Products.class);
		tBuy.put(Products.VEGETABLE, 20.0);
		tBuy.put(Products.FRUIT, 8.0);
		return tBuy;
	}

	public EnumMap<Products, Double> createSampleHave() {
		EnumMap<Products, Double> tHave = new EnumMap<Products, Double>(Products.class);
		tHave.put(Products.VEGETABLE, 10.0);
		tHave.put(Products.FRUIT, 5.0);
		return tHave;
	}
	
	public TakeCheapestThenTakeNextCheapestStrategy createSampleStrategy() {
		TakeCheapestThenTakeNextCheapestStrategy tt = new TakeCheapestThenTakeNextCheapestStrategy();
		tt.setBuy(createSampleBuy());
		tt.setHave(createSampleHave());
		tt.setMyMoney(money);
		return tt;
	}
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testDecideAboutSellOffer_should_choose_cheapest_offer_from_one_seller_if_he_has_enough_stuff() {
		TakeCheapestThenTakeNextCheapestStrategy tt = createSampleStrategy();
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		AgentOffer e = new AgentOffer(offerAgentName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 30.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e);

		ArrayList<AgentOffer> answers = tt.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(1, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(offerAgentName)) {
				assertEquals(2, agentOffer.getItemAmount().size());
				assertEquals(2, agentOffer.getItemPrice().size());
				assertEquals((Double)20.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else {
				assertEquals(0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}

	@Test
	public void testDecideAboutSellOffer_should_buy_everything_form_cheapest_seller_and_rest_from_second_cheapest_single_product_case() {
		TakeCheapestThenTakeNextCheapestStrategy tt = createSampleStrategy();
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		tt.getBuy().put(Products.VEGETABLE, 20.0);
		String expensiveTraderName = offerAgentName + 1;
		AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:");
		firstStageSellOffers.add(e1);
		
		String cheapTraderName = offerAgentName + 2;
		AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "VEGETABLE 10.0 5.5:");
		firstStageSellOffers.add(e2);
		
		String middleTraderName = offerAgentName + 3;
		AgentOffer e3 = new AgentOffer(middleTraderName, sellOfferPrefix + "VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		AgentOffer e4 = new AgentOffer(middleTraderName + 6, sellOfferPrefix + "MANURE 9.0 8.9:");
		firstStageSellOffers.add(e4);

		ArrayList<AgentOffer> answers = tt.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(4, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(cheapTraderName)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)10.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else if(agentOffer.getAgentName().equals(middleTraderName)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)9.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)8.9, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else if(agentOffer.getAgentName().equals(expensiveTraderName)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)14.0, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else {
				assertEquals("wrong answer for agent: " + agentOffer.getAgentName(), 0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void testDecideAboutSellOffer_should_buy_everything_form_cheapest_seller_and_rest_from_second_cheapest_multiple_product_case() {
		TakeCheapestThenTakeNextCheapestStrategy tt = createSampleStrategy();
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		tt.getBuy().put(Products.VEGETABLE, 20.0);
		tt.getBuy().put(Products.FRUIT, 8.0);
		String expensiveTraderName = "EXPESNIVE Agent";
		String middleTraderName = "MIDDLE Agent";
		String cheapTraderName = "CHEAP Agent";

		AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 5.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(middleTraderName, sellOfferPrefix + "FRUIT 7.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = tt.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		for (AgentOffer agentOffer : answers) {
			if(agentOffer.getAgentName().equals(cheapTraderName)) {
				assertEquals(1, agentOffer.getItemAmount().size());
				assertEquals(1, agentOffer.getItemPrice().size());
				
				assertEquals((Double)10.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
			}
			else if(agentOffer.getAgentName().equals(middleTraderName)) {
				assertEquals(2, agentOffer.getItemAmount().size());
				assertEquals(2, agentOffer.getItemPrice().size());
				
				assertEquals((Double)9.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)8.9, agentOffer.getItemPrice().get(Products.VEGETABLE));
				
				assertEquals((Double)7.0, agentOffer.getItemAmount().get(Products.FRUIT));
				assertEquals((Double)4.0, agentOffer.getItemPrice().get(Products.FRUIT));
			}
			else if(agentOffer.getAgentName().equals(expensiveTraderName)) {
				assertEquals(2, agentOffer.getItemAmount().size());
				assertEquals(2, agentOffer.getItemPrice().size());
				
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
				assertEquals((Double)14.0, agentOffer.getItemPrice().get(Products.VEGETABLE));
				
				assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.FRUIT));
				assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.FRUIT));
			}
			else {
				assertEquals("wrong answer for agent: " + agentOffer.getAgentName(), 0, agentOffer.getItemAmount().size());
				assertEquals(0, agentOffer.getItemPrice().size());
			}
		}
	}
	
	@Test
	public void testDecideAboutSellOffer_should_buy_less_if_he_doesnt_have_enough_money_multiple_product_case() {
		TakeCheapestThenTakeNextCheapestStrategy tt = createSampleStrategy();
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		tt.getBuy().put(Products.VEGETABLE, 20.0);
		tt.getBuy().put(Products.FRUIT, 8.0);
		double myMoney = 60.0;
		tt.setMyMoney(myMoney);
		String expensiveTraderName = "EXPESNIVE Agent";
		String middleTraderName = "MIDDLE Agent";
		String cheapTraderName = "CHEAP Agent";

		AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 5.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(middleTraderName, sellOfferPrefix + "FRUIT 7.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = tt.decideAboutSellOffer(firstStageSellOffers);
		assertEquals(3, answers.size());
		double totalPaid = 0;
		for (AgentOffer agentOffer : answers) {
			if (agentOffer.getItemAmount().containsKey(Products.VEGETABLE))
			totalPaid += agentOffer.getItemAmount().get(Products.VEGETABLE)
					* agentOffer.getItemPrice().get(Products.VEGETABLE);
			if (agentOffer.getItemAmount().containsKey(Products.FRUIT))
			totalPaid += agentOffer.getItemAmount().get(Products.FRUIT)
					* agentOffer.getItemPrice().get(Products.FRUIT);
		}
		assertTrue(
				"paid " + totalPaid + " but had only " + myMoney,
				totalPaid <= myMoney);
		assertTrue(totalPaid >= 0);
		assertEquals(myMoney, totalPaid, 0.01);
	}
	
	@Test
	public void test_decideAboutSellOffer_should_remember_all_traders() {
		TakeCheapestThenTakeNextCheapestStrategy tt = createSampleStrategy();
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		tt.getBuy().put(Products.VEGETABLE, 20.0);
		tt.getBuy().put(Products.FRUIT, 8.0);
		double myMoney = 60.0;
		tt.setMyMoney(myMoney);
		String expensiveTraderName = "EXPESNIVE Agent";
		String middleTraderName = "MIDDLE Agent";
		String cheapTraderName = "CHEAP Agent";

		AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 1.0 5.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 8.9:");
		firstStageSellOffers.add(e2);
		
		AgentOffer e3 = new AgentOffer(middleTraderName, sellOfferPrefix + "FRUIT 7.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = tt.decideAboutSellOffer(firstStageSellOffers);
		
		assertEquals(3, tt.getCurrentWeekBuyOffersHistory().size());
		assertTrue(tt.getCurrentWeekBuyOffersHistory().keySet().contains(expensiveTraderName));
		assertTrue(tt.getCurrentWeekBuyOffersHistory().keySet().contains(middleTraderName));
		assertTrue(tt.getCurrentWeekBuyOffersHistory().keySet().contains(cheapTraderName));
	} 
	
	@Test
	public void  test_composeFinalBuyingDecision_should_update_store_money_correctly() {
		TakeCheapestThenTakeNextCheapestStrategy ss = createSampleStrategy();
		double myMoney = 61;
		ss.setMyMoney(myMoney);
		ss.getBuy().put(Products.VEGETABLE, 20.0);
		ss.getBuy().put(Products.FRUIT, 8.0);
		EnumMap<Products, Double> have = new EnumMap<Products, Double>(Products.class);
		have.put(Products.VEGETABLE, 3.0);
		have.put(Products.FRUIT, 1.2);
		ss.setHave(have);
		ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
		String traderName1 = offerAgentName + 1;
		AgentOffer e1 = new AgentOffer(traderName1, sellOfferPrefix + "VEGETABLE 12.0 14.0:FRUIT 10.0 3.5:MANURE 9.0 8.9:");
		firstStageSellOffers.add(e1);
		
		String bestTraderName = "best trader";
		AgentOffer e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "MANURE 12.0 4.0:VEGETABLE 10.0 5.5:FRUIT 9.0 3.0:");
		firstStageSellOffers.add(e2); // the best one
		
		String traderName2 = offerAgentName + 3;
		AgentOffer e3 = new AgentOffer(traderName2, sellOfferPrefix + "FRUIT 12.0 4.0:MANURE 1.0 5.5:VEGETABLE 9.0 8.9:");
		firstStageSellOffers.add(e3);
		ArrayList<AgentOffer> answers = ss.decideAboutSellOffer(firstStageSellOffers);
		double totalPaid = 0.0;
		
		ArrayList<AgentOffer> secondStageSellOffers = new ArrayList<AgentOffer>();
		e1 = new AgentOffer(traderName1, sellOfferPrefix + "");
		secondStageSellOffers.add(e1);
		
		e2 = new AgentOffer(bestTraderName, sellOfferPrefix + "VEGETABLE 8.28 5.5:FRUIT 4.84 3.0:");
		secondStageSellOffers.add(e2); // the best one
		
		e3 = new AgentOffer(traderName2, sellOfferPrefix + "");
		secondStageSellOffers.add(e3);
		Map<String, Boolean> result = ss.composeFinalBuyingDecision(secondStageSellOffers);

		assertEquals("accepted bestSeller worse offer!", false, result.get(bestTraderName));
		assertEquals("accepted empty offer 1", false, result.get(traderName1));
		assertEquals("accepted empty offer 2", false, result.get(traderName2));
	
		ss.updateBuyerStore(bestTraderName);
		Map<String, ArrayList<AgentOffer>> currentWeekBuyOffersHistory = ss.getCurrentWeekBuyOffersHistory();
		for (Iterator<String> iterator = currentWeekBuyOffersHistory.keySet().iterator(); iterator
				.hasNext();) {
			String traderName = iterator.next();
			ArrayList<AgentOffer> agentOfferList = currentWeekBuyOffersHistory.get(traderName);
			for (AgentOffer agentOffer : agentOfferList) {
				if(agentOffer.getAgentName().equals(bestTraderName)) {
					
				}
				if (agentOffer.getItemAmount().containsKey(Products.VEGETABLE))
					totalPaid += agentOffer.getItemAmount().get(
							Products.VEGETABLE)
							* agentOffer.getItemPrice().get(Products.VEGETABLE);

				if (agentOffer.getItemAmount().containsKey(Products.FRUIT))
					totalPaid += agentOffer.getItemAmount().get(Products.FRUIT)
							* agentOffer.getItemPrice().get(Products.FRUIT);
			}
		}
		assertEquals(myMoney - totalPaid, ss.getMyMoney(), 0.01);
	} 
	/*
	@Test
	public void testComposeFinalBuyingDecision() {
		fail("Not yet implemented");
	}

	@Test
	public void testUpdateBuyerStore() {
		fail("Not yet implemented");
	}*/

}
