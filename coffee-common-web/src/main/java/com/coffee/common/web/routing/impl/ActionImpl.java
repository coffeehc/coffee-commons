/**
 *
 */
package com.coffee.common.web.routing.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.web.annotations.At;
import com.coffee.common.web.annotations.http.HttpMethod;
import com.coffee.common.web.annotations.http.RequestMethodEunm;
import com.coffee.common.web.exceptions.HandleRequestException;
import com.coffee.common.web.exceptions.LoadWebException;
import com.coffee.common.web.routing.IAction;
import com.coffee.common.web.routing.IPathMatcher;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.inject.BindingAnnotation;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.spi.TypeConverterBinding;

/**
 * 对调用方法的封装
 *
 * @author coffeehc@gmail.com
 *         create By 2015年2月12日
 */
public class ActionImpl implements IAction {
	/**
	 * 将通配符替换的正则
	 */
	public static String conversion = "[A-Za-z0-9]*";

	private static Logger logger = LoggerFactory.getLogger(ActionImpl.class);
	/**
	 * 执行的方法
	 */
	private Method method;
	/**
	 * 方法所在的类
	 */
	private Class<?> handleClass;
	/**
	 * request请求的方式
	 */
	private RequestMethodEunm requestMethod;
	/**
	 * 真实的请求URI定义
	 */
	private String defineUri;
	/**
	 * 匹配的正则
	 */
	private Pattern uriPattern;
	/**
	 * uri的个数
	 */
	private int pathSize = 0;
	/**
	 * 执行方法需要绑定的参数
	 */
	private BindArgs[] bindArgs;

	private Injector injector;
	/**
	 * 方法参数的定义
	 */
	private List<UriConversion> uriConversions;

	private boolean hasPathFragments = false;

	private static final int WILDCARD_PREFIX_LENGTH = IPathMatcher.WILDCARD_PREFIX.length();

	private static final int WILDCARD_SUFFIX_LENGTH = WILDCARD_PREFIX_LENGTH + IPathMatcher.WILDCARD_SUFFIX.length() - 1;

	/**
	 * @param method
	 */
	public ActionImpl(Method method, Injector injector) {
		this.injector = injector;
		this.method = method;
		if (method.isAccessible()) {
			logger.error("指定的方法{}不可访问", method.toGenericString());
			throw new LoadWebException(String.format("指定的方法%s不可访问", method.toGenericString()));
		}
		handleClass = method.getDeclaringClass();
		HttpMethod httpMethod = method.getAnnotation(HttpMethod.class);
		requestMethod = httpMethod.value();
		initUri();
		initHandler();
		logger.debug("初始化处理{}请求的方法{}的元数据", requestMethod, method.toGenericString());
	}

	private void initHandler() {
		Annotation[][] annotationses = this.method.getParameterAnnotations();
		Class<?>[] claszes = this.method.getParameterTypes();
		bindArgs = new BindArgs[claszes.length];
		for (int i = 0; i < claszes.length; i++) {
			Class<?> clasz = claszes[i];
			Annotation[] annotations = annotationses[i];
			if (annotations.length != 0) {
				for (Annotation annotation : annotations) {
					if (annotation.annotationType().isAnnotationPresent(BindingAnnotation.class)) {
						bindArgs[i] = new BindNamedParam(injector, clasz, annotation);
						continue;
					}
					// 可以考虑在这里扩展注解,将path参数与请求参数分离
				}
			}
			if (bindArgs[i] == null) {
				bindArgs[i] = new BindObject(injector, clasz);
			}
		}
	}

	private static String getBindingAnnotationValue(Annotation annotation) {
		try {
			Method valueMethod = annotation.getClass().getMethod("value");
			return (String) valueMethod.invoke(annotation);
		} catch (Exception e) {
			logger.error("指定的BindingAnnotation没有value()方法,不能获取指定的参数名称");
			throw new LoadWebException("获取BindingAnnotation的值失败:" + annotation.getClass());
		}
	}

	private void initUri() {
		At at = method.getDeclaringClass().getAnnotation(At.class);
		if (at == null) {
			logger.error("方法{}所在的类没有定义@At", method.toGenericString());
			throw new LoadWebException(String.format("方法%s所在的类没有定义@At", method.toGenericString()));
		}
		At subAt = method.getAnnotation(At.class);
		if (subAt == null) {
			defineUri = at.value();
		} else {
			defineUri = (at.value().equals("/") ? "" : at.value()) + subAt.value();
		}
		pathSize = defineUri.split(IPathMatcher.PATH_SEPARATOR).length;
		uriConversions = Lists.newArrayList();
		String[] paths = defineUri.split(IPathMatcher.PATH_SEPARATOR);
		String conversionUri = "";
		for (int i = 0; i < paths.length; i++) {
			String path = paths[i];
			if (Strings.isNullOrEmpty(path)) {
				continue;
			}
			String conversionPath = path;
			if (path.startsWith(IPathMatcher.WILDCARD_PREFIX) && path.endsWith(IPathMatcher.WILDCARD_SUFFIX)) {
				String name = path.substring(WILDCARD_PREFIX_LENGTH, path.length() - WILDCARD_SUFFIX_LENGTH);
				uriConversions.add(new UriConversion(i, name));
				conversionPath = conversion;
			}
			conversionUri += (IPathMatcher.PATH_SEPARATOR + conversionPath);
		}
		hasPathFragments = uriConversions.size() == 0 ? false : true;
		if (Strings.isNullOrEmpty(conversionUri)) {
			conversionUri = "/";
		}
		uriPattern = Pattern.compile(conversionUri);
	}

