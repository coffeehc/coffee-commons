/**
 *
 */
package com.coffee.common.base.exceptioin;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月7日
 */
public class CoffeeRuntimeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -902289969747657625L;

	public CoffeeRuntimeException(final String message) {
		super(message);
	}

	public CoffeeRuntimeException(final Throwable throwable) {
		super(throwable);
	}

	public CoffeeRuntimeException(final String message, final Throwable throwable) {
		super(message, throwable);
	}
}
