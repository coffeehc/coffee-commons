/**
 *
 */
package com.coffee.common.web.filters;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpStatus;
import org.eclipse.jetty.http.MimeTypes;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.base.AMainModule;
import com.coffee.common.web.annotations.http.RequestMethodEunm;
import com.coffee.common.web.exceptions.ErrorCodeException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月2日
 */
@Singleton
public class StaticResourceFilter implements Filter {
	@Inject
	@Named(AMainModule.NAMED_ISPRODUCTION)
	private boolean isProduction;

	private static final Logger logger = LoggerFactory.getLogger(StaticResourceFilter.class);

	protected Resource baseResource;
	/**
	 * 指定是否开启eTags
	 */
	@Inject(optional = true)
	@Named("webconfig.staticfilter.etags")
	private boolean _etags = true;
	/**
	 * 指定Cache-Control的值
	 */
	@Inject(optional = true)
	@Named("webconfig.staticfilter.cacheControl")
	private String _cacheControl;

	@Inject
	@Named("webconfig.staticfilter.resourceidr")
	private String resourceDir;

	private MimeTypes _mimeTypes = new MimeTypes();

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		logger.info("初始化静态资源Filter");
		try {
			Resource.setDefaultUseCaches(isProduction);
			baseResource = Resource.newResource(resourceDir);
		} catch (MalformedURLException e) {
			throw new ServletException(e);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		logger.debug("从缓存中取出静态资源,{}", req.getRequestURI());
		boolean skipContentBody = false;
		RequestMethodEunm requestMethod = RequestMethodEunm.valueOf(req.getMethod().toUpperCase());
		if (!requestMethod.equals(RequestMethodEunm.GET)) {
			if (!requestMethod.equals(RequestMethodEunm.HEAD)) {
				// try another handler
				chain.doFilter(request, response);
				return;
			}
			skipContentBody = true;
		}

		Resource resource = baseResource.addPath(req.getRequestURI());
		logger.debug("resource={} alias={} exists={}", resource, resource.getAlias(), resource.exists());
		// If resource is not found
		if (resource == null || !resource.exists())
		{
			chain.doFilter(request, response);
			return;
		}
		// handle directories
		if (resource.isDirectory()) {
			// 默认不显示目录内容
			throw new ErrorCodeException("没有访问目录的权限", 403);
		}

		// Handle ETAGS
		long last_modified = resource.lastModified();
		String etag = null;
		if (_etags)
		{
			// simple handling of only a single etag
			String ifnm = req.getHeader(HttpHeader.IF_NONE_MATCH.asString());
			etag = resource.getWeakETag();
			if (ifnm != null && resource != null && ifnm.equals(etag))
			{
				res.setStatus(HttpStatus.NOT_MODIFIED_304);
				res.addHeader(HttpHeader.ETAG.asString(), etag);
				return;
			}
		}

		// Handle if modified since
		if (last_modified > 0)
		{
			long if_modified = req.getDateHeader(HttpHeader.IF_MODIFIED_SINCE.asString());
			if (if_modified > 0 && last_modified / 1000 <= if_modified / 1000)
			{
				res.setStatus(HttpStatus.NOT_MODIFIED_304);
				return;
			}
		}

		// set the headers
		String mime = _mimeTypes.getMimeByExtension(resource.toString());
		if (mime == null) {
			mime = _mimeTypes.getMimeByExtension(req.getPathInfo());
		}
		doResponseHeaders(res, resource, mime);
		if (_etags) {
			res.addHeader(HttpHeader.ETAG.asString(), etag);
		}

		if (skipContentBody) {
			return;
		}
		// Send the content
		OutputStream out = response.getOutputStream();
		resource.writeTo(out, 0, resource.length());
	}

	/**
	 * Set the response headers.
	 * This method is called to set the response headers such as content type and content length.
	 * May be extended to add additional headers.
	 *
	 * @param response
	 * @param resource
	 * @param mimeType
	 */
	protected void doResponseHeaders(HttpServletResponse response, Resource resource, String mimeType)
	{
		if (mimeType != null) {
			response.setContentType(mimeType);
		}
		long length = resource.length();
		if (length > Integer.MAX_VALUE) {
			response.setHeader(HttpHeader.CONTENT_LENGTH.asString(), Long.toString(length));
		} else if (length > 0) {
			response.setContentLength((int) length);
		}
		if (_cacheControl != null) {
			response.setHeader(HttpHeader.CACHE_CONTROL.asString(), _cacheControl);
		}
	}

	/*
	 * (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
		logger.info("销毁静态资源Filter");
	}

}
