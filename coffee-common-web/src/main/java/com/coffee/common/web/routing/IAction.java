/**
 *
 */
package com.coffee.common.web.routing;

import java.lang.reflect.Method;

import javax.servlet.http.HttpServletRequest;

import com.coffee.common.web.annotations.http.RequestMethodEunm;
import com.coffee.common.web.exceptions.HandleRequestException;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月9日
 */
public interface IAction {
	/**
	 * 执行处理
	 *
	 * @return
	 */
	public Object doAction(HttpServletRequest request) throws HandleRequestException;

	/**
	 * 判断是否匹配Uri
	 *
	 * @param uri
	 * @return
	 */
	public boolean match(String uri);

	/**
	 * 获取需要执行的方法
	 *
	 * @return
	 */
	public Method getMethod();

	/**
	 * 获取Action对应处理Request的请求方式
	 *
	 * @return
	 */
	public RequestMethodEunm getRequestMethod();

	/**
	 * 获取该Action处理的定义URI
	 * 
	 * @return
	 */
	public String getDefineUri();

}
