package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.inject.TypeLiteral;

public class JsonTransport implements Transport {

	@Override
	public <T> T in(final InputStream in, final Class<T> type) throws IOException {
		byte[] bytes = ByteStreams.toByteArray(in);
		return JSON.parseObject(bytes, type);
	}

	@Override
	public <T> void out(final OutputStream out, final Class<T> type, final T data) throws IOException {
		ByteSource from = ByteSource.wrap(JSON.toJSONStringWithDateFormat(data, "yyyy-MM-dd").getBytes(Charsets.UTF_8));
		from.copyTo(out);
	}

	@Override
	public <T> T in(final InputStream in, final TypeLiteral<T> type) throws IOException {
		byte[] bytes = ByteStreams.toByteArray(in);
		return JSON.parseObject(bytes, type.getType());
	}

	@Override
	public String contentType() {
		return "application/json";
	}
}
