/**
 *
 */
package com.coffee.common.base.exceptioin;


/**
 * @author coffee
 *
 */
public class ServiceExection extends CoffeeException {

	public ServiceExection(final String message) {
		super(message);
	}

	public ServiceExection(final Throwable throwable) {
		super(throwable);
	}

	public ServiceExection(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
