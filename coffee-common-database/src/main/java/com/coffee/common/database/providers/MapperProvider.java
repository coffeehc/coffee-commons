/**
 *
 */
package com.coffee.common.database.providers;

import javax.inject.Provider;

import org.apache.ibatis.session.SqlSessionManager;

import com.google.inject.Inject;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
public final class MapperProvider<T> implements Provider<T> {

	private final Class<T> mapperType;

	@Inject
	private SqlSessionManager sqlSessionManager;

	public MapperProvider(Class<T> mapperType) {
		this.mapperType = mapperType;
	}

	public void setSqlSessionManager(SqlSessionManager sqlSessionManager) {
		this.sqlSessionManager = sqlSessionManager;
	}

	@Override
	public T get() {
		return this.sqlSessionManager.getMapper(mapperType);
	}

}
