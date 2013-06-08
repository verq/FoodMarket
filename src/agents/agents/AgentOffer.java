package agents;

import java.util.HashMap;
import java.util.Map;

public class AgentOffer {
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
	public Map<String, Integer> getItemPrice() {
		return itemPrice;
	}
	public void setItemPrice(Map<String, Integer> itemPrice) {
		this.itemPrice = itemPrice;
	}
	public Map<String, Integer> getItemAmount() {
		return itemAmount;
	}
	public void setItemAmount(Map<String, Integer> itemAmount) {
		this.itemAmount = itemAmount;
	}
	public AgentOffer() {
		itemPrice = new HashMap<String, Integer>();
		itemAmount = new HashMap<String, Integer>();
		agentName = agentType = offerType = "";
	}
	
	public void addItemPrice(String item, int price)
	{
		itemPrice.put(item, price);
	}
	public void addItemAmount(String item, int price)
	{
		itemAmount.put(item, price);
	}
	private String agentType;
	private String agentName;
	private String offerType;
	private Map<String, Integer> itemPrice;
	private Map<String, Integer> itemAmount;	
}
