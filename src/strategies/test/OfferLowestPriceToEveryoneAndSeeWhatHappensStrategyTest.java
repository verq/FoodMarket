package test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

import strategies.strategies.OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy;
import strategies.strategies.TakeCheapestThenTakeNextCheapestStrategy;
import agents.AgentOffer;
import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public class OfferLowestPriceToEveryoneAndSeeWhatHappensStrategyTest {
	
	String sellOfferPrefix = Participants.BAKER
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER
			+ OfferFormatUtilities.SELL_OFFER_TAG
			+ OfferFormatUtilities.OFFER_FIELD_DELIMITER;
	
	@Before
	public void setUp() throws Exception {
	}
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
	
	public OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy createSampleStrategy(double money) {
		OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy tt = new OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy();
		tt.setBuy(createSampleBuy());
		tt.setHave(createSampleHave());
		tt.setMyMoney(money);
		return tt;
	}
	
	public OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy createSampleStrategy() {
		return createSampleStrategy(100.0);
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_same_price_for_all_offers_for_single_category() {
		OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy tt = createSampleStrategy(1000.0);
			ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
			tt.getBuy().put(Products.VEGETABLE, 20.0);
			String expensiveTraderName = "EXPENSIVE";
			AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:");
			firstStageSellOffers.add(e1);
			
			String cheapTraderName = "CHEAP";
			AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "VEGETABLE 10.0 5.5:");
			firstStageSellOffers.add(e2);
			
			String middleTraderName = "MIDDLE";
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
					assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
				}
				else if(agentOffer.getAgentName().equals(expensiveTraderName)) {
					assertEquals(1, agentOffer.getItemAmount().size());
					assertEquals(1, agentOffer.getItemPrice().size());
					assertEquals((Double)1.0, agentOffer.getItemAmount().get(Products.VEGETABLE));
					assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
				}
				else {
					assertEquals("wrong answer for agent: " + agentOffer.getAgentName(), 0, agentOffer.getItemAmount().size());
					assertEquals(0, agentOffer.getItemPrice().size());
				}
			}
	}
	
	@Test
	public void test_decideAboutSellOffer_should_return_same_price_for_all_offers_for_single_category_with_limited_money() {
		OfferLowestPriceToEveryoneAndSeeWhatHappensStrategy tt = createSampleStrategy(100.0);
			ArrayList<AgentOffer> firstStageSellOffers = new ArrayList<AgentOffer>();
			tt.getBuy().put(Products.VEGETABLE, 20.0);
			String expensiveTraderName = "EXPENSIVE";
			AgentOffer e1 = new AgentOffer(expensiveTraderName, sellOfferPrefix + "VEGETABLE 12.0 14.0:");
			firstStageSellOffers.add(e1);
			
			String cheapTraderName = "CHEAP";
			AgentOffer e2 = new AgentOffer(cheapTraderName, sellOfferPrefix + "VEGETABLE 10.0 5.5:");
			firstStageSellOffers.add(e2);
			
			String middleTraderName = "MIDDLE";
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
					assertEquals((Double)8.18, agentOffer.getItemAmount().get(Products.VEGETABLE).doubleValue(), 0.01);
					assertEquals((Double)5.5, agentOffer.getItemPrice().get(Products.VEGETABLE));
				}
				else {
					assertEquals("wrong answer for agent: " + agentOffer.getAgentName(), 0, agentOffer.getItemAmount().size());
					assertEquals(0, agentOffer.getItemPrice().size());
				}
			}
	}

}
