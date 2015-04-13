/**
 *
 */
package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.google.common.io.ByteStreams;
import com.google.inject.TypeLiteral;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月1日
 */
public class ByteArrayTransport extends Raw {

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#in(java.io.InputStream, java.lang.Class)
	 */
	@Override
	public <T> T in(InputStream in, Class<T> type) throws IOException {
		assert type == byte[].class;
		return (T) ByteStreams.toByteArray(in);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#in(java.io.InputStream, com.google.inject.TypeLiteral)
	 */
	@Override
	public <T> T in(InputStream in, TypeLiteral<T> type) throws IOException {
		assert type.getType() == byte[].class;
		return (T) ByteStreams.toByteArray(in);
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.transports.Transport#out(java.io.OutputStream, java.lang.Class, java.lang.Object)
	 */
	@Override
	public <T> void out(OutputStream out, Class<T> type, T data) throws IOException {
		assert data instanceof byte[];
		out.write((byte[]) data);

	}

}
