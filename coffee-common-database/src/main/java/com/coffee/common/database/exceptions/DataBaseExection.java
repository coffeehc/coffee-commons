/**
 *
 */
package com.coffee.common.database.exceptions;

import com.coffee.common.base.exceptioin.CoffeeException;

/**
 * @author coffee
 */
public class DataBaseExection extends CoffeeException {

	public DataBaseExection(final String message) {
		super(message);
	}

	public DataBaseExection(final Throwable throwable) {
		super(throwable);
	}

	public DataBaseExection(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
