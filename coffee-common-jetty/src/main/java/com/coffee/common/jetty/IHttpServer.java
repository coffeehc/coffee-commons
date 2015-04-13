/**
 *
 */
package com.coffee.common.jetty;

import com.coffee.common.jetty.impl.HttpServerImpl;
import com.google.inject.ImplementedBy;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月7日
 */
@ImplementedBy(HttpServerImpl.class)
public interface IHttpServer {

	public void start() throws Exception;

	public void stop() throws Exception;

	public void waitToStop() throws InterruptedException;
}
