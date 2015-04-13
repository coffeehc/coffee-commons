/**
 *
 */
package com.coffee.common.web.transports;

import com.google.inject.ImplementedBy;

/**
 * 支持application/octet-stream类型
 *
 * @author coffeehc@gmail.com
 *         create By 2015年2月1日
 */
@ImplementedBy(ByteArrayTransport.class)
public abstract class Raw implements Transport {

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#contentType()
	 */
	@Override
	public String contentType() {
		return "application/octet-stream";
	}

}
