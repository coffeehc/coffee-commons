package com.coffee.common.base.utils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/*
 * 有前缀命名的线程工厂
 *
 */
public class NamedThreadFactory implements ThreadFactory {

	private final AtomicInteger	nextId	= new AtomicInteger();
	private final String		prefix;

	public NamedThreadFactory(String factoryName) {
		prefix = factoryName + '-';
	}

	@Override
	public Thread newThread(Runnable r) {
		Thread t = new Thread(r, prefix + nextId.incrementAndGet());
		try {
			if (t.isDaemon()) {
				t.setDaemon(false);
			}
			if (t.getPriority() != Thread.MAX_PRIORITY) {
				t.setPriority(Thread.MAX_PRIORITY);
			}
		} catch (Exception ignored) {
			// Doesn't matter even if failed to set.
		}
		return t;
	}
}
