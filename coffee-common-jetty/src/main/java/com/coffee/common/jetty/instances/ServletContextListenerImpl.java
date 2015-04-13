/**
 *
 */
package com.coffee.common.jetty.instances;

import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.servlet.GuiceServletContextListener;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月7日
 */
@Singleton
public class ServletContextListenerImpl extends GuiceServletContextListener {

	@Inject
	private Injector injector;

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.servlet.GuiceServletContextListener#getInjector()
	 */
	@Override
	protected Injector getInjector() {
		return injector;
	}

}
