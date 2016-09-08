package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Translates a card
 */
public class CardTranslator {

	private static final String[] SUITS = {
		"Spades",
		"Clubs",
		"Diamonds",
		"Hearts"
	};
	private static final String[] VALUES = new String[13];
	static {
		VALUES[0] = "A";
		for (int i = 2; i <= 10; ++i)
			VALUES[i - 1] = "" + i;
		VALUES[10] = "J";
		VALUES[11] = "Q";
		VALUES[12] = "K";
	};

	/**
	 * Returns the string of the simple card id
	 * @param id id of the card
	 * @return name of the card
	 */
	public static String simpleCard(int id) {
		if (id < 0 || id >= (VALUES.length * SUITS.length))
			return "Joker";
		return VALUES[id % VALUES.length] + " - " + SUITS[id / VALUES.length];
	}
}
