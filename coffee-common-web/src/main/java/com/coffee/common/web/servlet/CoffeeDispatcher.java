package com.coffee.common.web.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 用于处理request请求调度的核心类
 * @author coffeehc@gmail.com
 * create By 2015年1月31日
 */
public interface CoffeeDispatcher {
	/**
	 * 初始化
	 */
	void init();

    void service(HttpServletRequest request, HttpServletResponse response);

    void destroy();
    
    public HttpServletRequest currentRequest();

    public HttpServletResponse currentResponse();
}
