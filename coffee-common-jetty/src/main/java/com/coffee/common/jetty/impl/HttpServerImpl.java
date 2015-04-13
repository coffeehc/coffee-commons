/**
 *
 */
package com.coffee.common.jetty.impl;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.SessionManager;
import org.eclipse.jetty.server.Slf4jRequestLog;
import org.eclipse.jetty.server.handler.ErrorHandler;
import org.eclipse.jetty.server.session.HashSessionManager;
import org.eclipse.jetty.server.session.SessionHandler;
import org.eclipse.jetty.servlet.ErrorPageErrorHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.util.log.Slf4jLog;
import org.slf4j.LoggerFactory;

import com.coffee.common.jetty.IHttpServer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.inject.servlet.GuiceFilter;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月5日
 */
@Singleton
public class HttpServerImpl implements IHttpServer {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(HttpServerImpl.class);

	@Named("httpserver.port")
	@Inject(optional = true)
	private int port = 8888;

	@Named("httpserver.context")
	@Inject(optional = true)
	private String contextPath = "/";

	@Named("httpserver.displayName")
	@Inject(optional = true)
	private String displayName = "coffee_server";

	@Named("httpserver.resourcebase")
	@Inject(optional = true)
	private String resourceBase = ".";
	@Inject
	private GuiceServletContextListener servletContextListenter;

	private Server server;

	@Override
	public void start() throws Exception {
		long t1 = System.currentTimeMillis();
		logger.info("开始启动服务器[{}]", displayName, port);
		ServletContextHandler servletContextHandler = new ServletContextHandler(null, contextPath, 4);
		servletContextHandler.setResourceBase(".");
		servletContextHandler.setClassLoader(Thread.currentThread().getContextClassLoader());
		servletContextHandler.setDisplayName(displayName);
		servletContextHandler.setLogger(new Slf4jLog("com.coffee.common.jetty.log"));
		servletContextHandler.setAllowNullPathInfo(false);
		servletContextHandler.setStopTimeout(10000);
		servletContextHandler.addEventListener(servletContextListenter);
		EnumSet<DispatcherType> set = EnumSet.noneOf(DispatcherType.class);
		set.add(DispatcherType.REQUEST);
		set.add(DispatcherType.FORWARD);
		set.add(DispatcherType.INCLUDE);
		set.add(DispatcherType.ERROR);
		set.add(DispatcherType.ASYNC);
		servletContextHandler.addFilter(GuiceFilter.class, "/*", set);
		servletContextHandler.addLocaleEncoding("zh", "UTF-8");
		ErrorHandler errorHandler = new ErrorPageErrorHandler();
		// errorHandler.setShowMessageInTitle();
		servletContextHandler.setErrorHandler(errorHandler);
		SessionManager sessionManager = new HashSessionManager();
		sessionManager.setMaxInactiveInterval(30 * 60);
		servletContextHandler.setSessionHandler(new SessionHandler(sessionManager));
		server = new Server(port);
		Slf4jRequestLog requestLog = new Slf4jRequestLog();
		requestLog.setLoggerName("com.coffee.common.jetty.requeseLog");
		requestLog.setLogServer(true);
		server.setRequestLog(requestLog);
		server.setHandler(servletContextHandler);
		server.start();
		long t2 = System.currentTimeMillis();
		logger.info("启动服务器[{}]成功,耗时:{}毫秒", port, (t2 - t1));
		Connector[] connectors = server.getConnectors();
		for (Connector connector : connectors) {
			logger.info("连接信息{}", connector);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.jetty.IHttpServer#stop()
	 */
	@Override
	public void stop() throws Exception {
		if (server != null) {
			server.stop();
			logger.info("服务器停止");
		}

	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.jetty.IHttpServer#waitToStop()
	 */
	@Override
	public void waitToStop() throws InterruptedException {
		if (server != null) {
			logger.debug("等待服务器关闭");
			server.join();
			logger.info("服务器关闭");
		}
	}

}
