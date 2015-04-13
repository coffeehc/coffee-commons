/**
 *
 */
package com.coffee.common.database.providers;

import static org.apache.ibatis.session.SqlSessionManager.newInstance;

import javax.inject.Provider;
import javax.inject.Singleton;

import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
@Singleton
public class SqlSessionManagerProvider implements Provider<SqlSessionManager> {

	private SqlSessionManager sqlSessionManager;

	/**
	 * @param sqlSessionFactory
	 * @since 1.0.1
	 */
	@Inject
	public void createNewSqlSessionManager(SqlSessionFactory sqlSessionFactory) {
		this.sqlSessionManager = newInstance(sqlSessionFactory);
	}

	@Override
	public SqlSessionManager get() {
		return sqlSessionManager;
	}

}
