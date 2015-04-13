package com.coffee.common.web.exceptions;

import com.coffee.common.base.exceptioin.CoffeeRuntimeException;

/**
 * 处理请求事异常
 *
 * @author coffeehc@gmail.com
 *         create By 2015年2月11日
 */
public class HandleRequestException extends CoffeeRuntimeException {

	/**
	 * @param message
	 * @param throwable
	 */
	public HandleRequestException(String message, Throwable throwable) {
		super(message, throwable);
	}

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public HandleRequestException(final String message) {
		super(message);
	}

}
