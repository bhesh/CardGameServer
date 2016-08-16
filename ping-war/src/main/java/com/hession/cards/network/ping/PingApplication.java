package com.hession.cards.network.ping;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.wink.json4j.JSON;
import org.apache.wink.json4j.JSONArray;
import org.apache.wink.json4j.JSONObject;
import org.apache.wink.json4j.JSONException;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.PUT;

import com.hession.cards.engine.Card;
import com.hession.cards.engine.CardTranslator;
import com.hession.cards.engine.DeckException;

import com.hession.cards.ping.Ping;
import com.hession.cards.ping.PingAction;
import com.hession.cards.ping.PingController;
import com.hession.cards.ping.PingException;

/**
 * @author Brian Hession
 * Email: hessionb@gmail.com
 */
@Path("/")
public class PingApplication extends Application {

	private static final int OK_CODE = 200;
	private static final int CREATED_CODE = 201;
	private static final int INVALID_REQUEST_CODE = 403;
	private static final int NOT_FOUND_CODE = 404;
	private static final int CONFLICT_CODE = 409;
	private static final int SERVER_ERROR_CODE = 500;

	private static final String INVALID_JSON_ERROR = "Invalid JSON message";
	private static final String INVALID_SID_ERROR = "Invalid SID";
	private static final String INVALID_UID_ERROR = "Invalid UID";
	private static final String INVALID_ACTION_ERROR = "Invalid action";
	private static final String SID_ALREADY_IN_SESSION_ERROR = "SID is already in session";
	private static final String NO_SID_PRESENTED_ERROR = "Must present SID in the message body";
	private static final String NO_PLAYERS_PRESENTED_ERROR = "Must present players in the message body";
	private static final String INVALID_NUMBER_OF_PLAYERS_ERROR = "Invalid number of players";
	private static final String NO_ACTION_PRESENTED_ERROR = "Must present and action in the message body";

	private static final ConcurrentMap<String, NetworkPingController> controllers = new ConcurrentHashMap<String, NetworkPingController>();

