/**
 *
 */
package com.coffee.common.web.transports;

import com.google.inject.ImplementedBy;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年1月31日
 */
@ImplementedBy(TextTransport.class)
public abstract class Text implements Transport {

	@Override
	public String contentType() {
		return "text/plain";
	}
}
