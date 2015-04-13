package com.coffee.common.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertiesUtils {

	private static Logger	logger	= LoggerFactory
											.getLogger(PropertiesUtils.class);

	/*
	 * 加载properties文件
	 */
	public static Properties LoadProperties(File file) throws Exception {
		logger.info("加载配置文件:" + file.getAbsolutePath());
		Properties properties = new Properties();
		properties.load(new FileInputStream(file));
		return properties;
	}

	/*
	 * 通过名称加载配置文件，查找目录为运行目录
	 */
	public static Properties LoadPropertiesInRunDir(String name)
			throws Exception {
		return LoadProperties(new File(PathUtils.getRunDir() + File.separator
				+ name));
	}

	public static Properties LoadPropertiesInRunDir(String name,
			Class<?> mainClass) throws Exception {
		return LoadProperties(new File(PathUtils.getRunDir(mainClass)
				+ File.separator + name));
	}

	/*
	 * 在classPath中根据名称查找配置文件
	 */
	public static Properties LoadPropertiesInClassPath(String name)
			throws Exception {
		return LoadProperties(new File(PropertiesUtils.class.getClassLoader()
				.getResource(name).getFile()));
	}

}
