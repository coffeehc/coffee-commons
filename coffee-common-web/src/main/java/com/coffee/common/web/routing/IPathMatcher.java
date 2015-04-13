/**
 *
 */
package com.coffee.common.web.routing;

import java.lang.reflect.Method;

import com.coffee.common.base.core.interfaces.ILifecycleAware;
import com.coffee.common.web.routing.impl.PathMatcherImpl;
import com.google.inject.ImplementedBy;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月9日
 */
@ImplementedBy(PathMatcherImpl.class)
public interface IPathMatcher extends ILifecycleAware {

	public static final String PATH_SEPARATOR = "/";

	/*
	 * 通配符前缀
	 */
	public static final String WILDCARD_PREFIX = "{";

	/*
	 * 通配符后缀
	 */
	public static final String WILDCARD_SUFFIX = "}";

	/**
	 * 注册uri请求与方法的对应关系
	 *
	 * @param method
	 */
	public void regeditAt(Method method);

	/**
	 * @param uri
	 * @param httpMethod
	 * @return
	 */
	public IAction getAction(String uri, String httpMethod);

}
