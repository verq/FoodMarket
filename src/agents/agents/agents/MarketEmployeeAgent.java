package agents;

import constants.Products;

public abstract class MarketEmployeeAgent extends MarketAgent {
	protected int numberOfEmployees;
	protected double productCost;

	protected void produceAndUse() {
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