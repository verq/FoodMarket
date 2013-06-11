package test;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import constants.Participants;
import constants.Products;

import agents.AgentOffer;

public class AgentOfferTest {
	AgentOffer ao;
	String name = "dupa";
	String offerType = "buy";
	Participants agentType = Participants.CLIENT;
	Map<Products, Double> itemPrice;
	Map<Products, Double> itemAmount;
	
	@Before
	public void setUp() throws Exception {
		ao = new AgentOffer();
		itemPrice = new HashMap<Products, Double>();
		itemAmount = new HashMap<Products, Double>();

		itemPrice.put(Products.VEGETABLE, 12.0);
		itemPrice.put(Products.ANIMAL, 42.21);
		itemPrice.put(Products.FRUIT, 0.4);

		itemAmount.put(Products.VEGETABLE, 3.4);
		itemAmount.put(Products.ANIMAL, 5.0);
		itemAmount.put(Products.FRUIT, 2.0);
	}

	private String composeOfferContent() {
		if (itemPrice == null || itemPrice.size() == 0 | itemAmount == null || itemAmount.size() == 0)
			return "";
		String content = agentType + ";" + offerType + ";";
		Iterator<Products> priceIterator = itemPrice.keySet().iterator();

		boolean nonzero = false;
		while (priceIterator.hasNext()) {
			Products p = priceIterator.next();
			if (itemAmount.get(p) > 0.0) {
				content += p + " " + itemAmount.get(p) + " " + itemPrice.get(p)
						+ ":";
				nonzero = true;
			}
		}
		if (!nonzero)
			return "";
		return content;
	}
	@Test
	public void testparseIncommingOffer_should_return_correct_agent_name() {
		ao.parseIncommingOffer(name, "");
		assertEquals(name, ao.getAgentName());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_correct_offer_type() {
		ao.parseIncommingOffer(name, agentType + ";" + offerType + ";");
		assertEquals(offerType, ao.getOfferType());
	}

	@Test
	public void testparseIncommingOffer_should_return_correct_agent_type() {
		ao.parseIncommingOffer(name, agentType + ";" + offerType + ";");
		assertEquals(agentType, ao.getAgentType());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_empty_item_price_list_for_empty_offer() {
		Map<String, Double> itemPrice = new HashMap<String, Double>();
		ao.parseIncommingOffer(name, agentType + ";" + offerType + ";");
		assertEquals(itemPrice, ao.getItemPrice());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_empty_item_amount_list_for_empty_offer() {
		Map<String, Double> itemAmount = new HashMap<String, Double>();
		ao.parseIncommingOffer(name, agentType + ";" + offerType + ";");
		assertEquals(itemAmount, ao.getItemAmount());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_correct_item_list_from_itemPrice() {
		ao.parseIncommingOffer(name, composeOfferContent());
		assertEquals(itemPrice.keySet(), ao.getItemPrice().keySet());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_correct_item_list_from_itemAmount() {
		ao.parseIncommingOffer(name, composeOfferContent());
		assertEquals(itemAmount.keySet(), ao.getItemAmount().keySet());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_correct_values_from_itemAmount() {
		ao.parseIncommingOffer(name, composeOfferContent());
		assertEquals(itemAmount, ao.getItemAmount());
	}
	
	@Test
	public void testparseIncommingOffer_should_return_correct_values_from_itemPrice() {
		ao.parseIncommingOffer(name, composeOfferContent());
		assertEquals(itemPrice, ao.getItemPrice());
	}
}
