package constants;

import static org.junit.Assert.assertEquals;

import java.util.EnumMap;

import org.junit.Before;
import org.junit.Test;

public class OfferFormatUtilitiesTest {
	EnumMap<Products, Double> itemAmount;
	EnumMap<Products, Double> pricePerItem;
	Participants agentType;
	String offerType;

	@Before
	public void setUp() throws Exception {
		itemAmount = new EnumMap<Products, Double>(Products.class);
		pricePerItem = new EnumMap<Products, Double>(Products.class);

		itemAmount.put(Products.MANURE, 12.4);
		itemAmount.put(Products.ANIMAL, 34.0);
		itemAmount.put(Products.FRUIT, 15.0);

		pricePerItem.put(Products.MANURE, 1.0);
		pricePerItem.put(Products.ANIMAL, 567.0);
		pricePerItem.put(Products.FRUIT, 3456.0);

		agentType = Participants.BAKER;
		offerType = OfferFormatUtilities.BUY_OFFER_TAG;
	}

	@Test
	public void testComposeOfferContent_should_return_empty_offer_for_empty_offer_maps() {
		EnumMap<Products, Double> itemAmount = new EnumMap<Products, Double>(
				Products.class);
		EnumMap<Products, Double> pricePerItem = new EnumMap<Products, Double>(
				Products.class);
		String result = OfferFormatUtilities.composeOfferContent(itemAmount,
				pricePerItem, agentType, offerType);
		assertEquals("", result);
	}

	@Test
	public void testComposeOfferContent_should_return_empty_offer_for_null_offer_maps() {
		String result = OfferFormatUtilities.composeOfferContent(null, null,
				agentType, offerType);
		assertEquals("", result);
	}

	@Test
	public void testComposeOfferContent_should_return_empty_offer_when_amount_is_zero() {
		itemAmount = new EnumMap<Products, Double>(Products.class);
		pricePerItem = new EnumMap<Products, Double>(Products.class);

		itemAmount.put(Products.MANURE, 0.0);
		itemAmount.put(Products.ANIMAL, 0.0);
		itemAmount.put(Products.FRUIT, 0.0);

		pricePerItem.put(Products.MANURE, 1.0);
		pricePerItem.put(Products.ANIMAL, 567.0);
		pricePerItem.put(Products.FRUIT, 3456.0);
		String result = OfferFormatUtilities.composeOfferContent(itemAmount,
				pricePerItem, agentType, offerType);
		assertEquals("", result);
	}

	@Test
	public void testComposeOfferContent_should_return_correct_format_for_sell_offer() {
		String result = OfferFormatUtilities.composeOfferContent(itemAmount,
				pricePerItem, agentType, OfferFormatUtilities.SELL_OFFER_TAG);
		assertEquals(agentType + OfferFormatUtilities.OFFER_FIELD_DELIMITER
				+ OfferFormatUtilities.SELL_OFFER_TAG + OfferFormatUtilities.OFFER_FIELD_DELIMITER
				+ "FRUIT 15.0 3456.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER
				+ "ANIMAL 34.0 567.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER
				+ "MANURE 12.4 1.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER, result);
	}

	@Test
	public void testComposeOfferContent_should_return_correct_format_for_buy_offer() {
		String result = OfferFormatUtilities.composeOfferContent(itemAmount,
				pricePerItem, agentType, OfferFormatUtilities.BUY_OFFER_TAG);
		assertEquals(agentType + OfferFormatUtilities.OFFER_FIELD_DELIMITER
				+ OfferFormatUtilities.BUY_OFFER_TAG + OfferFormatUtilities.OFFER_FIELD_DELIMITER
				+ "FRUIT 15.0 3456.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER
				+ "ANIMAL 34.0 567.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER
				+ "MANURE 12.4 1.0"
				+ OfferFormatUtilities.OFFER_ITEM_DELIMITER, result);
	}

}
