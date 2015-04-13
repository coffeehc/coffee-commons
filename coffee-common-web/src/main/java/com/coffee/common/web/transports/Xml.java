/**
 *
 */
package com.coffee.common.web.transports;

import com.google.inject.ImplementedBy;

/**
 * 用户XML的输入输出
 * 
 * @author coffeehc@gmail.com
 *         create By 2015年2月1日
 */
@ImplementedBy(XStreamXmlTransport.class)
public abstract class Xml implements Transport {

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#contentType()
	 */
	@Override
	public String contentType() {
		return "text/xml";
	}

}
