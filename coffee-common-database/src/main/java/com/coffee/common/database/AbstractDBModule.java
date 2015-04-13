package com.coffee.common.database;

import static com.google.inject.matcher.Matchers.annotatedWith;
import static com.google.inject.matcher.Matchers.any;
import static com.google.inject.matcher.Matchers.not;
import static com.google.inject.name.Names.named;
import static com.google.inject.util.Providers.guicify;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.sql.DataSource;

import org.apache.ibatis.io.ResolverUtil;
import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.reflection.factory.DefaultObjectFactory;
import org.apache.ibatis.reflection.factory.ObjectFactory;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionManager;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.apache.ibatis.type.TypeHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.coffee.common.database.interceptors.TransactionalMethodInterceptor;
import com.coffee.common.database.names.Mappers;
import com.coffee.common.database.names.MappingTypeHandlers;
import com.coffee.common.database.names.SqlMaps;
import com.coffee.common.database.names.TypeAliases;
import com.coffee.common.database.providers.ConfigurationProvider;
import com.coffee.common.database.providers.DruidProvider;
import com.coffee.common.database.providers.EnvironmentProvider;
import com.coffee.common.database.providers.MapperProvider;
import com.coffee.common.database.providers.SqlSessionFactoryProvider;
import com.coffee.common.database.providers.SqlSessionManagerProvider;
import com.coffee.common.database.transactional.Transactional;
import com.google.inject.AbstractModule;
import com.google.inject.Scopes;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.MapBinder;
import com.google.inject.multibindings.Multibinder;

public abstract class AbstractDBModule extends AbstractModule {

	private final Logger logger = LoggerFactory.getLogger(AbstractDBModule.class);

	private MapBinder<String, Class<?>> aliases;

	private MapBinder<Class<?>, TypeHandler<?>> handlers;

	private Multibinder<TypeHandler<?>> mappingTypeHandlers;

	private Multibinder<Interceptor> interceptors;

	private Multibinder<Class<?>> mappers;

	private Multibinder<Resource> sqlmaps;

	@Override
	protected final void configure() {
		logger.info("开始加载DB模块");
		aliases = MapBinder.newMapBinder(binder(), new TypeLiteral<String>() {
		}, new TypeLiteral<Class<?>>() {
		}, TypeAliases.class);
		handlers = MapBinder.newMapBinder(binder(), new TypeLiteral<Class<?>>() {
		}, new TypeLiteral<TypeHandler<?>>() {
		});
		interceptors = Multibinder.newSetBinder(binder(), Interceptor.class);
		mappingTypeHandlers = Multibinder.newSetBinder(binder(), new TypeLiteral<TypeHandler<?>>() {
		}, MappingTypeHandlers.class);
		mappers = Multibinder.newSetBinder(binder(), new TypeLiteral<Class<?>>() {
		}, Mappers.class);
		sqlmaps = Multibinder.newSetBinder(binder(), Resource.class, SqlMaps.class);
		bind(SqlSessionManager.class).toProvider(SqlSessionManagerProvider.class).in(Scopes.SINGLETON);
		bind(SqlSession.class).to(SqlSessionManager.class).in(Scopes.SINGLETON);
		bind(DataSource.class).toProvider(DruidProvider.class).in(Scopes.SINGLETON);
		bind(TransactionFactory.class).to(JdbcTransactionFactory.class).in(Scopes.SINGLETON);
		TransactionalMethodInterceptor interceptor = new TransactionalMethodInterceptor();
		requestInjection(interceptor);
		bindInterceptor(any(), annotatedWith(Transactional.class), interceptor);
		bindInterceptor(annotatedWith(Transactional.class), not(annotatedWith(Transactional.class)), interceptor);
		List<String> packages = getMapperClassPackage();
		for (String packageName : packages) {
			Set<Class<?>> mapperClasses = getClasses(packageName);
			for (Class<?> mapperClass : mapperClasses) {
				mappers.addBinding().toInstance(mapperClass);
				bindMapper(mapperClass);
			}
		}
		List<String> sqlMapPaths = getSqlMapPaths();
		for (String sqlMapPath : sqlMapPaths) {
			sqlMapPath = sqlMapPath.replace(".", "/");
			Resource resource = Resource.newClassPathResource(sqlMapPath);
			Collection<Resource> resources = resource.getAllResources();
			for (Resource r : resources) {
				String name = r.getName();
				if (name.endsWith(".xml")) {
					sqlmaps.addBinding().toInstance(r);
				}
			}
		}
		bindConstant().annotatedWith(named("mybatis.configuration.lazyLoadingEnabled")).to(true);
		bind(Environment.class).toProvider(EnvironmentProvider.class).in(Scopes.SINGLETON);
		bind(Configuration.class).toProvider(ConfigurationProvider.class).in(Scopes.SINGLETON);
		bind(SqlSessionFactory.class).toProvider(SqlSessionFactoryProvider.class);
		bind(ObjectFactory.class).to(DefaultObjectFactory.class).in(Scopes.SINGLETON);
		logger.info("加载DB模块结束");
	}

	private Set<Class<?>> getClasses(String packageName) {
		return new ResolverUtil<Object>().find(new ResolverUtil.IsA(Object.class), packageName).getClasses();
	}

	private final <T> void bindMapper(Class<T> mapperType) {
		bind(mapperType).toProvider(guicify(new MapperProvider<T>(mapperType))).in(Scopes.SINGLETON);
	}

	protected abstract List<String> getMapperClassPackage();

	protected abstract List<String> getSqlMapPaths();
}
