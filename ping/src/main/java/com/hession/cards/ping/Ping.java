package com.hession.cards.ping;

import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import com.hession.cards.engine.Card;
import com.hession.cards.engine.CardTranslator;
import com.hession.cards.engine.Deck;
import com.hession.cards.engine.DeckException;
import com.hession.cards.engine.DiscardPile;
import com.hession.cards.engine.Player;

/**
 * @author Brian Hession
 * Email: hessionb@gmail.com
 *
 * Ping Game
 */
public class Ping {

	private static final Logger log = Logger.getLogger(Ping.class);

	private static final int NUM_CARDS = 53; // 52 + Joker
	private static final int STARTING_ROUND = 3;
	private static final int ENDING_ROUND = 13;

	private String sid;
	private List<PingPlayer> players;
	private Deck deck;
	private DiscardPile discardPile;
	private int round;
	private int currentTurn;
	private boolean hasPing = false;

	/**
	 * Construct a Ping game
	 */
	public Ping(String sid, PingPlayer[] players) throws PingException {
		this.sid = sid;
		if (players.length < 2)
			throw new PingException("Cannot play Ping with less than 2 players");
		this.players = Arrays.asList(players);
		log.info(this.sid + " :: creating game with players: " + this.players);

		// Build deck
		Card[] cards = new Card[NUM_CARDS * (players.length / 2)];
		for (int i = 0; i < cards.length; ++i)
			cards[i] = new Card(i % NUM_CARDS);
		this.deck = new Deck(cards);
		this.discardPile = new DiscardPile();

		// Initialize the game
		log.debug(this.sid + " :: initializing game");
		for (PingPlayer p : this.players) {
			p.setScore(0);
			p.setState(PlayerState.WAITING);
		}
		this.round = STARTING_ROUND;
		log.info(this.sid + " :: setting round to " + this.round);
		this.currentTurn = 0;
		this.players.get(currentTurn).setState(PlayerState.DEAL);
		log.info(this.sid + " :: setting " + this.getCurrentPlayer().getName() + "'s state to DEAL");
	}

