/**
 *
 */
package com.coffee.common.web.filters.ext;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.web.exceptions.PageNotFountException;
import com.coffee.common.web.filters.DispatcherFilter;
import com.coffee.common.web.routing.IRoutingDispatcher;
import com.google.common.base.Strings;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年3月23日
 */
@Singleton
public class AngularDispatcherFilter extends DispatcherFilter {

	private Logger logger = LoggerFactory.getLogger(AngularDispatcherFilter.class);

	@Inject(optional = true)
	@Named("webconfig.angularDispatcherFilter.postfix")
	private String routePostfix = "";

	private RouteCheck routeCheck;

	/**
	 * @param dispatcher
	 * @param injector
	 */
	@Inject
	public AngularDispatcherFilter(IRoutingDispatcher dispatcher, Injector injector) {
		super(dispatcher, injector);
		if (Strings.isNullOrEmpty(routePostfix)) {
			routeCheck = new NoPostFixRoute();
		} else {
			if (!routePostfix.startsWith(".")) {
				routePostfix = "." + routePostfix;
			}
			routeCheck = new PostFixRoute(routePostfix);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.filters.DispatcherFilter#dispatcher(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void dispatcher(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		try {
			super.dispatcher(request, response);
		} catch (PageNotFountException e) {
			String uri = request.getRequestURI();
			if (routeCheck.isRoute(uri.substring(uri.lastIndexOf("/")))) {
				logger.debug("angular的路由地址,直接forward到根目录");
				request.getRequestDispatcher("/").forward(request, response);
			} else {
				throw new PageNotFountException(request);
			}
		}
	}

	public interface RouteCheck {
		public boolean isRoute(String name);
	}

	private class NoPostFixRoute implements RouteCheck {

		/*
		 * (non-Javadoc)
		 * @see com.coffee.common.web.filters.ext.AngularDispatcherFilter.RouteCheck#isRoute(java.lang.String)
		 */
		@Override
		public boolean isRoute(String name) {
			return name.lastIndexOf(".") < 0;
		}
	}

	private class PostFixRoute implements RouteCheck {

		private String postFix;

		public PostFixRoute(String postFix) {
			this.postFix = postFix;
		}

		/*
		 * (non-Javadoc)
		 * @see com.coffee.common.web.filters.ext.AngularDispatcherFilter.RouteCheck#isRoute(java.lang.String)
		 */
		@Override
		public boolean isRoute(String name) {
			return name.endsWith(postFix);
		}
	}
}
