package agents;

import constants.Products;

public abstract class MarketFieldAgent extends MarketAgent {
	protected int numberOfFields;
	protected int tresholdSale;

	protected void produce() {
		for (Products product : sellTo.values()) {
			double numberOfProduct = have.get(product);
			numberOfProduct = numberOfProduct + numberOfFields * getNumberOfProductsPerField(product);
			have.put(product, numberOfProduct);
		}
	}

	protected void use() {
		// TODO
	}

	protected abstract double getNumberOfProductsPerField(Products product);

	@Override
	protected abstract void fillInitialBuy();

	@Override
	protected abstract void fillInitialHave();

	@Override
	protected abstract void fillInitialSell();

}
