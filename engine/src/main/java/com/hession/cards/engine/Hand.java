package com.hession.cards.engine;

import java.util.ArrayList;

import com.hession.cards.engine.HandException;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines a hand
 */
public class Hand {
	
	private ArrayList<Card> hand = new ArrayList<Card>();

	/**
	 * Adds a card to the hand in a certain position
	 * @Param pos
	 * @Param card
	 */
	public void addCard(int pos, Card card) {
		hand.add(pos, card);
	}

	/**
	 * Adds a card to the hand at the end
	 * @Param card
	 */
	public void addCard(Card card) {
		hand.add(card);
	}

	/**
	 * Removes a card from the hand
	 * @Param pos position to remove
	 * @Return the card that was removed
	 */
	public Card removeCard(int pos) {
		return hand.remove(pos);
	}

	/**
	 * Removes a card from the hand
	 * @Param card card to remove
	 * @Return true if successful
	 */
	public boolean removeCard(Card card) {
		return hand.remove(card);
	}

	/**
	 * Returns an array of the cards
	 * @Return the cards in the hand
	 */
	public Card[] getCards() {
		return hand.toArray(new Card[hand.size()]);
	}

	/**
	 * Returns whether the hand is empty
	 * @Return true if the hand is empty
	 */
	public boolean isEmpty() {
		return hand.isEmpty();
	}

	/**
	 * Returns whether the hand contains a card
	 * @Param card
	 * @Return true if the hand contains card
	 */
	public boolean hasCard(Card card) {
		return hand.contains(card);
	}

	/**
	 * Clears a hand
	 */
	public void clear() {
		hand.clear();
	}

	/**
	 * Returns the size of the hand
	 * @return the size of the hand
	 */
	public int size() {
		return hand.size();
	}
	
	/**
	 * Swaps 2 cards
	 */
	public void swap(int pos1, int pos2) {
		if (pos1 < 0 || pos2 < 0)
			throw new HandException("Invalid position");
		if (pos1 > hand.size() || pos2 > hand.size())
			throw new HandException("Invalid position");
		Card t = hand.get(pos2);
		hand.set(pos2, hand.get(pos1));
		hand.set(pos1, t);
	}
}
