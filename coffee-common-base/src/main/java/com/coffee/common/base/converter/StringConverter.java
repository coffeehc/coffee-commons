/**
 *
 */
package com.coffee.common.base.converter;

import com.google.inject.TypeLiteral;
import com.google.inject.spi.TypeConverter;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月23日
 */
public class StringConverter implements TypeConverter {

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.spi.TypeConverter#convert(java.lang.String, com.google.inject.TypeLiteral)
	 */
	@Override
	public Object convert(String value, TypeLiteral<?> toType) {
		return value;
	}

}
