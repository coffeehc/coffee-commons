/**
 *
 */
package com.coffee.common.web;

import javax.servlet.Filter;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年3月23日
 */
public class FilterDefine {

	private String path;

	private Class<? extends Filter> filterClass;

	public FilterDefine(String path, Class<? extends Filter> filterClass) {
		this.path = path;
		this.filterClass = filterClass;
	}

	/**
	 * @return the path
	 */
	public String getPath() {
		return this.path;
	}

	/**
	 * @return the filterClass
	 */
	public Class<? extends Filter> getFilterClass() {
		return this.filterClass;
	}
}