	/**
	 * Returns the server stats
	 * @return server stats
	 */
	@GET
	@Path("/")
	@Produces(MediaType.APPLICATION_JSON)
	public Response serverStats() {
		try {
			JSONObject info = new JSONObject().put("numgames", this.controllers.size());
			for (String sid : this.controllers.keySet()) {
				NetworkPingController controller = this.controllers.get(sid);
				synchronized (controller) {
					info.append("games", this.getGameStats(controller));
				}
			}
			return Response.status(OK_CODE).entity(info.toString()).build();
		} catch (JSONException e) {
			return Response.status(SERVER_ERROR_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
		}
	}

	/**
	 * Starts a new game
	 * Consumes JSON:
	 * {
	 *   "sid" : <sid>,
	 *   "players" : [
	 *     <player1>,
	 *     <player2>,
	 *     ...
	 *   ]
	 * }
	 * @return response
	 */
	@POST
	@Path("/newgame")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response newGame(String body) {

		String sid = null;
		ArrayList<String> names = new ArrayList<String>();

		// Parse the JSON received
		try {
			JSONObject gameData = (JSONObject) JSON.parse(body);

			// Grab the SID
			if (!gameData.has("sid"))
				return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(NO_SID_PRESENTED_ERROR)).build();
			sid = gameData.getString("sid");

			// Grab the player names
			if (!gameData.has("players"))
				return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(NO_PLAYERS_PRESENTED_ERROR)).build();
			JSONArray jsonNames = gameData.getJSONArray("players");
			for (int i = 0; i < jsonNames.length(); ++i)
				names.add(jsonNames.getString(i));
		} catch (JSONException e) {
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_JSON_ERROR)).build();
		}

		// Check if game is in session
		if (this.controllers.containsKey(sid))
			return Response.status(CONFLICT_CODE).entity(this.buildErrorJSON(SID_ALREADY_IN_SESSION_ERROR)).build();

		// Check that players were specified
		if (names.size() < 2)
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_NUMBER_OF_PLAYERS_ERROR)).build();

		try {
			// Create a new game
			NetworkPingPlayer[] players = new NetworkPingPlayer[names.size()];
			for (int i = 0; i < players.length; ++i)
				players[i] = new NetworkPingPlayer(names.get(i), names.get(i));
			NetworkPingController controller = new NetworkPingController(sid, players);
			this.controllers.put(sid, controller);
			synchronized (controller) {
				return Response.status(CREATED_CODE).entity(this.getGameStats(controller).toString()).build();
			}
		} catch (JSONException e) {
			return Response.status(SERVER_ERROR_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
		} catch (PingException e) {
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
		} catch (Exception e) {
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
		}
	}

	/**
	 * Ends a current game session
	 * @param sid
	 * @return response
	 */
	@DELETE
	@Path("/endgame/{sid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response endGame(@PathParam("sid") String sid) {
		if (!this.controllers.containsKey(sid))
			return Response.status(NOT_FOUND_CODE).entity(this.buildErrorJSON(INVALID_SID_ERROR)).build();
		this.controllers.remove(sid);
		return Response.status(OK_CODE).build();
	}

	/**
	 * Sends a control action
	 * @param sid
	 * @param name
	 * Consumes JSON:
	 * {
	 *   "action" : <action>,
	 *   "params" : [
	 *     <param1>,
	 *     <param2>,
	 *     ...
	 *   ]
	 * }
	 * @return response
	 */
	@PUT
	@Path("/{sid}/{uid}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response sendControl(@PathParam("sid") String sid,
	                            @PathParam("uid") String uid,
								String body) {

		String action = null;
		ArrayList<Integer> params = new ArrayList<Integer>();

		// Parse the JSON body
		try {
			JSONObject actionData = (JSONObject) JSON.parse(body);

			// Grab the action
			if (!actionData.has("action"))
				return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(NO_ACTION_PRESENTED_ERROR)).build();
			action = actionData.getString("action");

			// Grab the parameters (if any)
			if (actionData.has("params")) {
				JSONArray paramData = actionData.getJSONArray("params");
				for (int i = 0; i < paramData.length(); ++i)
					params.add(paramData.getInt(i));
			}
		} catch (JSONException e) {
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_JSON_ERROR)).build();
		}

		// Make param array
		int[] arguments = new int[params.size()];
		for (int i = 0; i < arguments.length; ++i)
			arguments[i] = params.get(i).intValue();

		// Get the controller
		if (!this.controllers.containsKey(sid))
			return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_SID_ERROR)).build();
		NetworkPingController controller = this.controllers.get(sid);

		// Lock the controller
		synchronized (controller) {

			// Get the player
			if (!controller.containsPlayer(uid))
				return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_UID_ERROR)).build();
			NetworkPingPlayer player = controller.getPlayer(uid);

			// Lock the player
			synchronized (player) {

				try {
					// Apply the action
					PingAction pingAction = parseAction(action);
					if (pingAction == null)
						return Response.status(NOT_FOUND_CODE).entity(this.buildErrorJSON(INVALID_ACTION_ERROR)).build();
					controller.runAction(pingAction, player, arguments);

					// Return the player stats
					return Response.status(OK_CODE).entity(this.getPrivatePlayerStats(player).toString()).build();
				} catch (JSONException e) {
					return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(INVALID_JSON_ERROR)).build();
				} catch (PingException e) {
					return Response.status(INVALID_REQUEST_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
				} catch (Exception e) {
					return Response.status(SERVER_ERROR_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
				}
			}
		}
	}

	/**
	 * Returns the player's stats
	 * @param sid
	 * @param uid
	 * @return response
	 */
	@GET
	@Path("/{sid}/{uid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getStatus(@PathParam("sid") String sid,
	                          @PathParam("uid") String uid) {
		// Get the controller
		if (!this.controllers.containsKey(sid))
			return Response.status(NOT_FOUND_CODE).entity(this.buildErrorJSON(INVALID_SID_ERROR)).build();
		NetworkPingController controller = this.controllers.get(sid);

		// Lock the controller
		synchronized (controller) {

			// Get player
			if (!controller.containsPlayer(uid))
				return Response.status(NOT_FOUND_CODE).entity(INVALID_UID_ERROR).build();
			NetworkPingPlayer player = controller.getPlayer(uid);

			// Lock the player
			synchronized (player) {

				// Return player stats
				try {
					return Response.status(OK_CODE).entity(this.getPrivatePlayerStats(player).toString()).build();
				} catch (JSONException e) {
					return Response.status(SERVER_ERROR_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
				}
			}
		}
	}

	/**
	 * Returns the game's state
	 * @param sid
	 * @return the game's state
	 */
	@GET
	@Path("/{sid}")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getGameState(@PathParam("sid") String sid) {

		// Get the controller
		if (!this.controllers.containsKey(sid))
			return Response.status(NOT_FOUND_CODE).entity(this.buildErrorJSON(INVALID_SID_ERROR)).build();
		NetworkPingController controller = this.controllers.get(sid);

		// Lock the controller
		synchronized (controller) {
			try {
				// Return game stats
				return Response.status(OK_CODE).entity(this.getGameStats(controller).toString()).build();
			} catch (JSONException e) {
				return Response.status(SERVER_ERROR_CODE).entity(this.buildErrorJSON(e.getMessage())).build();
			}
		}
	}

	/**
	 * Puts together private player stats. NOT THREAD SAFE.
	 * @param player
	 * @return JSON of player stats
	 */
	private JSONObject getPrivatePlayerStats(NetworkPingPlayer player) throws JSONException {
		JSONObject info = this.getPublicPlayerStats(player);
		for (Card c : player.getHand().getCards())
			info.append("cards", CardTranslator.simpleCard(c.getId()));
		return info;
	}

	/**
	 * Puts together public player stats. NOT THREAD SAFE.
	 * @param player
	 * @return JSON of player stats
	 */
	private JSONObject getPublicPlayerStats(NetworkPingPlayer player) throws JSONException {
		JSONObject info = new JSONObject()
				.put("uid", player.getUid())
				.put("name", player.getName())
				.put("state", Ping.translateState(player.getState()))
				.put("score", player.getScore());
		return info;
	}

	/**
	 * Puts together the game stats. CONTROLER NOT THREAD SAFE.
	 * @param controller
	 * @return JSON of game stats
	 */
	private JSONObject getGameStats(NetworkPingController controller) throws JSONException {

		// Get the game
		Ping game = controller.getPing();

		// Lock the game
		synchronized (game) {

			// Build the stats
			JSONObject info = new JSONObject()
					.put("sid", controller.getSid())
					.put("round", game.getRound())
					.put("decksize", game.getDeck().totalCardsLeft());

			// Get current player's info
			int turn = game.getCurrentTurn();
			info.put("turn", turn);
			NetworkPingPlayer player = controller.getPlayers()[turn];
			synchronized (player) {
				info.put("player", this.getPublicPlayerStats(player));
			}

			// Append all of the players
			for (NetworkPingPlayer p : controller.getPlayers()) {
				synchronized (p) {
					info.append("players", this.getPublicPlayerStats(p));
				}
			}

			// Get the top of the discard (if there's one)
			try {
				info.put("discard", CardTranslator.simpleCard(game.getDiscardPile().peekCard().getId()));
			} catch (DeckException e1) {
				info.put("discard", "NULL");
			}

			return info;
		}
	}

	/**
	 * Returns a JSON message with an error
	 * @param message
	 * @return json
	 */
	private String buildErrorJSON(String message) {
		return "{\"error\":\"" + message + "\"}";
	}

	/**
	 * Parses the action from the string
	 * @param action
	 * @return the action enum
	 */
	private static PingAction parseAction(String action) {
		if (action.equalsIgnoreCase("deal"))
			return PingAction.DEAL;
		else if (action.equalsIgnoreCase("reorganizehand"))
			return PingAction.REORGANIZE_HAND;
		else if (action.equalsIgnoreCase("drawfromdeck"))
			return PingAction.DRAW_FROM_DECK;
		else if (action.equalsIgnoreCase("drawfromdiscard"))
			return PingAction.DRAW_FROM_DISCARD;
		else if (action.equalsIgnoreCase("discard"))
			return PingAction.DISCARD;
		else if (action.equalsIgnoreCase("checkping"))
			return PingAction.CHECK_PING;
		else if (action.equalsIgnoreCase("endturn"))
			return PingAction.END_TURN;
		else if (action.equalsIgnoreCase("endround"))
			return PingAction.END_ROUND;
		else
			return null;
	}
}
