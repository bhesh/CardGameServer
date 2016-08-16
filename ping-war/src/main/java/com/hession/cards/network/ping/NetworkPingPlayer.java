package com.hession.cards.network.ping;

import com.hession.cards.engine.network.NetworkPlayer;

import com.hession.cards.ping.PingPlayer;

/**
 * Defines a Ping player with a UID
 */
public class NetworkPingPlayer extends PingPlayer implements NetworkPlayer {

	private String uid;

	public NetworkPingPlayer(String uid, String name) {
		super(name);
		this.uid = uid;
	}

	public String getUid() {
		return this.uid;
	}

	public boolean equals(Object other) {
		try {
			NetworkPingPlayer npp = (NetworkPingPlayer) other;
			return npp.getUid().equals(this.getUid());
		} catch (ClassCastException e) {
			return false;
		}
	}

	public String toString() {
		return this.getName();
	}
}
