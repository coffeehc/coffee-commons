/**
 *
 */
package com.coffee.common.base.exceptioin;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月8日
 */
public class PackageScanFailedException extends CoffeeRuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -6770127321320935430L;

	/**
	 * @param message
	 * @param throwable
	 */
	public PackageScanFailedException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 * @param message
	 */
	public PackageScanFailedException(String message) {
		super(message);
	}

}
