/**
 *
 */
package com.coffee.common.database.interceptors;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.apache.ibatis.session.SqlSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.database.transactional.Transactional;
import com.google.inject.Inject;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
public class TransactionalMethodInterceptor implements MethodInterceptor {
	private static final Class<?>[] CAUSE_TYPES = new Class[] { Throwable.class };

	private static final Class<?>[] MESSAGE_CAUSE_TYPES = new Class[] { String.class, Throwable.class };

	private Logger logger = LoggerFactory.getLogger(TransactionalMethodInterceptor.class);

	/**
	 * The {@code SqlSessionManager} reference.
	 */
	@Inject
	private SqlSessionManager sqlSessionManager;

	/**
	 * Sets the SqlSessionManager instance.
	 *
	 * @param sqlSessionManager
	 *            the SqlSessionManager instance.
	 */
	public void setSqlSessionManager(SqlSessionManager sqlSessionManager) {
		this.sqlSessionManager = sqlSessionManager;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object invoke(MethodInvocation invocation) throws Throwable {
		Method interceptedMethod = invocation.getMethod();
		Transactional transactional = interceptedMethod.getAnnotation(Transactional.class);

		// The annotation may be present at the class level instead
		if (transactional == null) {
			transactional = interceptedMethod.getDeclaringClass().getAnnotation(Transactional.class);
		}

		boolean isSessionInherited = this.sqlSessionManager.isManagedSessionStarted();

		if (isSessionInherited) {
			logger.debug("[Intercepted method: {}] - SqlSession already set for thread: {}",
					interceptedMethod.toGenericString(),
					Thread.currentThread().getId());
		} else {
			logger.debug("[Intercepted method: {}] - SqlSession not set for thread: {}, creating a new one",
					interceptedMethod.toGenericString(),
					Thread.currentThread().getId());

			sqlSessionManager.startManagedSession(transactional.executorType(), transactional.isolation().getTransactionIsolationLevel());
		}

		Object object = null;
		boolean needsRollback = transactional.rollbackOnly();
		try {
			object = invocation.proceed();
		} catch (Throwable t) {
			needsRollback = true;
			throw convertThrowableIfNeeded(invocation, transactional, t);
		} finally {
			if (!isSessionInherited) {
				try {
					if (needsRollback) {
						logger.debug("[Intercepted method: {}] - SqlSession not set for thread: {}, rolling back",
								interceptedMethod.toGenericString(),
								Thread.currentThread().getId());

						sqlSessionManager.rollback(true);
					} else {
						logger.debug("[Intercepted method: {}] - SqlSession not set for thread: {}, committing", interceptedMethod.toGenericString(),
								Thread.currentThread().getId());

						sqlSessionManager.commit(transactional.force());
					}
				} finally {
					logger.debug("[Intercepted method: {}] - SqlSession of thread: {} terminated its life-cycle, closing it",
							interceptedMethod.toGenericString(),
							Thread.currentThread().getId());

					sqlSessionManager.close();
				}
			} else {
				logger.debug("[Intercepted method: {}] - SqlSession of thread: {} is inherited, skipped close operation",
						interceptedMethod.toGenericString(),
						Thread.currentThread().getId());
			}
		}

		return object;
	}

	private Throwable convertThrowableIfNeeded(MethodInvocation invocation, Transactional transactional, Throwable t) {
		Method interceptedMethod = invocation.getMethod();

		// check the caught exception is declared in the invoked method
		for (Class<?> exceptionClass : interceptedMethod.getExceptionTypes()) {
			if (exceptionClass.isAssignableFrom(t.getClass())) {
				return t;
			}
		}

		// check the caught exception is of same rethrow type
		if (transactional.rethrowExceptionsAs().isAssignableFrom(t.getClass())) {
			return t;
		}

		// rethrow the exception as new exception
		String errorMessage = null;
		Object[] initargs;
		Class<?>[] initargsType;

		if (transactional.exceptionMessage().length() != 0) {
			errorMessage = String.format(transactional.exceptionMessage(), invocation.getArguments());
			initargs = new Object[] { errorMessage, t };
			initargsType = MESSAGE_CAUSE_TYPES;
		} else {
			initargs = new Object[] { t };
			initargsType = CAUSE_TYPES;
		}

		Constructor<? extends Throwable> exceptionConstructor = getMatchingConstructor(transactional.rethrowExceptionsAs(), initargsType);
		Throwable rethrowEx = null;
		if (exceptionConstructor != null) {
			try {
				rethrowEx = exceptionConstructor.newInstance(initargs);
			} catch (Exception e) {
				logger.error("Impossible to re-throw '{}', it needs the constructor with {} argument(s).", transactional.rethrowExceptionsAs()
						.getName(),
						Arrays.toString(initargsType), e);
				rethrowEx = new RuntimeException(errorMessage, e);
			}
		} else {
			logger.error("Impossible to re-throw '{}', it needs the constructor with {} or {} argument(s).", transactional.rethrowExceptionsAs()
					.getName(),
					Arrays.toString(CAUSE_TYPES),
					Arrays.toString(MESSAGE_CAUSE_TYPES));
			rethrowEx = new RuntimeException(errorMessage);
		}

		return rethrowEx;
	}

	@SuppressWarnings("unchecked")
	private static <E extends Throwable> Constructor<E> getMatchingConstructor(Class<E> type,
			Class<?>[] argumentsType) {
		Class<? super E> currentType = type;
		while (Object.class != currentType) {
			for (Constructor<?> constructor : currentType.getConstructors()) {
				if (Arrays.equals(argumentsType, constructor.getParameterTypes())) {
					return (Constructor<E>) constructor;
				}
			}
			currentType = currentType.getSuperclass();
		}
		return null;
	}
}
