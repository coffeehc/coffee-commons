package com.coffee.common.base.exceptioin;

public class CoffeeException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 4613252922645765837L;

	public CoffeeException(final String message) {
		super(message);
	}

	public CoffeeException(final Throwable throwable) {
		super(throwable);
	}

	public CoffeeException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
