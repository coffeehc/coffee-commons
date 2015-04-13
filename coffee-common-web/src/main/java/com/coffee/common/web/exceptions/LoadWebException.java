package com.coffee.common.web.exceptions;

import com.coffee.common.base.exceptioin.CoffeeRuntimeException;

/**
 * 加载web异常,运行时异常
 *
 * @author coffee
 */
public class LoadWebException extends CoffeeRuntimeException {

	public LoadWebException(final String message) {
		super(message);
	}

}
