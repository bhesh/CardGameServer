package com.hession.cards.engine;

/**
 * @Author Brian Hession
 * Email: hessionb@gmail.com
 */
public class HandException extends IndexOutOfBoundsException {

	private static final long serialVersionUID = 1;

	/**
	 * Constructor
	 */
	public HandException(String message) {
		super(message);
	}
}
