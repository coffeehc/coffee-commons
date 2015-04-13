/**
 *
 */
package com.coffee.common.web.headless;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.coffee.common.web.transports.Text;
import com.coffee.common.web.transports.Transport;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.collect.Maps;
import com.google.common.io.ByteStreams;
import com.google.inject.Injector;
import com.google.inject.Key;

/**
 * @author coffeehc@gmail.com create By 2015年1月31日
 */
class ReplyMaker<E> extends Reply<E> {

	int status = HttpServletResponse.SC_OK;

	String contentType;

	String redirectUri;
	Map<String, String> headers = Maps.newHashMap();

	Key<? extends Transport> transport = Key.get(Text.class);
	E entity;

	// Class<?> templateKey;

	public ReplyMaker(E entity) {
		this.entity = entity;
	}

	@Override
	public Reply<E> seeOther(String uri, int statusCode) {
		Preconditions.checkArgument(statusCode >= 300 && statusCode < 400,
				"Redirect statuses must be between 300-399");
		redirectUri = uri;
		status = statusCode;
		return this;
	}

	@Override
	public Reply<E> type(String mediaType) {
		Preconditions.checkArgument(Strings.isNullOrEmpty(mediaType),
				"Media type cannot be null or empty");
		this.contentType = mediaType;
		return this;
	}

	@Override
	public Reply<E> headers(Map<String, String> headers) {
		this.headers.putAll(headers);
		return this;
	}

	@Override
	public Reply<E> as(Class<? extends Transport> transport) {
		Preconditions.checkArgument(null != transport,
				"Transport class cannot be null!");
		this.transport = Key.get(transport);
		return this;
	}

	@Override
	public Reply<E> redirect(String url) {
		Preconditions.checkArgument(!Strings.isNullOrEmpty(url),
				"Redirect URL must be non empty!");
		this.redirectUri = url;
		status = HttpServletResponse.SC_MOVED_TEMPORARILY;
		return this;
	}

	@Override
	public Reply<E> status(int code) {
		status = code;
		return this;
	}

	// @Override
	// public Reply<E> template(Class<?> templateKey) {
	// this.templateKey = templateKey;
	// return this;
	// }

	@Override
	@SuppressWarnings("unchecked")
	public void populate(Injector injector, HttpServletResponse response)
			throws IOException {
		if (Reply.NO_REPLY == this) {
			return;
		}
		Transport transport = injector.getInstance(this.transport);
		if (!headers.isEmpty()) {
			for (Map.Entry<String, String> header : headers.entrySet()) {
				response.setHeader(header.getKey(), header.getValue());
			}
		}
		if (response.getContentType() == null) {
			if (null == contentType) {
				response.setContentType(transport.contentType());
			} else {
				response.setContentType(contentType);
			}
		}
		if (null != redirectUri) {
			response.sendRedirect(redirectUri);
			response.setStatus(status);
			return;
		}
		response.setStatus(status);
		// if (null != templateKey) {
		// response.getWriter().write(
		// injector.getInstance(Templates.class).render(templateKey,
		// entity));
		// } else
		if (null != entity) {
			if (entity instanceof InputStream) {
				InputStream inputStream = (InputStream) entity;
				try {
					ByteStreams.copy(inputStream, response.getOutputStream());
				} finally {
					inputStream.close();
				}
			} else {
				transport.out(response.getOutputStream(),
						(Class<E>) entity.getClass(), entity);
			}
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof ReplyMaker<?>)) {
			return false;
		}

		@SuppressWarnings("unchecked")
		ReplyMaker<E> o = (ReplyMaker<E>) other;
		if (this.status != o.status) {
			return false;
		}

		if ((this.contentType != o.contentType)
				&& (this.contentType != null && !this.contentType
						.equals(o.contentType))
				&& (this.contentType == null && o.contentType != null)) {
			return false;
		}

		if ((this.redirectUri != o.redirectUri)
				&& (this.redirectUri != null && !this.redirectUri
						.equals(o.redirectUri))
				&& (this.redirectUri == null && o.redirectUri != null)) {
			return false;
		}

		if (!this.headers.equals(o.headers)) {
			return false;
		}

		if (!this.transport.equals(o.transport)) {
			return false;
		}

		// if (this.templateKey != o.templateKey)
		// return false;

		if ((this.entity != o.entity)
				&& (this.entity != null && !this.entity.equals(o.entity))
				&& (this.entity == null && o.entity != null)) {
			return false;
		}

		return true;
	}
}