	int getPathSize() {
		return pathSize;
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IAction#getMethod()
	 */
	@Override
	public Method getMethod() {
		return method;
	}

	/**
	 * 将uri中得参数片段提取出来
	 *
	 * @param requestUri
	 * @return
	 * @throws HandleRequestException
	 */
	private Map<String, String> getPathFragments(String requestUri) throws HandleRequestException {
		Map<String, String> param = Maps.newHashMap();
		if (hasPathFragments) {
			String[] paths = requestUri.split(IPathMatcher.PATH_SEPARATOR);
			if (pathSize != paths.length) {
				logger.error("需要解析的uri[{}]不匹配定义的uri[{}]", requestUri, this.defineUri);
				throw new HandleRequestException(String.format("需要解析的uri[%s]不匹配定义的uri[%s]", requestUri, this.defineUri));
			}

			for (UriConversion uriConversion : uriConversions) {
				param.put(uriConversion.name, paths[uriConversion.index]);
			}
		}
		return param;

	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IAction#getRequestMethod()
	 */
	@Override
	public RequestMethodEunm getRequestMethod() {
		return requestMethod;
	}

	/**
	 * Uri中需要转换的类型
	 *
	 * @author coffeehc@gmail.com
	 *         create By 2015年2月23日
	 */
	private class UriConversion {

		private UriConversion(int index, String name) {
			this.index = index;
			this.name = name;
		}

		int index;
		String name;
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IAction#match(java.lang.String)
	 */
	@Override
	public boolean match(String uri) {
		return uriPattern.matcher(uri).matches();
	}

	public String getConversionUri() {
		return uriPattern.pattern();
	}

	/**
	 * 构建执行方法需要的参数
	 *
	 * @param request
	 * @param pathFragments
	 * @return
	 */
	private Object[] budilMethodArgs(HttpServletRequest request, Map<String, String> pathFragments) {
		Object[] args = new Object[this.bindArgs.length];
		for (int i = 0; i < this.bindArgs.length; i++) {
			args[i] = this.bindArgs[i].bind(request, pathFragments);
		}
		return args;
	}

	/*
	 * (non-Javadoc)
	 * @see com.coffee.common.web.routing.IAction#doAction(java.util.Map)
	 */
	@Override
	public Object doAction(HttpServletRequest request) throws HandleRequestException {
		Object object = injector.getInstance(this.handleClass);
		try {
			return this.method.invoke(object, budilMethodArgs(request, getPathFragments(request.getRequestURI())));
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			logger.error("方法{}执行错误", method.toGenericString());
			// TODO 此处需要单独定义异常
			throw new HandleRequestException("方法" + method.toGenericString() + "执行错误", e);
		}
	}

	/**
	 * 用于绑定参数
	 *
	 * @author coffeehc@gmail.com
	 *         create By 2015年2月23日
	 */
	private abstract class BindArgs {

		protected Class<?> paramClass;

		public BindArgs(Class<?> paramClass) {
			this.paramClass = paramClass;
		}

		abstract Object bind(HttpServletRequest request, Map<String, String> pathFragments);

	}

	class BindObject extends BindArgs {

		private Injector injector;

		public BindObject(Injector injector, Class<?> paramClass) {
			super(paramClass);
			this.injector = injector;
		}

		/*
		 * (non-Javadoc)
		 * @see com.coffee.common.web.routing.impl.ActionImpl.BindArgs#bind(javax.servlet.http.HttpServletRequest, java.util.Map)
		 */
		@Override
		public Object bind(HttpServletRequest request, Map<String, String> pathFragments) {
			if (paramClass.isAssignableFrom(HttpServletRequest.class)) {
				return request;
			}
			if (paramClass.isAssignableFrom(HttpServletResponse.class) || paramClass.isAssignableFrom(HttpSession.class)) {
				logger.warn("不允许直接注入HttpServletResponse或者HttpSession");
				return null;
			}
			if (paramClass.isAssignableFrom(Map.class)) {
				return pathFragments;
			}
			return injector.getInstance(paramClass);
		}

	}

	class BindNamedParam extends BindArgs {

		private Injector injector;

		private String name;

		private TypeConverterBinding typeConverterBinding;

		private TypeLiteral<?> type;

		private Annotation annotation;

		/**
		 * @param paramClass
		 */
		public BindNamedParam(Injector injector, Class<?> paramClass, Annotation annotation) {
			super(paramClass);
			this.injector = injector;
			this.name = getBindingAnnotationValue(annotation);
			this.annotation = annotation;
			type = TypeLiteral.get(paramClass);
			Set<TypeConverterBinding> typeConverterBindings = injector.getTypeConverterBindings();
			for (TypeConverterBinding converter : typeConverterBindings) {
				if (converter.getTypeMatcher().matches(type)) {
					typeConverterBinding = converter;
					continue;
				}
			}
			if (typeConverterBinding == null) {
				throw new LoadWebException("不能找到" + paramClass.getName() + "对应的转换方法,请再Module中添加对应的TypeConverter实现");
			}
		}

		/*
		 * (non-Javadoc)
		 * @see com.coffee.common.web.routing.impl.ActionImpl.BindArgs#bind(javax.servlet.http.HttpServletRequest, java.util.Map)
		 */
		@Override
		public Object bind(HttpServletRequest request, Map<String, String> pathFragments) {
			String obj = pathFragments.get(name);
			if (obj == null) {
				if (paramClass.isArray()) {
					return request.getParameterValues(name);
				} else {
					obj = request.getParameter(name);
				}
			}
			if (obj == null) {
				obj = injector.getBinding(Key.get(String.class, Names.named(name))).getProvider().get();
			}
			return typeConverterBinding.getTypeConverter().convert(obj, type);
		}
	}

	/**
	 * @return the defineUri
	 */
	@Override
	public String getDefineUri() {
		return this.defineUri;
	}
}
