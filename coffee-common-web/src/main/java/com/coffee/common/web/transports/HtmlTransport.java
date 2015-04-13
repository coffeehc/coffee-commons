package com.coffee.common.web.transports;

public class HtmlTransport extends TextTransport {

	@Override
	public String contentType() {
		return "text/html";
	}
}
