package com.coffee.common.base.utils;

import java.text.DecimalFormat;
import java.util.Map;

import com.google.common.collect.Maps;

/*
 * 单位转换工具类
 */
public class UnitUtil {

	private static Map<Integer, String>	unitNameMap	= Maps.newHashMap();

	static {
		unitNameMap.put(1, "%");
		unitNameMap.put(2, "Byte");
		unitNameMap.put(3, "KByte");
		unitNameMap.put(4, "MByte");
		unitNameMap.put(5, "Gbyte");
		unitNameMap.put(6, "TByte");
		unitNameMap.put(7, "PByte");
		unitNameMap.put(8, "bps");
		unitNameMap.put(9, "Kbps");
		unitNameMap.put(10, "Mbps");
		unitNameMap.put(11, "Gbps");
		unitNameMap.put(12, "次(个)/秒");
		unitNameMap.put(13, "秒");
		unitNameMap.put(14, "分");
		unitNameMap.put(15, "小时");
	}

	public static String getunitName(final Integer unitIndex) {
		return unitNameMap.get(unitIndex);
	}

	/**
	 * 将对应单位的值转向最小值
	 *
	 * @param unit
	 * @param value
	 * @return
	 */
	public final static double parseUnit(final Integer unit, final double value) {
		switch (unit) {
		case 3:// KByte
			return value * 1024;
		case 4:// MByte
			return value * 1048576;
		case 5:// Gbyte
			return value * 1073741824;
		case 6:// Tbyte
			return value * 1099511627776L;
		case 7:// Pbyte
			return value * 1125899906842624L;
		case 9:// Kbps
			return value * 1000;
		case 10:// Mbps
			return value * 10000;
		case 11:// Gbps
			return value * 100000;
		case 13:// 秒
			return value * 1000;
		case 14:// 分
			return value * 60000;
		case 15:// 小时
			return value * 3600000;
		default:
			return value;
		}
	}

	public final static double reverseParseUnit(final Integer resUnit,final Integer desUnit, final double value) {
		if (resUnit == desUnit) {
			return value;
		}
		double ratio = 1;
		switch (resUnit) {
		case 2:
			ratio = 1024;
			break;
		case 8:
			ratio = 1000;
			break;
		case 12:
			ratio = 60;
			if (desUnit == 13) {
				ratio = 1000;
			}
			break;
		default:
			break;
		}
		return reverseParseUnit(resUnit, desUnit - 1, value / ratio);
	}

	public final static int getMinUnit(final Integer unit) {
		if (unit >= 2 && unit <= 7) {
			return 2;
		}
		if (unit >= 8 && unit <= 11) {
			return 8;
		}
		if (unit >= 13 && unit <= 15) {
			return 13;
		}
		return unit;
	}

	private static final String[]		byteNames		= { "Byte", "KB", "MB",
			"GB", "TB", "PB"							};
	private static final String[]		bpsNames		= { "bps", "Kbps",
			"Mbps", "Gbps", "Tbps", "Pbps"				};

	public static final DecimalFormat	decimalFormat	= new DecimalFormat(
																"0.00");

	public static String bitConvert(double size, int i) {
		if (size < 1000) {
			return decimalFormat.format(size) + bpsNames[i];
		} else {
			return bitConvert(size / 1000, ++i);
		}
	}

	public static String byteConvert(double size, int i) {
		if (size < 1024) {
			return decimalFormat.format(size) + byteNames[i];
		} else {
			return byteConvert(size / 1024, ++i);
		}
	}
}
