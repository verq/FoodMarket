package agents;

import utilities.AgentsUtilities;
import constants.MarketConstants;
import constants.Products;

public abstract class MarketFieldAgent extends MarketAgent {
	protected int numberOfFields;
	protected int tresholdSale;

	protected void produceAndUse() {
		for (Products product : sellTo.values()) {
			double numberOfProduct = have.get(product);
			//if(AgentsUtilities.PRINT_AGENT_WEEKLY_UPDATES)  System.out.println("farmer had " + numberOfProduct + " " + product);
			numberOfProduct = numberOfProduct + numberOfFields
					* getNumberOfProductsPerField(product);
			//if(AgentsUtilities.PRINT_AGENT_WEEKLY_UPDATES) System.out.println("farmer now has " + numberOfProduct + " " + product);
			have.put(product, numberOfProduct);
			
			// TODO: przerobić na funkcję zwracającą odpowiedni treshold w zależności od produktu
			double newSellValue = sell.get(product) + have.get(product) * MarketConstants.FARMER_TRESHOLD_SOLD_GRAIN;
			sell.put(product, newSellValue);
		}
	}

	protected abstract double getNumberOfProductsPerField(Products product);

	@Override
	protected abstract void fillInitialBuy();

	@Override
	protected abstract void fillInitialHave();

	@Override
	protected abstract void fillInitialSell();

}
