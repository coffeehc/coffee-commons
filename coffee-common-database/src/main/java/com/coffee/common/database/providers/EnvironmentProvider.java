/**
 *
 */
package com.coffee.common.database.providers;

import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.transaction.TransactionFactory;

import com.google.inject.Inject;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
@Singleton
public final class EnvironmentProvider implements Provider<Environment> {

	/**
	 * The environment id.
	 */
	@Inject(optional = true)
	@Named("mybatis.environment.id")
	private String id = "coffee-db";

	@Inject
	private TransactionFactory transactionFactory;

	@Inject
	private DataSource dataSource;

	public void setId(String id) {
		this.id = id;
	}

	public void setTransactionFactory(TransactionFactory transactionFactory) {
		this.transactionFactory = transactionFactory;
	}

	public void setDataSource(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Environment get() {
		return new Environment(id, transactionFactory, dataSource);
	}

}
