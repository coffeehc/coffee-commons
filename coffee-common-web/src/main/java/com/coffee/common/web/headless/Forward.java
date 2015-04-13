/**
 *
 */
package com.coffee.common.web.headless;

/**
 * 内部重定向
 * 
 * @author coffeehc@gmail.com
 *         create By 2015年3月21日
 */
public class Forward {

	private String forwardUri;

	public String getForwardUri() {
		return forwardUri;
	}

	private Forward(String uri) {
		this.forwardUri = uri;
	}

	public static Forward forwardTo(String uri) {
		return new Forward(uri);
	}
}
