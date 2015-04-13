/**
 *
 */
package com.coffee.common.database.providers;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Inject;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
@Singleton
public class SqlSessionFactoryProvider implements Provider<SqlSessionFactory> {

	/**
	 * The SqlSessionFactory reference.
	 */
	private SqlSessionFactory sqlSessionFactory;

	/**
	 * Creates a new SqlSessionFactory from the specified configuration.
	 *
	 * @param configuration
	 *            the specified configuration.
	 * @since 1.0.1
	 */
	@Inject
	public void createNewSqlSessionFactory(final Configuration configuration) {
		this.sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public SqlSessionFactory get() {
		return sqlSessionFactory;
	}

}
