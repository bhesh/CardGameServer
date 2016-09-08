package com.hession.cards.network.ping;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.hession.cards.engine.ControllerActionException;

import com.hession.cards.engine.network.NetworkController;

import com.hession.cards.ping.Ping;
import com.hession.cards.ping.PingController;
import com.hession.cards.ping.PingException;

/**
 * Wraps the PingController into a network interface
 */
public class NetworkPingController extends PingController implements NetworkController {

	private String sid;
	private NetworkPingPlayer[] players;
	private Map<String, NetworkPingPlayer> playerhash = new HashMap<String, NetworkPingPlayer>();

	public NetworkPingController(String sid, NetworkPingPlayer[]  players) throws PingException, ControllerActionException {
		super(new Ping(sid, players));
		this.sid = sid;
		this.players = players;
		for (NetworkPingPlayer p : players)
			this.playerhash.put(p.getUid(), p);
	}

	public String getSid() {
		return this.sid;
	}

	public NetworkPingPlayer[] getPlayers() {
		return players;
	}

	public boolean containsPlayer(String uid) {
		return playerhash.containsKey(uid);
	}

	public NetworkPingPlayer getPlayer(String uid) {
		return playerhash.get(uid);
	}

	public boolean equals(Object other) {
		try {
			NetworkPingController npc = (NetworkPingController) other;
			return npc.getSid().equals(this.getSid());
		} catch (ClassCastException e) {
			return false;
		}
	}
}
