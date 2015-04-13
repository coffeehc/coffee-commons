/**
 *
 */
package com.coffee.common.web.routing.impl;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.web.annotations.http.RequestMethodEunm;
import com.coffee.common.web.exceptions.LoadWebException;
import com.coffee.common.web.routing.IAction;
import com.coffee.common.web.routing.IPathMatcher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Singleton;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月9日
 */
@Singleton
public class PathMatcherImpl implements IPathMatcher {

	private static final Logger logger = LoggerFactory.getLogger(PathMatcherImpl.class);

	private Map<RequestMethodEunm, List<ActionImpl>> requestActions = Maps.newHashMap();

	@Inject
	private Injector injector;

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IPathMatcher#doAt(java.lang.String, java.lang.reflect.Method)
	 */
	@Override
	public void regeditAt(Method method) {
		ActionImpl requestAction = new ActionImpl(method, injector);
		RequestMethodEunm requestMethod = requestAction.getRequestMethod();
		List<ActionImpl> actions = requestActions.get(requestMethod);
		if (actions == null) {
			actions = Lists.newArrayList();
			requestActions.put(requestMethod, actions);
		} else {
			for (ActionImpl action : actions) {
				if (action.getConversionUri().equals(requestAction.getConversionUri())) {
					logger.error("方法[{}]与方法[{}]定义的uri冲突", action.getMethod().toGenericString(), requestAction.getMethod().toGenericString());
					throw new LoadWebException(String.format("方法[%s]与方法[%s]定义的uri冲突", action.getMethod().toGenericString(), requestAction.getMethod()
							.toGenericString()));
				}
			}
		}
		actions.add(requestAction);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IPathMatcher#getAction(java.lang.String, java.lang.String)
	 */
	@Override
	public IAction getAction(String uri, String httpMethod) {
		RequestMethodEunm method = RequestMethodEunm.valueOf(httpMethod);
		List<ActionImpl> actions = requestActions.get(method);
		if (actions == null) {
			return null;
		}
		String[] paths = uri.split(PATH_SEPARATOR);
		for (ActionImpl action : actions) {
			if (action.getPathSize() == paths.length && action.match(uri)) {
				return action;
			}
		}
		return null;
	}

	private void sort() {
		RequestMethodEunm[] methods = RequestMethodEunm.values();
		for (RequestMethodEunm method : methods) {
			List<ActionImpl> actions = requestActions.get(method);
			if (actions != null) {
				Collections.sort(actions, new Comparator<ActionImpl>() {
					@Override
					public int compare(ActionImpl o1, ActionImpl o2) {
						String[] uri1s = o1.getConversionUri().split(PATH_SEPARATOR);
						String[] uri2s = o2.getConversionUri().split(PATH_SEPARATOR);
						if (uri1s.length != uri2s.length) {
							return uri1s.length - uri2s.length;
						}
						int size = uri1s.length > uri2s.length ? uri2s.length : uri1s.length;
						for (int i = 0; i < size; i++) {
							String uri1 = uri1s[i];
							String uri2 = uri2s[i];
							if (!uri1.equals(uri2)) {
								if (uri1.equals(ActionImpl.conversion)) {
									return 1;
								}
								if (uri2.equals(ActionImpl.conversion)) {
									return -1;
								}
								return uri1.compareTo(uri2);
							}
						}
						return 0;
					}
				});
				for (IAction action : actions) {
					logger.debug("排序后的defineUri:{}", action.getDefineUri());
				}
			}
		}

	}

	private AtomicBoolean isInit = new AtomicBoolean(false);

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#init()
	 */
	@Override
	public void init() {
		if (isInit.compareAndSet(false, true)) {
			sort();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#destroy()
	 */
	@Override
	public void destroy() {
		requestActions = Maps.newHashMap();
		isInit.set(false);

	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.base.core.interfaces.ILifecycleAware#isInited()
	 */
	@Override
	public boolean isInited() {
		return isInit.get();
	}
}
