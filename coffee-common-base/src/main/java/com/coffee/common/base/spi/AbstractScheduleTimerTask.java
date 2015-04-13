package com.coffee.common.base.spi;

import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

public abstract class AbstractScheduleTimerTask implements Runnable {

	private final Logger logger = LoggerFactory
			.getLogger(AbstractScheduleTimerTask.class);

	/**
	 * ScheduledThreadPoolExecutor注解
	 */
	public static final String NAMED_SCHEDULEPOOLEXECUTOR = "named_ScheduledThreadPoolExecutor";
	@Inject
	@Named(NAMED_SCHEDULEPOOLEXECUTOR)
	protected ScheduledThreadPoolExecutor scheduledThreadPoolExecutor;

	protected boolean isStart = false;

	public void startFixedDelay() {
		if (isStart) {
			logger.warn("{}已经启动,不能再次启动!", getTaskName());
			return;
		}
		scheduledThreadPoolExecutor.scheduleWithFixedDelay(this,
				initialDelay(), delay(), TimeUnit.SECONDS);
		isStart = true;
		logger.info("{}任务已启动,运行方式:FixedDelay,将在{}秒后第一次运行,平均延迟{}", new Object[] {
				getTaskName(), initialDelay(), delay() });
	}

	public void startFixedRate() {
		if (isStart) {
			logger.warn("{}已经启动,不能再次启动!", getTaskName());
			return;
		}
		scheduledThreadPoolExecutor.scheduleAtFixedRate(this, initialDelay(),
				delay(), TimeUnit.SECONDS);
		isStart = true;
		logger.info("{}任务已启动,运行方式:FixedRate,将在{}秒后第一次运行,平均间隔{}", new Object[] {
				getTaskName(), initialDelay(), delay() });
	}

	public void startOneTimes() {
		if (isStart) {
			logger.warn("{}已经启动,不能再次启动!", getTaskName());
			return;
		}
		scheduledThreadPoolExecutor.schedule(this, initialDelay(),
				TimeUnit.SECONDS);
		isStart = true;
		logger.info("{}任务已启动,运行方式:OneTimes,将在{}秒后运行", new Object[] {
				getTaskName(), initialDelay() });
	}

	public boolean isStart() {
		return isStart;
	}

	/**
	 * 初始化延时时间,单位秒
	 *
	 * @return
	 */
	protected abstract int initialDelay();

	/**
	 * 每次运行完后延迟,单位秒
	 *
	 * @return
	 */
	protected abstract int delay();

	/**
	 * 获取任务名称
	 *
	 * @return
	 */
	protected abstract String getTaskName();
}
