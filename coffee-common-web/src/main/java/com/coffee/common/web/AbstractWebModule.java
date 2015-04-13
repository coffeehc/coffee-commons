package com.coffee.common.web;

import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.base.spi.AbstractScheduleTimerTask;
import com.coffee.common.base.utils.NamedThreadFactory;
import com.coffee.common.web.routing.IRoutingDispatcher;
import com.google.inject.AbstractModule;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;

public abstract class AbstractWebModule extends AbstractModule {

	private static final Logger logger = LoggerFactory.getLogger(AbstractWebModule.class);

	/**
	 * 获取需要扫描的页面定义包
	 *
	 * @return
	 */
	protected abstract List<String> getWebPackages();

	/**
	 * 用于绑定别的东西的
	 */
	protected void configureOther() {
	}

	/**
	 * 获取ServletModule的配置
	 *
	 * @return
	 */
	protected AbstractServletModule servletModule() {
		return new AbstractServletModule() {

			@Override
			public void configureCustomServlets() {

			}

			@Override
			public void configurePostFilters() {

			}

			@Override
			public void configurePreFilters() {

			}

		};
	}

	@Override
	protected void configure() {
		logger.info("开始加载Web模块");
		// this.converter(DateUtils.getDataTimeInstance());

		bind(new TypeLiteral<List<String>>() {
		}).annotatedWith(Names.named(IRoutingDispatcher.NAMEED_SCAN_PACKAGES)).toInstance(getWebPackages());
		configureOther();
		this.bind(ScheduledThreadPoolExecutor.class).annotatedWith(Names.named(AbstractScheduleTimerTask.NAMED_SCHEDULEPOOLEXECUTOR))
				.toInstance(new ScheduledThreadPoolExecutor(5, new NamedThreadFactory("server_scheduleTask")));
		install(servletModule());
		logger.info("加载Web模块结束");
	}
}
