/**
 *
 */
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

import com.coffee.common.web.exceptions.PageNotFountException;
import com.coffee.common.web.headless.Forward;
import com.coffee.common.web.headless.Reply;
import com.coffee.common.web.routing.IRoutingDispatcher;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年1月31日
 */
@Singleton
public class DispatcherFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(DispatcherFilter.class);

	private final IRoutingDispatcher dispatcher;

	private final Injector injector;

	@Inject
	public DispatcherFilter(IRoutingDispatcher dispatcher, Injector injector) {
		this.dispatcher = dispatcher;
		this.injector = injector;
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("初始化DispatcherFilter");
		dispatcher.init();
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) servletRequest;
		HttpServletResponse response = (HttpServletResponse) servletResponse;
		logger.debug("接收{}请求,开始解析", request.getRequestURL());
		try {
			dispatcher(request, response);
		} catch (PageNotFountException e) {
			chain.doFilter(request, response);
		}
	}

	protected void dispatcher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		Object respondObject = dispatcher.dispatch(request);
		logger.debug("解析结果:{}", respondObject);
		if (respondObject == null) {
			respondObject = Reply.NO_REPLY;
		}
		// 处理返回的结果
		if (respondObject instanceof String) {
			respondObject = Reply.saying().redirect((String) respondObject);
			return;
		}
		if (respondObject instanceof Reply) {
			((Reply<?>) respondObject).populate(injector, response);
			return;
		}
		if (respondObject instanceof Forward) {
			request.getRequestDispatcher(((Forward) respondObject).getForwardUri()).forward(request, response);
			return;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		dispatcher.destroy();
		logger.info("销毁DispatcherFilter");
	}

}
