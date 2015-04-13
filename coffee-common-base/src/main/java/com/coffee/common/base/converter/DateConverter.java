/**
 *
 */
package com.coffee.common.base.converter;

import java.util.Date;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.primitives.Longs;
import com.google.inject.Inject;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Named;
import com.google.inject.spi.TypeConverter;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月23日
 */
public class DateConverter implements TypeConverter {

	private static final Logger logger = LoggerFactory.getLogger(DateConverter.class);
	private String dateFormat = "yyyy-MM-dd hh:mm:ss";

	private volatile DateTimeFormatter dateTimeFormater = DateTimeFormat.forPattern(dateFormat);

	@Inject(optional = true)
	private void setDateFormat(@Named("baseconfig.dateConverter.format") String format) {
		dateFormat = format;
		dateTimeFormater = DateTimeFormat.forPattern(dateFormat);
	}

	/*
	 * (non-Javadoc)
	 * @see com.google.inject.spi.TypeConverter#convert(java.lang.String, com.google.inject.TypeLiteral)
	 */
	@Override
	public Object convert(String value, TypeLiteral<?> toType) {
		try {
			return new Date(Longs.tryParse(value));
		} catch (Exception e1) {
			try {
				return dateTimeFormater.parseDateTime(value).toDate();
			} catch (Exception e) {
				logger.error("将字符串{}解析为时间类型失败", value, e);
				return null;
			}
		}
	}
}
