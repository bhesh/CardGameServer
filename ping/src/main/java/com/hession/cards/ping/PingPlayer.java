package com.hession.cards.ping;

import com.hession.cards.engine.Player;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * PingPlayer
 */
public class PingPlayer extends Player {

	private int score = 0;
	private PlayerState state = PlayerState.WAITING;

	/**
	 * Constructs a ping player
	 * @Param name
	 */
	public PingPlayer(String name) {
		super(name);
	}

	/**
	 * Sets the score for the player
	 * @Param score
	 */
	public void setScore(int score) {
		this.score = score;
	}

	/**
	 * Returns the score
	 * @Return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Sets the player's state
	 * @Param state
	 */
	public void setState(PlayerState state) {
		this.state = state;
	}

	/**
	 * Returns the player's state
	 * @Return the state of the player
	 */
	public PlayerState getState() {
		return state;
	}
}
