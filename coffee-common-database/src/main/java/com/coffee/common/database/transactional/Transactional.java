/**
 *
 */
package com.coffee.common.database.transactional;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.apache.ibatis.session.ExecutorType;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface Transactional {

	/**
	 * Mybatis的执行类型
	 *
	 * @return the constant indicating the myBatis executor type.
	 */
	ExecutorType executorType() default ExecutorType.SIMPLE;

	/**
	 * Returns the constant indicating the transaction isolation level.
	 *
	 * @return the constant indicating the transaction isolation level.
	 */
	Isolation isolation() default Isolation.DEFAULT;

	/**
	 * Flag to indicate that myBatis has to force the transaction {@code commit().}
	 *
	 * @return false by default, user defined otherwise.
	 */
	boolean force() default false;

	/**
	 * The exception re-thrown when an error occurs during the transaction.
	 *
	 * @return the exception re-thrown when an error occurs during the
	 *         transaction.
	 */
	Class<? extends Throwable> rethrowExceptionsAs() default Exception.class;

	/**
	 * A custom error message when throwing the custom exception.
	 * It supports java.util.Formatter place holders, intercepted method
	 * arguments will be used as message format arguments.
	 *
	 * @return a custom error message when throwing the custom exception.
	 * @see java.util.Formatter#format(String, Object...)
	 */
	String exceptionMessage() default "";

	/**
	 * If true, the transaction will never committed but rather rolled back, useful for testing purposes.
	 * This parameter is false by default.
	 *
	 * @return if true, the transaction will never committed but rather rolled back, useful for testing purposes.
	 */
	boolean rollbackOnly() default false;
}
