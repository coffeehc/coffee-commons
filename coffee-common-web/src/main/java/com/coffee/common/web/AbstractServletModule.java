package com.coffee.common.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.web.filters.DispatcherFilter;
import com.coffee.common.web.filters.EncodeFilter;
import com.coffee.common.web.filters.ExceptionFilter;
import com.coffee.common.web.filters.StaticResourceFilter;
import com.google.inject.servlet.ServletModule;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年1月31日
 */
public abstract class AbstractServletModule extends ServletModule {

	private static final Logger logger = LoggerFactory.getLogger(AbstractServletModule.class);

	@Override
	protected final void configureServlets() {
		logger.info("开始加载Servlet配置");
		filter("/*").through(EncodeFilter.class);
		filter("/*").through(ExceptionFilter.class);
		String[] staticFilterPaths = getStaticFilterPath();
		for (String staticFilterPath : staticFilterPaths) {
			if (staticFilterPath.equals("/*")) {
				continue;
			}
			filter(staticFilterPath).through(StaticResourceFilter.class);
		}
		configurePreFilters();
		FilterDefine[] filterDefines = dispatcherFilterDefines();
		if (filterDefines != null) {
			for (FilterDefine filterDefine : filterDefines) {
				filter(filterDefine.getPath()).through(filterDefine.getFilterClass());
			}
		}
		filter("/*").through(StaticResourceFilter.class);
		configurePostFilters();
		configureCustomServlets();
		logger.info("加载Servlet配置结束");
	}

	/**
	 * 定义调度FilterClass,用于扩展自定义的调度过滤器
	 *
	 * @return
	 */
	protected FilterDefine[] dispatcherFilterDefines() {
		return new FilterDefine[] { new FilterDefine("/*", DispatcherFilter.class) };
	}

	/**
	 * 返回需要拦截的静态资源后缀
	 *
	 * @return
	 */
	protected String[] getStaticFilterPath() {
		return new String[] { "*.css", "*.js", "*.html", "*.js.map", "*.json", "*.ttf", "*.woff", "*.svg", "*.eot", "*.png", "*.jpg", "*.gif" };
	}

	/**
	 * 配置Servlet
	 */
	public abstract void configureCustomServlets();

	/**
	 * 配置Filters,此方法运行在默认Filter配置之后调用
	 */
	public abstract void configurePostFilters();

	/**
	 * 配置Filters,此方法运行在默认Filter配置之前调用
	 */
	public abstract void configurePreFilters();

}
