package com.coffee.common.base.utils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class PathUtils {
	/*
	 * 获取运行路径
	 */
	public static String getRunDir() {
		return getRunDir(PathUtils.class);
	}

	/*
	 * 通过class查找运行路径
	 */
	public static String getRunDir(Class<?> clasz) {
		String path = clasz.getProtectionDomain().getCodeSource().getLocation()
				.getFile();
		try {
			path = URLDecoder.decode(path, "UTF-8");
		} catch (UnsupportedEncodingException e) {
		}// 转换处理中文及空格
		return new File(path).getParent();
	}

}
