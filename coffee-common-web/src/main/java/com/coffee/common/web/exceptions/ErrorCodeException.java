/**
 *
 */
package com.coffee.common.web.exceptions;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月17日
 */
public class ErrorCodeException extends HandleRequestException {

	/**
	 *
	 */
	private static final long serialVersionUID = -5885865525147561677L;
	private int errorCode;

	/**
	 * @param message
	 */
	public ErrorCodeException(final String message, int errorCode) {
		super(message);
		this.errorCode = errorCode;
	}

	public int getErrorCode() {
		return errorCode;
	}

}
