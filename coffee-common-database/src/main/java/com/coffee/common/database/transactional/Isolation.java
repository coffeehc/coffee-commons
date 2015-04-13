/**
 *
 */
package com.coffee.common.database.transactional;

import org.apache.ibatis.session.TransactionIsolationLevel;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
public enum Isolation {

	DEFAULT(null),
	NONE(TransactionIsolationLevel.NONE),
	READ_COMMITTED(TransactionIsolationLevel.READ_COMMITTED),
	READ_UNCOMMITTED(TransactionIsolationLevel.READ_UNCOMMITTED),
	REPEATABLE_READ(TransactionIsolationLevel.REPEATABLE_READ),
	SERIALIZABLE(TransactionIsolationLevel.SERIALIZABLE);

	private final TransactionIsolationLevel transactionIsolationLevel;

	private Isolation(TransactionIsolationLevel transactionIsolationLevel) {
		this.transactionIsolationLevel = transactionIsolationLevel;
	}

	public TransactionIsolationLevel getTransactionIsolationLevel() {
		return transactionIsolationLevel;
	}

}
