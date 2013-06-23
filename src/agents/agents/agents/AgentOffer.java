package agents;

import java.util.HashMap;
import java.util.Map;

import constants.OfferFormatUtilities;
import constants.Participants;
import constants.Products;

public class AgentOffer {

	public Participants getAgentType() {
		return agentType;
	}

	public void setAgentType(Participants agentType) {
		this.agentType = agentType;
	}

	public String getAgentName() {
		return agentName;
	}

	public void setAgentName(String agentName) {
		this.agentName = agentName;
	}

	public String getOfferType() {
		return offerType;
	}

	public void setOfferType(String offerType) {
		this.offerType = offerType;
	}

	public Map<Products, Double> getItemPrice() {
		return itemPrice;
	}

	public void setItemPrice(Map<Products, Double> itemPrice) {
		this.itemPrice = itemPrice;
	}

	public Map<Products, Double> getItemAmount() {
		return itemAmount;
	}

	public void setItemAmount(Map<Products, Double> itemAmount) {
		this.itemAmount = itemAmount;
	}

	private void initialize() {
		itemPrice = new HashMap<Products, Double>();
		itemAmount = new HashMap<Products, Double>();
	}

	public AgentOffer() {
		initialize();
	}

	public AgentOffer(String agentName, String offer) {
		initialize();
		parseIncommingOffer(agentName, offer);
	}

	public void addItemPrice(String item, double price) {
		itemPrice.put(Products.valueOf(item), price);
	}

	public void addItemAmount(Products item, double amount) {
		itemAmount.put(item, amount);
	}

	public void addItemPrice(Products item, double price) {
		itemPrice.put(item, price);
	}

	public void addItemAmount(String item, double amount) {
		itemAmount.put(Products.valueOf(item), amount);
	}

	private void parseListElements(String[] items) {
		for (String single_entry : items) {
			String[] values = single_entry
					.split(OfferFormatUtilities.OFFER_ITEM_PARTS_DELIMITER);
			if (values.length < OfferFormatUtilities.ITEM_ENTRY_LENGTH)
				continue;
			itemPrice
					.put(Products
							.valueOf(values[OfferFormatUtilities.ITEM_NAME_INDEX]),
							Double.parseDouble(values[OfferFormatUtilities.ITEM_PRICE_INDEX]));
			itemAmount
					.put(Products
							.valueOf(values[OfferFormatUtilities.ITEM_NAME_INDEX]),
							Double.parseDouble(values[OfferFormatUtilities.ITEM_AMOUNT_INDEX]));
		}
	}

	public void parseIncommingOffer(String agentName, String offer) {
		this.agentName = agentName;
		if (offer == null || offer.isEmpty())
			return;
		String[] str = offer.split(OfferFormatUtilities.OFFER_FIELD_DELIMITER);
		this.agentType = Participants
				.valueOf(str[OfferFormatUtilities.AGENT_TYPE_INDEX]);
		this.offerType = str[OfferFormatUtilities.OFFER_TYPE_INDEX];
		String[] items;
		try {
			items = str[OfferFormatUtilities.ITEM_LIST_INDEX]
					.split(OfferFormatUtilities.OFFER_ITEM_DELIMITER);
			parseListElements(items);
		} catch (Exception e) {
			// raised in case the offer is empty
		}

	}

	private Participants agentType;
	private String agentName;
	private String offerType;
	private Map<Products, Double> itemPrice;
	private Map<Products, Double> itemAmount;
}
