/**
 *
 */
package com.coffee.common.base.core.interfaces;

/**
 * 生命周期管理
 *
 * @author coffeehc@gmail.com
 *         create By 2015年2月9日
 */
public interface ILifecycleAware {
	/**
	 * 初始化
	 */
	void init();

	/**
	 * 销毁,并且将初始化状态变更为没有初始化
	 */
	void destroy();

	/**
	 * 是否初始化,销毁后显示没有初始化
	 *
	 * @return 是否初始化
	 */
	boolean isInited();

}
