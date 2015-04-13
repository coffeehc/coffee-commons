package com.coffee.common.web.exceptions.impl;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.base.exceptioin.CoffeeException;
import com.coffee.common.web.exceptions.AWebExceptionHandler;

public class PageNotFountExceptionHandler extends AWebExceptionHandler {

	private final Logger logger = LoggerFactory
			.getLogger(PageNotFountExceptionHandler.class);

	@Override
	public void handlerException(final Throwable e) throws CoffeeException {
		logger.debug("处理404错误,没有找到页面:{}", request.getRequestURI());
		logger.debug("来源IP：{}", request.getRemoteAddr());
		try {
			response.sendError(404, "没有找到" + request.getRequestURI() + "请求的页面");
			// response.sendRedirect("404.html");
		} catch (IOException e1) {
			throw new CoffeeException(e1);
		}
	}
}
