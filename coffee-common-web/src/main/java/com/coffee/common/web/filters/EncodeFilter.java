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

import org.eclipse.jetty.http.HttpHeader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

@Singleton
public class EncodeFilter implements Filter {

	private static final Logger logger = LoggerFactory.getLogger(EncodeFilter.class);

	@Inject(optional = true)
	@Named("webconfig.encodefilter.encoding")
	private String encoding = "UTF-8";

	@Override
	public void destroy() {
		logger.info("销毁EncodeFilter");
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest) req;
		HttpServletResponse response = (HttpServletResponse) res;
		request.setCharacterEncoding(this.encoding);
		response.setCharacterEncoding(this.encoding);
		response.addHeader(HttpHeader.SERVER.asString(), "coffee");
		response.addHeader(HttpHeader.SERVLET_ENGINE.asString(), "coffee Power");
		chain.doFilter(request, response);
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {
		logger.info("初始化EncodeFilter");
	}
}