	/**
	 * Resets the table and deals the cards
	 * @param player
	 */
	public void deal(PingPlayer player) throws PingException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.DEAL)
			throw new PingException("Player is not in DEAL state");

		// Clears everything
		log.debug(this.sid + " :: reseting hands and setting players to state WAITING");
		hasPing = false;
		for (PingPlayer p : players) {
			p.getHand().clear();
			p.setState(PlayerState.WAITING);
		}
		discardPile.clear();

		// Suffles and deals
		log.debug(this.sid + " :: shuffling the deck");
		deck.shuffle();
		log.debug(this.sid + " :: dealing cards");
		for (int i = 0; i < round; ++i)
			for (int j = 1; j <= players.size(); ++j)
				players.get((currentTurn + j) % players.size()).getHand().addCard(deck.dealCard());
		discardPile.pushCard(deck.dealCard());
		currentTurn = (currentTurn + 1) % players.size();
		players.get(currentTurn).setState(PlayerState.DRAW_CARD);
		log.info(this.sid + " :: setting " + this.getCurrentPlayer().getName() + "'s state to DRAW_CARD");
	}

	/**
	 * Reorganizes a card in the had
	 * @param player
	 * @param card
	 * @param pos
	 */
	public void reorganizeHand(PingPlayer player, int oldPos, int newPos) throws PingException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (oldPos < 0 || oldPos >= player.getHand().size())
			throw new PingException("Invalid card position");
		if (newPos < 0 || newPos >= player.getHand().size())
			throw new PingException("Invalid target position");
		player.getHand().addCard(newPos, player.getHand().removeCard(oldPos));
		log.debug(this.sid + " :: player " + player.getName() + " moving card " + oldPos + " to " + newPos);
	}

	/**
	 * Draws a card from the top of the deck
	 * @param player
	 * @throw PingException
	 * @throw DeckException
	 * @return the card
	 */
	public void drawFromDeck(PingPlayer player) throws PingException, DeckException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.DRAW_CARD)
			throw new PingException("Player is not in DRAW_CARD state");
		Card card = deck.dealCard();
		player.getHand().addCard(card);
		log.debug(this.sid + " :: " + player.getName() + " drew " + CardTranslator.simpleCard(card.getId()));
		player.setState(PlayerState.DISCARD);
		log.info(this.sid + " :: setting " + player.getName() + "'s state to DISCARD");
	}

	/**
	 * Draws the card from the discard pile
	 * @param player
	 * @throw PingException
	 * @throw DeckException
	 * @return the card
	 */
	public void drawFromDiscard(PingPlayer player) throws PingException, DeckException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.DRAW_CARD)
			throw new PingException("Player is not in DRAW_CARD state");
		Card card = discardPile.popCard();
		player.getHand().addCard(card);
		log.debug(this.sid + " :: " + player.getName() + " drew " + CardTranslator.simpleCard(card.getId()));
		player.setState(PlayerState.DISCARD);
		log.info(this.sid + " :: setting " + player.getName() + "'s state to DISCARD");
	}

	/**
	 * Discards the card
	 */
	public void discard(PingPlayer player, int pos) throws PingException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.DISCARD)
			throw new PingException("Player is not in DISCARD state");
		if (pos < 0 || pos >= player.getHand().size())
			throw new PingException("Invalid position");
		Card card = player.getHand().removeCard(pos);
		discardPile.pushCard(card);
		log.debug(this.sid + " :: " + player.getName() + " discarded " + CardTranslator.simpleCard(card.getId()));
		player.setState(PlayerState.CHECK_PING);
		log.info(this.sid + " :: setting " + player.getName() + "'s state to CHECK_PING");

		// Changing this to happen automatically
		checkPing(player);
		endTurn(player);
	}

	/**
	 * Checks status for ping
	 * @param player
	 * @return true if player has ping or ping occurred
	 */
	public void checkPing(PingPlayer player) throws PingException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.CHECK_PING)
			throw new PingException("Player is not in CHECK_PING state");
		int score = calculateHand(player);
		log.debug(this.sid + " :: player " + player.getName() + " has a score of " + score);
		if (hasPing || score == 0) {
			player.setScore(player.getScore() + score);
			player.setState(PlayerState.END);
			log.info(this.sid + " :: setting " + player.getName() + "'s state to END");
		} else {
			player.setState(PlayerState.WAITING);
			log.info(this.sid + " :: setting " + player.getName() + "'s state to WAITING");
		}
	}

	/**
	 * Ends the player's turn
	 * @param player
	 */
	public void endTurn(PingPlayer player) throws PingException {
		if (!players.contains(player))
			throw new PingException("Invalid player: " + player.getName());
		if (player != this.getCurrentPlayer())
			throw new PingException("It is not " + player.getName() + "'s turn");
		if (player.getState() != PlayerState.WAITING &&
				player.getState() != PlayerState.END)
			throw new PingException("Player is not in WAITING or END state");
		currentTurn = (currentTurn + 1) % players.size();
		if (players.get(currentTurn).getState() != PlayerState.END) {
			players.get(currentTurn).setState(PlayerState.DRAW_CARD);
			log.info(this.sid + " :: setting " + this.getCurrentPlayer().getName() + "'s state to DRAW_CARD");
		} else {
			log.info(this.sid + " :: ending round");
			endRound();
		}
	}

	/**
	 * Ends the round
	 */
	public void endRound() throws PingException {
		if (!hasPing)
			throw new PingException("A Ping has not occurred");
		for (PingPlayer p : players)
			if (p.getState() != PlayerState.END)
				throw new PingException("Player " + p.getName() + " is not in END state");
		++this.round;
		log.info(this.sid + " :: setting round to " + this.round);
		currentTurn = round % players.size();
		players.get(currentTurn).setState(PlayerState.DEAL);
		log.info(this.sid + " :: setting " + this.getCurrentPlayer().getName() + "'s state to DEAL");
	}

	/**
	 * Returns the current round
	 * @return the current round
	 */
	public int getRound() {
		return round;
	}

	/**
	 * Returns whose turn it is
	 * @return the index
	 */
	public int getCurrentTurn() {
		return currentTurn;
	}

	/**
	 * Returns whose turn it is
	 * @return the player
	 */
	public PingPlayer getCurrentPlayer() {
		return players.get(currentTurn);
	}

	/**
	 * Returns all of the players
	 * @return all of the players
	 */
	public List<PingPlayer> getPlayers() {
		return players;
	}

	/**
	 * Returns the deck
	 * @return the deck
	 */
	public Deck getDeck() {
		return deck;
	}

	/**
	 * Returns the discardPile
	 * @return the discard pile
	 */
	public DiscardPile getDiscardPile() {
		return discardPile;
	}

	/**
	 * Checks if the player has ping
	 * @return true if the player has Ping
	 */
	private int calculateHand(PingPlayer player) throws PingException {
		int[] wildcards = { round - 1, (round + 12), (round + 25), (round + 38), 52 };
		int score = calculateHand(player.getHand().getCards(), new int[0], 0, wildcards);
		hasPing = hasPing || score == 0;
		return score;
	}

	/**
	 * Recursive build sets
	 * @param cards
	 * @param sets
	 * @param pos
	 * @param wildcards
	 * @return the lowest score
	 */
	private int calculateHand(Card[] cards, int[] sets, int pos, final int[] wildcards) throws PingException {

		// If not all cards have been grouped
		if (pos < cards.length) {

			// Start by making the card its own set
			int[] newsets = new int[sets.length + 1];
			System.arraycopy(sets, 0, newsets, 0, sets.length);
			newsets[sets.length] = addCard(0, cards[pos].getId());
			int min = calculateHand(cards, newsets, pos + 1, wildcards);

			// Try adding the card to other sets
			for (int i = 0; i < sets.length; ++i) {
				if (sizeOfSet(sets[i]) < 5) {
					int[] nsets = new int[sets.length];
					System.arraycopy(sets, 0, nsets, 0, sets.length);
					nsets[i] = addCard(nsets[i], cards[pos].getId());
					int score = calculateHand(cards, nsets, pos + 1, wildcards);
					if (score < min)
						min = score;
				}
			}

			// Return the minimum
			return min;
		}

		// Caculate individual sets
		if (sets.length == 0)
			throw new PingException("Unknown error calculating hand");
		int sum = calculateSet(sets[0], wildcards);
		for (int i = 1; i < sets.length; ++i)
			sum += calculateSet(sets[i], wildcards);
		return sum;
	}

	/**
	 * Returns the point value of the set
	 * @return the value of the set
	 */
	private int calculateSet(int set, final int[] wildcards) throws PingException {
		// Check if less than 3 cards
		if (getCard(set, 2) == -1)
			return addSet(set);
		
		// Assume it is a set
		if (isSet(set, wildcards, 0))
			return 0;
		else if (isRun(set, 0, wildcards, 0))
			return 0;
		else
			return addSet(set);
	}

	/**
	 * Adds up all of the cards in the set
	 * @param set
	 * @return sum of set
	 */
	private int addSet(int set) throws PingException {
		int id = getCard(set, 0);
		if (id == -1)
			throw new PingException("Not a valid set");
		int sum = 0;
		int i = 0;
		do {
			if (id == 52)
				sum += 50;
			else if (id % 13 ==  0)
				sum += 20;
			else if (id % 13 >= 10)
				sum += 10;
			else
				sum += (id % 13) + 1;
		} while (i + 1 < 5 && (id = getCard(set, ++i)) != -1);
		return sum;
	}

	/**
	 * Checks if the set is valid
	 * @param set
	 * @param wildcards
	 * @param numwilds
	 * @return true if a set
	 */
	private boolean isSet(int set, final int[] wildcards, int numwilds) throws PingException {
		int size = 0;
		for (int i = 0; i < 4; ++i) {
			int id1 = getCard(set, i);
			int id2 = getCard(set, i + 1);
			if (id1 % 13 == id2 % 13)
				continue;
			else if (isWild(id1, wildcards) && 
					isSet(setCard(set, id2, i), wildcards, numwilds + 1))
				return true;
			else if (isWild(id2, wildcards) &&
					isSet(setCard(set, id1, i + 1), wildcards, numwilds + 1))
				return true;
			else if (id2 == -1) {
				size = i + 1;
				break;
			}
			return false;
		}
		return size / numwilds >= 2;
	}

	/**
	 * Checks if the run is valid
	 * @param set
	 * @param wildcards
	 * @param numwilds
	 * @return true if a run
	 */
	private boolean isRun(int set, int run, final int[] wildcards, int numwilds) throws PingException {
		if (set == 0)
			return sizeOfSet(run) / numwilds >= 2;
		
		// If run is empty, look for first non-wild
		if (run == 0) {
			int spot = 0;
			while (spot < 5 && isWild(getCard(set, spot++), wildcards));
			if (spot == 5)
				return false;
			return isRun(setCard(set, -1, spot), setCard(run, getCard(set, spot), 0), wildcards, numwilds);
		}
		
		// Find low and high card
		int low = getCard(run, 0);
		if (isWild(low, wildcards)) {
			for (int i = 1; i < 5; ++i) {
				int c = getCard(run, i);
				if (!isWild(c, wildcards)) {
					low = c - i;
				}
			}
		}
		--low;
		int high = low + sizeOfSet(run) + 1;

		// Look for non-wild card that fits in set
		for (int i = 0; i < 5; ++i) {
			int c = getCard(set, i);
			if (low % 13 != 12 && c == low)
				return isRun(setCard(set, -1, i), setCard(run >> 6, c, 0), wildcards, numwilds);
			if (high % 13 != 0 && c == high)
				return isRun(setCard(set, -1, i), addCard(run, c), wildcards, numwilds);
		}

		// Look for wild
		for (int i = 0; i < 5; ++i) {
			int c = getCard(set, i);
			if (isWild(c, wildcards)) {
				if (isRun(setCard(set, -1, i), setCard(run >> 6, c, 0), wildcards, numwilds + 1))
					return true;
				if (isRun(setCard(set, -1, i), addCard(run, c), wildcards, numwilds + 1))
					return true;
			}
		}

		// Will be false if reaches here
		return false;
	}

	/**
	 * Returns the size of the set
	 * @param set
	 * @return size of the set
	 */
	private int sizeOfSet(int set) throws PingException {
		int size;
		for (size = 0; size < 5; ++size)
			if (getCard(set, size) == -1)
				return size;
		return size;
	}

	/**
	 * Returns the card id of the pos in the set
	 * @param set
	 * @param pos
	 * @return card at position pos
	 */
	private int getCard(int set, int pos) throws PingException {
		if (pos > 4)
			throw new PingException("Invalid position");
		return ((set >> (6 * pos)) & 0x3F) - 1;
	}

	/**
	 * Adds a card to the end of the set
	 * @param set
	 * @param id
	 * @return the new set
	 */
	private int addCard(int set, int id) throws PingException {
		if (id >= NUM_CARDS)
			throw new PingException("Card id does not exist");
		for (int i = 0; i < 5; ++i)
			if (getCard(set, i) == -1)
				return setCard(set, id, i);
		throw new PingException("The set is full");
	}

	/**
	 * Adds a card to position
	 * @param set
	 * @param id
	 * @param pos
	 * @return the new set
	 */
	private int setCard(int set, int id,  int pos) throws PingException {
		if (id >= NUM_CARDS)
			throw new PingException("Card id does not exist");
		if (pos >= 5)
			throw new PingException("Set index is out of bounds");
		set = ~(0x3F << (6 * pos)) & set;
		return ((id + 1) << (6 * pos)) | set;
	}

	/**
	 * Returns whether the card is wild
	 * @return true if wild
	 */
	private boolean isWild(int id, int[] wildcards) {
		for (int w : wildcards)
			if (id == w)
				return true;
		return false;
	}

	/**
	 * Returns the string representation of the state
	 * @param state
	 * @return string rep of the state
	 */
	public static String translateState(PlayerState state) {
		switch (state) {
			case WAITING:
				return "WAITING";
			case DEAL:
				return "DEAL";
			case DRAW_CARD:
				return "DRAW_CARD";
			case DISCARD:
				return "DISCARD";
			case CHECK_PING:
				return "CHECK_PING";
			case END:
				return "END";
			default:
				return "UNKNOWN";
		}
	}
}
