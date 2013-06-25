package agents;

import constants.MarketConstants;
import constants.Products;

public abstract class MarketEmployeeAgent extends MarketAgent {
	protected int numberOfEmployees;
	protected double productCost;

	protected void produceAndUse() {
		System.out.println(" baker have BEFORE update: " + have);
		for (Products product : sellTo.values()) {
			double numberOfProduct = have.get(product);
			double numberOfUsedProduct = numberOfEmployees * getNumberOfProductPerEmployee();
			if (numberOfUsedProduct <= have.get(getProductUsedToProduce(product))) {
				have.put(product, numberOfProduct + numberOfUsedProduct * getUsedProductUsage());
				have.put(getProductUsedToProduce(product), have.get(getProductUsedToProduce(product)) - numberOfUsedProduct);
			} else {
				have.put(product, have.get(getProductUsedToProduce(product)) * getUsedProductUsage());
				have.put(getProductUsedToProduce(product), 0D);
			}
		}
		System.out.println(" baker have AFTER update: " + have);

		for (Products product : sellTo.values()) {
			double totalAmountOfProduct = have.get(product);
			have.put(product, 0.0);
			sell.put(product, sell.get(product) + totalAmountOfProduct);
		}
	}

	protected abstract double getUsedProductUsage();

	protected abstract Products getProductUsedToProduce(Products product);

	protected abstract double getNumberOfProductPerEmployee();

	@Override
	protected abstract void fillInitialBuy();

	@Override
	protected abstract void fillInitialHave();

	@Override
	protected abstract void fillInitialSell();
}