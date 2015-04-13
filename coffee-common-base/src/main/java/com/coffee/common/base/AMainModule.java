package com.coffee.common.base;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.base.converter.DateConverter;
import com.coffee.common.base.converter.StringConverter;
import com.coffee.common.base.exceptioin.LoadException;
import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.inject.AbstractModule;
import com.google.inject.Key;
import com.google.inject.Module;
import com.google.inject.Stage;
import com.google.inject.TypeLiteral;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;

public abstract class AMainModule extends AbstractModule {

	private static Logger logger = LoggerFactory.getLogger(AMainModule.class);

	/**
	 * 获取配置文件的目录
	 *
	 * @return 配置文件目录
	 */
	public abstract String getConfigDir();

	/**
	 * 获取需要加载的module
	 *
	 * @return 需要加载的Module清单
	 */
	protected abstract List<? extends Module> loadedModules();

	public static final String NAMED_CONFIGDIR = "named_configDir";

	public static final String NAMED_ISPRODUCTION = "named_isProduction";

	@Override
	protected void configure() {
		logger.info("初始化加载配置参数");
		binder().convertToTypes(Matchers.only(TypeLiteral.get(String.class)), new StringConverter());
		binder().convertToTypes(Matchers.only(TypeLiteral.get(Date.class)), new DateConverter());
		bind(Key.get(Boolean.class, Names.named(NAMED_ISPRODUCTION))).toInstance(binder().currentStage().equals(Stage.PRODUCTION));
		String configDir = getConfigDir();
		bind(Key.get(String.class, Names.named(NAMED_CONFIGDIR))).toInstance(configDir);
		bindPerporties(configDir);
		logger.info("初始化子模块");
		List<? extends Module> modules = loadedModules();
		if (modules != null) {
			for (Module module : modules) {
				install(module);
			}
		}
	}

	protected void bindPerporties(String configDir) {
		File configDirFile = new File(configDir);
		logger.info("扫描配置目录:{}", configDirFile.getAbsolutePath());
		if (configDirFile.exists() && configDirFile.isDirectory()) {
			File[] configFiles = configDirFile.listFiles(new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					return name.endsWith(".properties");
				}
			});
			for (File configFile : configFiles) {
				Names.bindProperties(binder(), loadProperties(configFile));
			}
		} else {
			throw new LoadException("指定的配置目录" + configDirFile.getAbsolutePath() + "不存在或不是目录");
		}
	}

	private Properties loadProperties(final File configFile) {
		logger.info("加载配置文件{}", configFile.getAbsolutePath());
		Properties appProperties = new Properties();
		try {
			appProperties.load(Files.newReader(configFile, Charsets.UTF_8));
		} catch (IOException e) {
			logger.error("加载配置文件失败!", e);
			throw new LoadException("加载配置文件失败!");
		}
		return appProperties;
	}

}
