/**
 *
 */
package com.coffee.common.web.headless;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import com.coffee.common.web.transports.Transport;
import com.google.inject.Injector;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年1月31日
 */
public abstract class Reply<E> {
	// 告诉
	public static final Reply<?> NO_REPLY = Reply.saying();

	/**
	 * 执行 302 重定向操作
	 */
	public abstract Reply<E> redirect(String uri);

	/**
	 * 重定向操作,code如果不为3xx,则会抛出异常
	 */
	public abstract Reply<E> seeOther(String uri, int statusCode);

	/**
	 * 设置返回内容的mediaType,默认为tex/plan.
	 */
	public abstract Reply<E> type(String mediaType);

	/**
	 * 为返回的内容设置Header
	 */
	public abstract Reply<E> headers(Map<String, String> headers);

	/**
	 * 指定结果的渲染输出方式
	 *
	 * <pre>
	 *   return Reply.with(new Person(..)).as(Xml.class);
	 * </pre>
	 * <p>
	 */
	public abstract Reply<E> as(Class<? extends Transport> transport);

	// /**
	// * Render template associated with the given class. The class must have
	// * an @Show() annotation pointing to a valid Sitebricks template type (can
	// * be any of the supported templates: MVEL, freemarker, SB, etc.)
	// * <p>
	// * The entity passed into with() is used as the template's context during
	// * render.
	// */
	// public abstract Reply<E> template(Class<?> templateKey);

	/**
	 * 设置当前的状态码
	 */
	public abstract Reply<E> status(int code);

	/**
	 * Used internally by sitebricks. Do NOT call.
	 */
	public abstract void populate(Injector injector, HttpServletResponse response) throws IOException;

	/**
	 * 创建一个Reply的实体,可以用于对定义方法的调用
	 *
	 * <pre>
	 * return Reply.saying().redirect(&quot;/other&quot;);
	 * </pre>
	 */
	public static <E> Reply<E> saying() {
		return new ReplyMaker<E>(null);
	}

	/**
	 * 返回一个有具体实体信息的Reply,可以用于对实体进行格式化输出.
	 *
	 * @param entity
	 *            An entity to send back for which a valid transport exists (see {@link #as(Class)}).
	 */
	public static <E> Reply<E> with(E entity) {
		return new ReplyMaker<E>(entity);
	}

}
