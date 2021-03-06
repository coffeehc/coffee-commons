package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;
import com.google.inject.TypeLiteral;

public class TextTransport extends Text {

	@Override
	public <T> T in(final InputStream in, final Class<T> type)
			throws IOException {
		return type
				.cast(new String(ByteStreams.toByteArray(in), Charsets.UTF_8));
	}

	@Override
	public <T> void out(final OutputStream out, final Class<T> type,
			final T data) {
		try {
			ByteSource.wrap(data.toString().getBytes(Charsets.UTF_8)).copyTo(
					out);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public <T> T in(final InputStream in, final TypeLiteral<T> type)
			throws IOException {
		return (T) CharStreams.toString(new InputStreamReader(in,
				Charsets.UTF_8));
	}

}
