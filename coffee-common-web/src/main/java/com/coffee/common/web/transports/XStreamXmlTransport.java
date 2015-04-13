/**
 *
 */
package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.thoughtworks.xstream.XStream;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月1日
 */
public class XStreamXmlTransport extends Xml {

	private final XStream xStream;

	@Inject
	public XStreamXmlTransport(XStream xStream) {
		this.xStream = xStream;
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#in(java.io.InputStream, java.lang.Class)
	 */
	@Override
	public <T> T in(InputStream in, Class<T> type) throws IOException {
		return type.cast(xStream.fromXML(in));
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#in(java.io.InputStream, com.google.inject.TypeLiteral)
	 */
	@Override
	public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
		return (T) xStream.fromXML(in);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#out(java.io.OutputStream, java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> void out(OutputStream out, Class<T> type, T data) throws IOException {
		xStream.toXML(data, out);
	}

}
