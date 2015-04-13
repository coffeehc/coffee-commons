/**
 *
 */
package com.coffee.common.base.exceptioin;

/**
 * 加载异常,属于运行时异常
 *
 * @author coffeehc@gmail.com
 *         create By 2015年2月7日
 */
public class LoadException extends CoffeeRuntimeException {

	/**
	 * @param message
	 * @param throwable
	 */
	public LoadException(String message, Throwable throwable) {
		super(message, throwable);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param message
	 */
	public LoadException(String message) {
		super(message);
	}

}
