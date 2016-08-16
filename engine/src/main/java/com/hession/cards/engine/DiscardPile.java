package com.hession.cards.engine;

import java.util.Stack;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Implements a stack for the discard pile
 */
public class DiscardPile {

	private Stack<Card> discard = new Stack<Card>();

	/**
	 * Push card on the top of the pile
	 * @Param card
	 */
	public void pushCard(Card card) {
		discard.push(card);
	}

	/**
	 * Pop card off the top of the pile
	 * @Return the card popped off
	 * @Throws DeckException
	 */
	public Card popCard() throws DeckException {
		if (this.isEmpty())
			throw new DeckException("There is no card in the discard pile");
		return discard.pop();
	}

	/**
	 * Peek at the top of the discard pile
	 * @Return the top of the discard pile
	 * @Throws DeckException
	 */
	public Card peekCard() throws DeckException {
		if (this.isEmpty())
			throw new DeckException("There is no card in the discard pile");
		return discard.peek();
	}

	/**
	 * Returns whether empty or not
	 * @Return true if empty
	 */
	public boolean isEmpty() {
		return discard.empty();
	}

	/**
	 * Returns whether the card is in the discard pile
	 * @Return true if the card is in the discard pile
	 */
	public boolean hasCard(Card card) {
		return discard.search(card) != -1;
	}

	/**
	 * Clears the discard pile
	 */
	public void clear() {
		discard.clear();
	}
}
