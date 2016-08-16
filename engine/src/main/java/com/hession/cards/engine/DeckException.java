package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 */
public class DeckException extends IndexOutOfBoundsException {

	private static final long serialVersionUID = 1;

	/**
	 * Constructor
	 */
	public DeckException(String message) {
		super(message);
	}
}
