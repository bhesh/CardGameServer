package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines an Action
 */
public abstract class Action {

	/**
	 * Runs the action
	 * @Param player
	 */
	public void process(Player player) throws Exception {
		this.process(player, null);
	}

	/**
	 * Runs the action
	 * @Param player
	 */
	public abstract void process(Player player, int... params) throws Exception;
}
