package com.hession.cards.engine;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Defines a the controller class
 */
public abstract class Controller<E> {
	
	private Map<E, Action> actions = new HashMap<E, Action>();

	/**
	 * Registers the action for the given command
	 * @param command
	 * @param action
	 */
	public void registerAction(E command, Action action) throws ControllerActionException {
		if (actions.containsKey(command))
			throw new ControllerActionException("Command is already registered");
		actions.put(command, action);
	}

	/**
	 * Unegisters the action for the given command
	 * @param command
	 * @return the unregistered action
	 */
	public Action unregisterAction(E command) throws ControllerActionException {
		if (!actions.containsKey(command))
			throw new ControllerActionException("The command is not registered");
		return actions.remove(command);
	}

	/**
	 * Runs the action for the given command
	 * @param command
	 * @param player
	 */
	public void runAction(E command, Player player) throws Exception {
		this.runAction(command, player, null);
	}

	/**
	 * Runs the action for the given command
	 * @param command
	 * @param player
	 */
	public void runAction(E command, Player player, int... params) throws Exception {
		if (!actions.containsKey(command))
			throw new ControllerActionException("The command is not registered");
		actions.get(command).process(player, params);
	}
}
