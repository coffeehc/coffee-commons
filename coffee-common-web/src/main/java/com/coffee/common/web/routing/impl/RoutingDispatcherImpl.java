/**
 *
 */
package com.coffee.common.web.routing.impl;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.servlet.http.HttpServletRequest;

import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.web.annotations.At;
import com.coffee.common.web.annotations.http.HttpMethod;
import com.coffee.common.web.exceptions.LoadWebException;
import com.coffee.common.web.exceptions.PageNotFountException;
import com.coffee.common.web.routing.IAction;
import com.coffee.common.web.routing.IPathMatcher;
import com.coffee.common.web.routing.IRoutingDispatcher;
import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月8日
 */
@Singleton
public class RoutingDispatcherImpl implements IRoutingDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(RoutingDispatcherImpl.class);

	private AtomicBoolean inited = new AtomicBoolean(false);

	@Inject
	@Named(NAMEED_SCAN_PACKAGES)
	private List<String> packagePaths;
	@Inject
	private IPathMatcher pathMatcher;

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IRoutingDispatcher#dispatch(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	public Object dispatch(HttpServletRequest request) {
		IAction action = pathMatcher.getAction(request.getRequestURI(), request.getMethod().toUpperCase());
		if (action == null) {
			throw new PageNotFountException(request);
		}
		return action.doAction(request);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#init()
	 */
	@Override
	public void init() {
		if (!inited.compareAndSet(false, true)) {
			logger.warn("路由分配已经初始化,不需要再次进行初始化");
			return;
		}
		Set<Class<?>> atClasses = Sets.newHashSet();
		for (String packagePath : packagePaths) {
			logger.debug("扫描包:{}", packagePath);
			packagePath = packagePath.replace(".", "/");
			Resource resource = Resource.newClassPathResource(packagePath);
			if (resource == null) {
				logger.warn("{}不存在,不进行扫描", packagePath);
				continue;
			}
			Collection<Resource> resources = resource.getAllResources();
			for (Resource r : resources) {
				String name = r.getName();
				if (name.endsWith(".class")) {
					name = name.substring(name.indexOf(packagePath)).replace("/", ".").replace(".class", "");
					Class<?> clasz;
					try {
						clasz = Class.forName(name);
					} catch (ClassNotFoundException e) {
						throw new LoadWebException("类" + name + "不存在");
					}
					logger.debug("加载{}", name);
					if (clasz.getAnnotation(At.class) != null) {
						atClasses.add(clasz);
					}
				}
			}
		}
		complieAction(atClasses);
		pathMatcher.init();
		inited.set(true);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#destroy()
	 */
	@Override
	public void destroy() {
		// TODO Auto-generated method stub
		inited.set(false);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#isInited()
	 */
	@Override
	public boolean isInited() {
		return inited.get();
	}

	private void complieAction(Set<Class<?>> classes) {
		for (Class<?> atclass : classes) {
			At at = atclass.getAnnotation(At.class);
			if (at == null) {
				logger.warn("{}没有指定@At", atclass.getName());
				continue;
			}
			String uri = at.value();
			uri = uri.endsWith(IPathMatcher.PATH_SEPARATOR) ? uri.substring(0, uri.length() - 1) : uri;
			for (Method method : atclass.getMethods()) {
				if (method.isAnnotationPresent(HttpMethod.class)) {
					pathMatcher.regeditAt(method);
				}
			}
		}
	}

}
