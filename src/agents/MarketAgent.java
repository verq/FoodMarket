package agents;

import jade.core.Agent;

import java.util.HashMap;
import java.util.Map;

import constants.MarketConstants;

public abstract class MarketAgent extends Agent {
	protected int income;
	protected Map<Integer, Integer> buy;
	protected Map<Integer, Integer> have;
	protected Map<Integer, Integer> sell;

	public MarketAgent() {
		initializeProducts();
	}

	protected void initializeProducts() {
		buy = new HashMap<Integer, Integer>();
		have = new HashMap<Integer, Integer>();
		sell = new HashMap<Integer, Integer>();
		initializeAddProducts(buy);
		initializeAddProducts(have);
		initializeAddProducts(sell);

	}

	protected void initializeAddProducts(Map<Integer, Integer> products) {
		for (int i = 0; i < MarketConstants.NUMBER_OF_PRODUCTS; i++) {
			products.put(i, 0);
		}
	}

	protected abstract void fillBuy();

	protected abstract void fillHave();

	protected abstract void fillSell();
}
