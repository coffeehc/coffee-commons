/**
 *
 */
package com.coffee.common.database.providers;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

import javax.inject.Named;
import javax.inject.Provider;
import javax.sql.DataSource;

import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.executor.ErrorContext;
import org.apache.ibatis.mapping.DatabaseIdProvider;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.AutoMappingBehavior;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.type.TypeHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.database.names.Mappers;
import com.coffee.common.database.names.SqlMaps;
import com.google.inject.Inject;
import com.google.inject.ProvisionException;
import com.google.inject.Singleton;

/**
 * @author coffeehc@gmail.com
 *         create By 2015年2月21日
 */
@Singleton
public class ConfigurationProvider implements Provider<Configuration> {

	private static Logger logger = LoggerFactory.getLogger(ConfigurationProvider.class);
	/**
	 * The myBatis Configuration reference.
	 */
	private final Environment environment;

	@Inject(optional = true)
	@Named("mybatis.configuration.lazyLoadingEnabled")
	private boolean lazyLoadingEnabled = false;

	@Inject(optional = true)
	@Named("mybatis.configuration.aggressiveLazyLoading")
	private boolean aggressiveLazyLoading = true;

	@Inject(optional = true)
	@Named("mybatis.configuration.multipleResultSetsEnabled")
	private boolean multipleResultSetsEnabled = true;

	@Inject(optional = true)
	@Named("mybatis.configuration.useGeneratedKeys")
	private boolean useGeneratedKeys = false;

	@Inject(optional = true)
	@Named("mybatis.configuration.useColumnLabel")
	private boolean useColumnLabel = true;

	@Inject(optional = true)
	@Named("mybatis.configuration.cacheEnabled")
	private boolean cacheEnabled = true;

	@Inject(optional = true)
	@Named("mybatis.configuration.defaultExecutorType")
	private ExecutorType defaultExecutorType = ExecutorType.SIMPLE;

	@Inject(optional = true)
	@Named("mybatis.configuration.autoMappingBehavior")
	private AutoMappingBehavior autoMappingBehavior = AutoMappingBehavior.PARTIAL;

	@Inject(optional = true)
	@Named("mybatis.configuration.failFast")
	private boolean failFast = false;

	@Inject
	private ObjectFactory objectFactory;

	// @Inject(optional = true)
	// @TypeAliases
	// private Map<String, Class<?>> typeAliases;

	@Inject(optional = true)
	private Map<Class<?>, TypeHandler<?>> typeHandlers = Collections.emptyMap();

	// @Inject(optional = true)
	// @MappingTypeHandlers
	// private Set<TypeHandler<?>> mappingTypeHandlers = Collections.emptySet();

	@Inject(optional = true)
	@Mappers
	private Set<Class<?>> mapperClasses = Collections.emptySet();
	@Inject(optional = true)
	@SqlMaps
	private Set<Resource> sqlMapResource = Collections.emptySet();

	@Inject(optional = true)
	private Set<Interceptor> plugins = Collections.emptySet();

	@Inject(optional = true)
	@Named("mybatis.configuration.mapUnderscoreToCamelCase")
	private boolean mapUnderscoreToCamelCase = false;

	@Inject(optional = true)
	private DatabaseIdProvider databaseIdProvider;

	@Inject
	private DataSource dataSource;

	/**
	 * @since 1.0.1
	 */
	@Inject
	public ConfigurationProvider(final Environment environment) {
		this.environment = environment;
	}

