package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines a card
 */
public class Card {

	private int id;

	/**
	 * Constructs a card of id: id
	 */
	public Card(int id) {
		this.id = id;
	}

	/**
	 * Returns the card's id
	 * @Return the id of the card
	 */
	public int getId() {
		return id;
	}

	public String toString() {
		return "" + id;
	}
}
