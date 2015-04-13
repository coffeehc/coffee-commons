package com.coffee.common.web.transports;

public class JavascriptTransport extends TextTransport {

	@Override
	public String contentType() {
		return "text/javascript";
	}
}