	/**
	 * @since 1.0.1
	 */
	public void setLazyLoadingEnabled(boolean lazyLoadingEnabled) {
		this.lazyLoadingEnabled = lazyLoadingEnabled;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setAggressiveLazyLoading(boolean aggressiveLazyLoading) {
		this.aggressiveLazyLoading = aggressiveLazyLoading;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setMultipleResultSetsEnabled(boolean multipleResultSetsEnabled) {
		this.multipleResultSetsEnabled = multipleResultSetsEnabled;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setUseGeneratedKeys(boolean useGeneratedKeys) {
		this.useGeneratedKeys = useGeneratedKeys;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setUseColumnLabel(boolean useColumnLabel) {
		this.useColumnLabel = useColumnLabel;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setCacheEnabled(boolean cacheEnabled) {
		this.cacheEnabled = cacheEnabled;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setDefaultExecutorType(ExecutorType defaultExecutorType) {
		this.defaultExecutorType = defaultExecutorType;
	}

	/**
	 * @since 1.0.1
	 */
	protected void setAutoMappingBehavior(AutoMappingBehavior autoMappingBehavior) {
		this.autoMappingBehavior = autoMappingBehavior;
	}

	/**
	 * Flag to check all statements are completed.
	 *
	 * @param failFast
	 *            flag to check all statements are completed
	 * @since 1.0.1
	 */
	public void setFailFast(boolean failFast) {
		this.failFast = failFast;
	}

	// /**
	// * Adds the user defined type aliases to the myBatis Configuration.
	// *
	// * @param typeAliases
	// * the user defined type aliases.
	// */
	// public void setTypeAliases(Map<String, Class<?>> typeAliases) {
	// this.typeAliases = typeAliases;
	// }

	/**
	 * Adds the user defined type handlers to the myBatis Configuration.
	 *
	 * @param typeHandlers
	 *            the user defined type handlers.
	 */
	@Inject(optional = true)
	public void registerTypeHandlers(final Map<Class<?>, TypeHandler<?>> typeHandlers) {
		this.typeHandlers = typeHandlers;
	}

	/**
	 * Adds the user defined Mapper classes to the myBatis Configuration.
	 *
	 * @param mapperClasses
	 *            the user defined Mapper classes.
	 */
	public void setMapperClasses(Set<Class<?>> mapperClasses) {
		this.mapperClasses = mapperClasses;
	}

	/**
	 * Adds the user defined ObjectFactory to the myBatis Configuration.
	 *
	 * @param objectFactory
	 */
	public void setObjectFactory(final ObjectFactory objectFactory) {
		this.objectFactory = objectFactory;
	}

	/**
	 * Registers the user defined plugins interceptors to the
	 * myBatis Configuration.
	 *
	 * @param plugins
	 *            the user defined plugins interceptors.
	 */
	public void setPlugins(Set<Interceptor> plugins) {
		this.plugins = plugins;
	}

	/**
	 * @since 3.3
	 * @param mapUnderscoreToCamelCase
	 */
	public void mapUnderscoreToCamelCase(boolean mapUnderscoreToCamelCase)
	{
		this.mapUnderscoreToCamelCase = mapUnderscoreToCamelCase;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Configuration get() {
		final Configuration configuration = new Configuration(environment);
		configuration.setLazyLoadingEnabled(lazyLoadingEnabled);
		configuration.setAggressiveLazyLoading(aggressiveLazyLoading);
		configuration.setMultipleResultSetsEnabled(multipleResultSetsEnabled);
		configuration.setUseGeneratedKeys(useGeneratedKeys);
		configuration.setUseColumnLabel(useColumnLabel);
		configuration.setCacheEnabled(cacheEnabled);
		configuration.setDefaultExecutorType(defaultExecutorType);
		configuration.setAutoMappingBehavior(autoMappingBehavior);
		configuration.setObjectFactory(objectFactory);
		configuration.setMapUnderscoreToCamelCase(mapUnderscoreToCamelCase);
		try {
			if (databaseIdProvider != null) {
				configuration.setDatabaseId(databaseIdProvider.getDatabaseId(dataSource));
			}
			// for (Map.Entry<String, Class<?>> alias : typeAliases.entrySet()) {
			// configuration.getTypeAliasRegistry().registerAlias(alias.getKey(), alias.getValue());
			// }
			for (Map.Entry<Class<?>, TypeHandler<?>> typeHandler : typeHandlers.entrySet()) {
				registerTypeHandler(configuration, typeHandler.getKey(), typeHandler.getValue());
			}
			// for (TypeHandler<?> typeHandler : mappingTypeHandlers) {
			// configuration.getTypeHandlerRegistry().register(typeHandler);
			// }

			for (Class<?> mapperClass : mapperClasses) {
				if (!configuration.hasMapper(mapperClass)) {
					logger.debug("加载MapperClass:{}" + mapperClass);
					configuration.addMapper(mapperClass);
				}
			}
			for (Interceptor interceptor : plugins) {
				configuration.addInterceptor(interceptor);
			}
			for (Resource resource : sqlMapResource) {
				logger.debug("加载SqlMap:{}" + resource.getName());
				XMLMapperBuilder xmlMapperBuilder = new XMLMapperBuilder(resource.getInputStream(),
						configuration, resource.getName(), configuration.getSqlFragments());
				xmlMapperBuilder.parse();
			}
			if (failFast) {
				configuration.getMappedStatementNames();
			}
		} catch (Throwable cause) {
			throw new ProvisionException("初始化org.apache.ibatis.session.Configuration异常", cause);
		} finally {
			ErrorContext.instance().reset();
		}

		return configuration;
	}

	@SuppressWarnings("unchecked")
	private <T> void registerTypeHandler(Configuration configuration, Class<?> type, TypeHandler<?> handler) {
		configuration.getTypeHandlerRegistry().register((Class<T>) type, (TypeHandler<T>) handler);
	}

}
