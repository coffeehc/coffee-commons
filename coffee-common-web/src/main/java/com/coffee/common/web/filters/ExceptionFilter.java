package com.coffee.common.web.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.base.exceptioin.CoffeeException;
import com.coffee.common.base.exceptioin.IExceptionHandler;
import com.coffee.common.base.exceptioin.annotated.ExceptionHandler;
import com.coffee.common.web.exceptions.AWebExceptionHandler;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/*
 * 只用来处理FIlter异常,不处理400这些玩意
 */
@Singleton
public class ExceptionFilter implements Filter {

	private final Logger logger = LoggerFactory.getLogger(ExceptionFilter.class);
	@Inject
	private Injector injector;

	@Override
	public void init(final FilterConfig filterConfig) throws ServletException {
		logger.info("初始化ExceptionFilter");
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		try {
			chain.doFilter(req, res);
		} catch (Throwable e) {
			logger.debug("获取一个异常:{}", e.getMessage(), e);
			ExceptionHandler exceptionHandlerAnnotation = e.getClass().getAnnotation(ExceptionHandler.class);
			if (exceptionHandlerAnnotation != null) {
				IExceptionHandler exceptionHandler = injector.getInstance(exceptionHandlerAnnotation.value());
				if (exceptionHandler instanceof AWebExceptionHandler) {
					AWebExceptionHandler fistWebExceptionHandler = (AWebExceptionHandler) exceptionHandler;
					fistWebExceptionHandler.setRequest(req);
					fistWebExceptionHandler.setResponse(res);
				}
				try {
					exceptionHandler.handlerException(e);
				} catch (CoffeeException e1) {
					logger.error("处理异常出现错误:{}", e1.getMessage());
					throw new ServletException(e1);
				}
			} else {
				logger.warn("异常{}没有设置处理类.", e.getClass());
				throw new ServletException(e);
			}
		}
	}

	@Override
	public void destroy() {
		logger.info("销毁ExceptionFilter");
	}

	private Class<? extends Throwable> getRootThrowable(final Throwable e) {
		Throwable throwable = e.getCause();
		if (throwable == null || throwable instanceof CoffeeException) {
			return e.getClass();
		} else {
			return getRootThrowable(throwable);
		}
	}
}
