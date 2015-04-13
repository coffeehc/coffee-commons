/**
 *
 */
package com.coffee.common.jetty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.jetty.instances.ServletContextListenerImpl;
import com.google.inject.AbstractModule;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月7日
 */
public class JettyModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(JettyModule.class);

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.AbstractModule#configure()
	 */
	@Override
	protected void configure() {
		logger.info("开始加载JettyModule");
		bind(GuiceServletContextListener.class).to(ServletContextListenerImpl.class);
		logger.info("加载JettyMoudle结束");
	}
}
