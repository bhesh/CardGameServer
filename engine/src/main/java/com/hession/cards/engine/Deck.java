package com.hession.cards.engine;

import java.util.ArrayList;
import java.util.Random;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 *
 * This class simulates a deck of cards
 */
public class Deck {

	private ArrayList<Card> playable;
	private ArrayList<Card> played;

	/**
	 * Constructor
	 * @Param cards
	 */
	public Deck(Card[] cards) {
		playable = new ArrayList<Card>(cards.length);
		played = new ArrayList<Card>(cards.length);
		for (Card c : cards)
			played.add(c);
	}

	/**
	 * Shuffles a new deck
	 */
	public void shuffle() {
		// Move all cards to played
		played.addAll(playable);
		playable.clear();

		// Randomly shuffle deck
		Random gen = new Random();
		while (!played.isEmpty())
			playable.add(played.remove(gen.nextInt(played.size())));
	}

	/**
	 * Deals a card
	 */
	public Card dealCard() throws DeckException {
		if (this.isEmpty())
			throw new DeckException("There are no more playable cards");
		Card card = playable.remove(playable.size() - 1);
		played.add(card);
		return card;
	}

	/**
	 * Returns True if the deck is empty
	 * @Returns True when deck is empty
	 */
	public boolean isEmpty() {
		return playable.isEmpty();
	}

	/**
	 * Adds a card to the deck as pos position
	 * @Param pos
	 * @Param card
	 * @Throws DeckException
	 */
	public void returnCard(int pos, Card card) throws DeckException {
		if (!played.contains(card))
			throw new DeckException("This card has not been played or does not exist");
		playable.add(pos, card);
		played.remove(card);
	}

	/**
	 * Adds a card to the top of the deck
	 * @Param card
	 * @Throws DeckException
	 */
	public void returnCardToTop(Card card) throws DeckException {
		this.returnCard(playable.size(), card);
	}

	/**
	 * Adds a card to the bottom of the deck
	 * @Param card
	 * @Throws DeckException
	 */
	public void returnCardToBottom(Card card) throws DeckException {
		this.returnCard(0, card);
	}

	/**
	 * Adds a card randomly to the deck
	 * @Param card
	 * @Throws DeckException
	 */
	public void returnCardRandomly(Card card) throws DeckException {
		this.returnCard(new Random().nextInt(playable.size() + 1), card);
	}

	/**
	 * Returns the total size of the deck
	 * @Return size of the deck
	 */
	public int size() {
		return playable.size() + played.size();
	}

	/**
	 * Returns the total size of the deck
	 * @Return size of the deck
	 */
	public int totalCards() {
		return this.size();
	}

	/**
	 * Returns the number of cards left
	 * @Return number of cards left
	 */
	public int totalCardsLeft() {
		return playable.size();
	}

	/**
	 * Returns the number of cards dealt
	 * @Return number of cards dealt
	 */
	public int totalCardsDealt() {
		return played.size();
	}

	/**
	 * Checks if a card has been dealt
	 * @Return true if the card has been dealt
	 */
	public boolean hasBeenDealt(Card card) {
		return played.contains(card);
	}

	/**
	 * Checks if a card is still in the deck
	 * @Return true if the card is in the deck
	 */
	public boolean isInDeck(Card card) {
		return playable.contains(card);
	}
}
