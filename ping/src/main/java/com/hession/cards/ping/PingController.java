package com.hession.cards.ping;

import com.hession.cards.engine.Action;
import com.hession.cards.engine.Controller;
import com.hession.cards.engine.ControllerActionException;
import com.hession.cards.engine.Player;

/**
 * @author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Ping Controller
 */
public class PingController extends Controller<PingAction> {

	private final Ping game;

	/**
	 * Constructs a new PingController
	 */
	public PingController(final Ping game) throws ControllerActionException {
		this.game = game;
		this.registerAction(PingAction.DEAL, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.deal((PingPlayer) player);
			}
		});
		this.registerAction(PingAction.REORGANIZE_HAND, new Action() {
			public void process(Player player, int... params) throws Exception {
				if (params == null || params.length < 2)
					throw new PingException("Invalid action");
				game.reorganizeHand((PingPlayer) player, params[0], params[1]);
			}
		});
		this.registerAction(PingAction.DRAW_FROM_DECK, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.drawFromDeck((PingPlayer) player);
			}
		});
		this.registerAction(PingAction.DRAW_FROM_DISCARD, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.drawFromDiscard((PingPlayer) player);
			}
		});
		this.registerAction(PingAction.DISCARD, new Action() {
			public void process(Player player, int... params) throws Exception {
				if (params == null || params.length < 1)
					throw new PingException("Invalid action");
				game.discard((PingPlayer) player, params[0]);
			}
		});
		this.registerAction(PingAction.CHECK_PING, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.checkPing((PingPlayer) player);
			}
		});
		this.registerAction(PingAction.END_TURN, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.endTurn((PingPlayer) player);
			}
		});
		this.registerAction(PingAction.END_ROUND, new Action() {
			public void process(Player player, int... params) throws Exception {
				game.endRound();
			}
		});
	}

	public Ping getPing() {
		return this.game;
	}
}
