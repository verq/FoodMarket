package constants;

import java.util.Iterator;
import java.util.Map;

public class OfferFormatUtilities {
	public final static String OFFER_ITEM_DELIMITER = ":";
	public final static String OFFER_FIELD_DELIMITER = ";";
	public final static String OFFER_ITEM_PARTS_DELIMITER = " ";
	public final static String SELL_OFFER_TAG = "sell";
	public final static String BUY_OFFER_TAG = "buy";

	public final static int ITEM_NAME_INDEX = 0;
	public final static int ITEM_AMOUNT_INDEX = 1;
	public final static int ITEM_PRICE_INDEX = 2;
	public final static int ITEM_ENTRY_LENGTH = 3;
	public final static int AGENT_TYPE_INDEX = 0;
	public final static int OFFER_TYPE_INDEX = 1;
	public final static int ITEM_LIST_INDEX = 2;

	public static String composeOfferContent(Map<Products, Double> itemAmount,
			Map<Products, Double> pricePerItem, Participants agentType,
			String offerType) {
		if (itemAmount == null || pricePerItem == null
				|| itemAmount.size() == 0 || pricePerItem.size() == 0)
			return "";
		String content = agentType + OfferFormatUtilities.OFFER_FIELD_DELIMITER
				+ offerType + OfferFormatUtilities.OFFER_FIELD_DELIMITER;
		Iterator<Products> sellProductsIterator = itemAmount.keySet()
				.iterator();
		boolean nonzero = false;
		while (sellProductsIterator.hasNext()) {
			Products p = sellProductsIterator.next();
			if (itemAmount.get(p) > 0.0) {
				nonzero = true;

				String amount = String.format("%.2f", itemAmount.get(p).doubleValue());
				String price = String.format("%.2f", pricePerItem.get(p).doubleValue());
				content += p + OfferFormatUtilities.OFFER_ITEM_PARTS_DELIMITER
						+ itemAmount.get(p).doubleValue()//String.format("%.2f", itemAmount.get(p).doubleValue())
						+ OfferFormatUtilities.OFFER_ITEM_PARTS_DELIMITER
						+ pricePerItem.get(p).doubleValue()//String.format("%.2f", pricePerItem.get(p).doubleValue())
						+ OfferFormatUtilities.OFFER_ITEM_DELIMITER;
			}
		}
		if (!nonzero)
			return "";

		return content;
	}
}
