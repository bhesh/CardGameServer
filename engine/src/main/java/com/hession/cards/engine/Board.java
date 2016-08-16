package com.hession.cards.engine;

import java.util.ArrayList;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines the board
 */
public class Board {

	private ArrayList<Integer> cards = new ArrayList<Integer>();

	/**
	 * Add a card to the board
	 * @Param card
	 */
	public void addCard(int card) {
		cards.add(card);
	}

	/**
	 * Remove a card from the board
	 * @Param card
	 * @Return the card upon removal
	 */
	public int removeCard(int card) throws HandException {
		if (cards.remove(new Integer(card)))
			return card;
		throw new HandException("The hand does not contain the card id: " + card);
	}

	/**
	 * Clears the board
	 */
	public void clear() {
		cards.clear();
	}

	/**
	 * Returns whether the board contains the card
	 * @Return true if the board contains the card
	 */
	public boolean contains(int card) {
		return cards.contains(new Integer(card));
	}

	/**
	 * Returns the number of cards on the board
	 * @Return total cards on the board
	 */
	public int size() {
		return cards.size();
	}

	/**
	 * Returns the cards on the board
	 * @Return the cards on the board
	 */
	public int[] getCards() {
		int[] cs = new int[cards.size()];
		for (int i = 0; i < cs.length; ++i)
			cs[i] = cards.get(i).intValue();
		return cs;
	}
}
