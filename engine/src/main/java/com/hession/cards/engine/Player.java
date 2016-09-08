package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines a player
 */
public class Player {

	private String name;
	private Hand hand;

	/**
	 * Constructs a player
	 * @Param name
	 */
	public Player(String name) {
		this.name = name;
		hand = new Hand();
	}

	/**
	 * Returns the name
	 * @Return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the hand
	 * @Return the hand
	 */
	public Hand getHand() {
		return hand;
	}

	/**
	 * Returns whether the players are equal
	 * @Return true if equal
	 */
	public boolean equals(Object other) {
		return other instanceof Player && ((Player) other).getName() == this.getName();
	}
}
