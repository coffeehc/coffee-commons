/**
 *
 */
package com.coffee.common.database.providers;

import java.sql.SQLException;
import java.util.List;

import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.logging.Slf4jLogFilter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.wall.WallConfig;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.coffee.common.base.exceptioin.LoadException;
import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import com.google.inject.name.Named;

/**
 * @author coffee
 */
@Singleton
public class DruidProvider implements Provider<DataSource> {

	private final DruidDataSource druidDataSource;

	public DruidProvider() {
		druidDataSource = new DruidDataSource();
		druidDataSource.setOracle(false);
		druidDataSource.setUseOracleImplicitCache(false);
		druidDataSource.setDbType("mysql");
		WallFilter wallFilter = new WallFilter();
		WallConfig config = new WallConfig(MySqlWallProvider.DEFAULT_CONFIG_DIR);
		config.setMultiStatementAllow(true);
		wallFilter.setConfig(config);
		Slf4jLogFilter slf4jLogFilter = new Slf4jLogFilter();
		List<Filter> filters = Lists.newArrayList();
		filters.add(wallFilter);
		filters.add(slf4jLogFilter);
		druidDataSource.setProxyFilters(filters);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.Provider#get()
	 */
	@Override
	public DataSource get() {
		try {
			druidDataSource.init();
		} catch (SQLException e) {
			throw new LoadException("初始化数据源失败", e);
		}
		return druidDataSource;
	}

	@Inject(optional = true)
	public void setLoginTimeout(@Named("jdbc.loginTimeout") final int loginTimeout) {
		druidDataSource.setLoginTimeout(loginTimeout);
	}

	@Inject(optional = true)
	public void setFilters(@Named("jdbc.filters") final String filters) throws Exception {
		druidDataSource.setFilters(filters);
	}

	@Inject
	public void setDriverClassName(@Named("jdbc.driverClass") final String driverClass) {
		druidDataSource.setDriverClassName(driverClass);
	}

	@Inject
	public void setUrl(@Named("jdbc.jdbcUrl") final String jdbcUrl) {
		druidDataSource.setUrl(jdbcUrl);
	}

	@Inject
	public void setPassword(@Named("jdbc.password") final String password) {
		druidDataSource.setPassword(password);
	}

	@Inject
	public void setUsername(@Named("jdbc.username") final String username) {
		druidDataSource.setUsername(username);
	}

	@Inject(optional = true)
	public void setInitialSize(@Named("jdbc.initialSize") final int initialSize) {
		druidDataSource.setInitialSize(initialSize);
	}

	@Inject(optional = true)
	public void setMinIdle(@Named("jdbc.minIdle") final int minIdle) {
		druidDataSource.setMinIdle(minIdle);
	}

	@Inject(optional = true)
	public void setMaxWait(@Named("jdbc.maxWaitMillis") final long maxWaitMillis) {
		druidDataSource.setMaxWait(maxWaitMillis);
	}

	@Inject(optional = true)
	public void setDbType(@Named("jdbc.dbType") final String dbType) {
		druidDataSource.setDbType(dbType);
	}

	@Inject(optional = true)
	public void setName(@Named("jdbc.name") final String name) {
		druidDataSource.setName(name);
	}

	@Inject(optional = true)
	public void setQueryTimeout(@Named("jdbc.queryTimeout") final int queryTimeout) {
		druidDataSource.setQueryTimeout(queryTimeout);
	}

	@Inject(optional = true)
	public void setResetStatEnable(@Named("jdbc.resetStatEnable") final boolean resetStatEnable) {
		druidDataSource.setResetStatEnable(resetStatEnable);
	}

	@Inject(optional = true)
	public void setEnable(@Named("jdbc.enable") final boolean enable) {
		druidDataSource.setEnable(enable);
	}

	@Inject(optional = true)
	public void setPoolPreparedStatements(@Named("jdbc.poolPreparedStatements") final boolean poolPreparedStatements) {
		druidDataSource.setPoolPreparedStatements(poolPreparedStatements);
	}

	@Inject(optional = true)
	public void setMaxActive(@Named("jdbc.maxActive") final int maxActive) {
		druidDataSource.setMaxActive(maxActive);
	}

	@Inject(optional = true)
	public void setUseUnfairLock(@Named("jdbc.useUnfairLock") final boolean useUnfairLock) {
		druidDataSource.setUseUnfairLock(useUnfairLock);
	}

	@Inject(optional = true)
	public void setTransactionQueryTimeout(@Named("jdbc.transactionQueryTimeout") final int transactionQueryTimeout) {
		druidDataSource.setTransactionQueryTimeout(transactionQueryTimeout);
	}

	@Inject(optional = true)
	public void setDupCloseLogEnable(@Named("jdbc.dupCloseLogEnable") final boolean dupCloseLogEnable) {
		druidDataSource.setDupCloseLogEnable(dupCloseLogEnable);
	}

	@Inject(optional = true)
	public void setTransactionThresholdMillis(@Named("jdbc.transactionThresholdMillis") final long transactionThresholdMillis) {
		druidDataSource.setTransactionThresholdMillis(transactionThresholdMillis);
	}

	@Inject(optional = true)
	public void setBreakAfterAcquireFailure(@Named("jdbc.breakAfterAcquireFailure") final boolean breakAfterAcquireFailure) {
		druidDataSource.setBreakAfterAcquireFailure(breakAfterAcquireFailure);
	}

	@Inject(optional = true)
	public void setConnectionErrorRetryAttempts(@Named("jdbc.connectionErrorRetryAttempts") final int connectionErrorRetryAttempts) {
		druidDataSource.setConnectionErrorRetryAttempts(connectionErrorRetryAttempts);
	}

	@Inject(optional = true)
	public void setMaxPoolPreparedStatementPerConnectionSize(
			@Named("jdbc.maxPoolPreparedStatementPerConnectionSize") final int maxPoolPreparedStatementPerConnectionSize) {
		druidDataSource.setMaxPoolPreparedStatementPerConnectionSize(maxPoolPreparedStatementPerConnectionSize);
	}

	@Inject(optional = true)
	public void setSharePreparedStatements(@Named("jdbc.sharePreparedStatements") final boolean sharePreparedStatements) {
		druidDataSource.setSharePreparedStatements(sharePreparedStatements);
	}

	@Inject(optional = true)
	public void setValidConnectionCheckerClassName(@Named("jdbc.validConnectionCheckerClass") final String validConnectionCheckerClass)
			throws Exception {
		druidDataSource.setValidConnectionCheckerClassName(validConnectionCheckerClass);
	}

	@Inject(optional = true)
	public void setTimeBetweenConnectErrorMillis(@Named("jdbc.timeBetweenConnectErrorMillis") final long timeBetweenConnectErrorMillis) {
		druidDataSource
				.setTimeBetweenConnectErrorMillis(timeBetweenConnectErrorMillis);
	}

	@Inject(optional = true)
	public void setMaxOpenPreparedStatements(@Named("jdbc.maxOpenPreparedStatements") final int maxOpenPreparedStatements) {
		druidDataSource.setMaxOpenPreparedStatements(maxOpenPreparedStatements);
	}

	@Inject(optional = true)
	public void setLogAbandoned(@Named("jdbc.logAbandoned") final boolean logAbandoned) {
		druidDataSource.setLogAbandoned(logAbandoned);
	}

	@Inject(optional = true)
	public void setRemoveAbandonedTimeout(@Named("jdbc.removeAbandonedTimeout") final int removeAbandonedTimeout) {
		druidDataSource.setRemoveAbandonedTimeout(removeAbandonedTimeout);
	}

	@Inject(optional = true)
	public void setRemoveAbandoned(@Named("jdbc.removeAbandoned") final boolean removeAbandoned) {
		druidDataSource.setRemoveAbandoned(removeAbandoned);
	}

	@Inject(optional = true)
	public void setMinEvictableIdleTimeMillis(@Named("jdbc.minEvictableIdleTimeMillis") final long minEvictableIdleTimeMillis) {
		druidDataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);
	}

