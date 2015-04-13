package com.coffee.common.web.exceptions;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.coffee.common.base.exceptioin.IExceptionHandler;

public abstract class AWebExceptionHandler implements IExceptionHandler {

	protected HttpServletRequest request;

	protected HttpServletResponse response;

	/**
	 * @param request
	 *            the request to set
	 */
	public void setRequest(final HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * @param response
	 *            the response to set
	 */
	public void setResponse(final HttpServletResponse response) {
		this.response = response;
	}
}
