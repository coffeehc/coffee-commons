package com.coffee.common.web.exceptions;

import javax.servlet.http.HttpServletRequest;

import com.coffee.common.base.exceptioin.annotated.ExceptionHandler;
import com.coffee.common.web.exceptions.impl.PageNotFountExceptionHandler;

@ExceptionHandler(PageNotFountExceptionHandler.class)
public class PageNotFountException extends HandleRequestException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public PageNotFountException(HttpServletRequest request) {
		super("找不到" + request.getRequestURI() + "对应的处理方法");
	}
}
