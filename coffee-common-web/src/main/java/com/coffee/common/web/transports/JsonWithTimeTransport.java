package com.coffee.common.web.transports;

import java.io.IOException;
import java.io.OutputStream;

import com.alibaba.fastjson.JSON;
import com.google.common.base.Charsets;
import com.google.common.io.ByteSource;

public class JsonWithTimeTransport extends JsonTransport {

	@Override
	public <T> void out(final OutputStream out, final Class<T> type,
			final T data) throws IOException {
		ByteSource from = ByteSource.wrap(JSON.toJSONStringWithDateFormat(data,
				"yyyy-MM-dd HH:mm:ss").getBytes(Charsets.UTF_8));
		from.copyTo(out);
	}

}
