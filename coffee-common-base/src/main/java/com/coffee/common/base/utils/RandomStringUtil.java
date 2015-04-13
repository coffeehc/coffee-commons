package com.coffee.common.base.utils;

import java.util.Random;

public class RandomStringUtil {

	private static final String	resourceString		= "12LMN345$%^&*()_+abcdefgyz90-=~!@#tFGHIJABk;mnohijvQRSrs678uCDETUVwxpqKOPWXYZ";

	private static final String	safeResourceString	= "1234567890abcdefghjkmnopqrstuvwxyz";

	/*
	 * 获取随机字符串
	 */
	public static String getRandomString(int length) {
		StringBuilder result = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(75);
			result.append(resourceString.substring(index, index + 1));
		}
		return result.toString();
	}

	/*
	 * 获取安全的可识别的随机字符串
	 */
	public static String getSafeRandomString(int length) {
		StringBuilder result = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < length; i++) {
			int index = random.nextInt(75);
			result.append(safeResourceString.substring(index, index + 1));
		}
		return result.toString();
	}
}