	@Inject(optional = true)
	public void setTimeBetweenEvictionRunsMillis(@Named("jdbc.timeBetweenEvictionRunsMillis") final long timeBetweenEvictionRunsMillis) {
		druidDataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
	}

	@Inject(optional = true)
	public void setMaxWaitThreadCount(@Named("jdbc.maxWaithThreadCount") final int maxWaithThreadCount) {
		druidDataSource.setMaxWaitThreadCount(maxWaithThreadCount);
	}

	@Inject(optional = true)
	public void setValidationQuery(@Named("jdbc.validationQuery") final String validationQuery) {
		druidDataSource.setValidationQuery(validationQuery);
	}

	@Inject(optional = true)
	public void setDefaultCatalog(@Named("jdbc.defaultCatalog") final String defaultCatalog) {
		druidDataSource.setDefaultCatalog(defaultCatalog);
	}

	@Inject(optional = true)
	public void setDefaultTransactionIsolation(@Named("jdbc.defaultTransactionIsolation") final Integer defaultTransactionIsolation) {
		druidDataSource.setDefaultTransactionIsolation(defaultTransactionIsolation);
	}

	@Inject(optional = true)
	public void setDefaultReadOnly(@Named("jdbc.defaultReadOnly") final Boolean defaultReadOnly) {
		druidDataSource.setDefaultReadOnly(defaultReadOnly);
	}

	@Inject(optional = true)
	public void setDefaultAutoCommit(@Named("jdbc.defaultAutoCommit") final boolean defaultAutoCommit) {
		druidDataSource.setDefaultAutoCommit(defaultAutoCommit);
	}

	@Inject(optional = true)
	public void setTestWhileIdle(@Named("jdbc.testWhileIdle") final boolean testWhileIdle) {
		druidDataSource.setTestWhileIdle(testWhileIdle);
	}

	@Inject(optional = true)
	public void setTestOnReturn(@Named("jdbc.testOnReturn") final boolean testOnReturn) {
		druidDataSource.setTestOnReturn(testOnReturn);
	}

	@Inject(optional = true)
	public void setTestOnBorrow(@Named("jdbc.testOnBorrow") final boolean testOnBorrow) {
		druidDataSource.setTestOnBorrow(testOnBorrow);
	}

	@Inject(optional = true)
	public void setAccessToUnderlyingConnectionAllowed(
			@Named("jdbc.accessToUnderlyingConnectionAllowed") final boolean accessToUnderlyingConnectionAllowed) {
		druidDataSource.setAccessToUnderlyingConnectionAllowed(accessToUnderlyingConnectionAllowed);
	}

	@Inject(optional = true)
	public void setValidationQueryTimeout(@Named("jdbc.validationQueryTimeout") final int validationQueryTimeout) {
		druidDataSource.setValidationQueryTimeout(validationQueryTimeout);
	}
}
