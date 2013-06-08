package agents;

import java.util.HashMap;
import java.util.Map;

public class AgentOffer {
	final static int ITEM_NAME_INDEX = 0;
	final static int ITEM_AMOUNT_INDEX = 1;
	final static int ITEM_PRICE_INDEX = 2;
	final static int ITEM_ENTRY_LENGTH = 3;
	final static int AGENT_TYPE_INDEX = 0;
	final static int OFFER_TYPE_INDEX = 1;
	final static int ITEM_LIST_INDEX = 2;
	final static String OFFER_ITEM_DELIMITER = ":";
	final static String OFFER_FIELD_DELIMITER = ";";
	final static String OFFER_ITEM_PARTS_DELIMITER = " ";
	
	public String getAgentType() {
		return agentType;
	}
	public void setAgentType(String agentType) {
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
	public Map<String, Double> getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Map<String, Double> itemPrice) {
		this.itemPrice = itemPrice;
	}
	public Map<String, Double> getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(Map<String, Double> itemAmount) {
		this.itemAmount = itemAmount;
	}
	private void initialize() {
		itemPrice = new HashMap<String, Double>();
		itemAmount = new HashMap<String, Double>();
		agentName = agentType = offerType = "";		
	}
	
	public AgentOffer() {
		initialize();
	}
	
	public void addItemPrice(String item, double price) {
		itemPrice.put(item, price);
	}

	public void addItemAmount(String item, double price) {
		itemAmount.put(item, price);
	}

	private void parseListElements(String[] items) {
		for (String single_entry : items) {
			String[] values = single_entry.split(OFFER_ITEM_PARTS_DELIMITER);
			if (values.length < ITEM_ENTRY_LENGTH) continue;
			itemPrice.put(values[ITEM_NAME_INDEX], Double.parseDouble(values[ITEM_PRICE_INDEX]));
			itemAmount.put(values[ITEM_NAME_INDEX], Double.parseDouble(values[ITEM_AMOUNT_INDEX]));
		}
	}
	public void parseIncommingOffer(String agentName, String offer) {
		this.agentName = agentName;
		if(offer.isEmpty()) return;
		String[] str = offer.split(OFFER_FIELD_DELIMITER);
		this.agentType = str[AGENT_TYPE_INDEX];
		this.offerType = str[OFFER_TYPE_INDEX];
		String[] items;
		try {
			items = str[ITEM_LIST_INDEX].split(OFFER_ITEM_DELIMITER);
			parseListElements(items);
		} catch (Exception e) {
			// raised in case the offer is empty
		}

	}
	private String agentType;
	private String agentName;
	private String offerType;
	private Map<String, Double> itemPrice;
	private Map<String, Double> itemAmount;	
}
