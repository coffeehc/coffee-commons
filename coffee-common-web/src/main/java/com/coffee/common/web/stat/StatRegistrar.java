/**
 * Copyright (C) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.coffee.common.web.stat;

import java.lang.reflect.Member;
import java.util.List;
import java.util.concurrent.ExecutionException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.Lists;

/**
 * A {@link StatRegistrar} offers the means by which to register a stat.
 *
 * @author ffaber@gmail.com (Fred Faber)
 */
public final class StatRegistrar {
	/**
	 * The default exposer to use, which is suitable in most cases. Note that
	 * this would be best defined within {@link Stat}, but static fields
	 * declared within {@code @interface} definitions lead to javac bugs, such
	 * as is described here:
	 * https://bugs.eclipse.org/bugs/show_bug.cgi?id=324931
	 */
	Class<? extends StatExposer>											DEFAULT_EXPOSER_CLASS	= StatExposers.InferenceExposer.class;

	private final LoadingCache<Class<?>, List<MemberAnnotatedWithAtStat>>	classesToInstanceMembers;
	private final LoadingCache<Class<?>, List<MemberAnnotatedWithAtStat>>	classesToStaticMembers;

	private final Stats														stats;

	StatRegistrar(Stats stats) {
		classesToInstanceMembers = CacheBuilder.newBuilder().build(
				CacheLoader.from(new StatCollector(StatCollector.StaticMemberPolicy.EXCLUDE_STATIC_MEMBERS)));
		classesToStaticMembers = CacheBuilder.newBuilder().build(
				CacheLoader.from(new StatCollector(StatCollector.StaticMemberPolicy.INCLUDE_STATIC_MEMBERS)));
		this.stats = stats;
	}

	public void registerSingleStat(String name, String description, Object stat) {
		registerSingleStat(name, description, StatReaders.forObject(stat), DEFAULT_EXPOSER_CLASS);
	}

	public void registerSingleStat(String name, String description, StatReader statReader, Class<? extends StatExposer> statExposerClass) {
		stats.register(StatDescriptor.of(name, description, statReader, statExposerClass));
	}

	public void registerStaticStatsOn(Class<?> clazz) {
		List<MemberAnnotatedWithAtStat> annotatedMembers;
		try {
			annotatedMembers = classesToStaticMembers.get(clazz);
		} catch (ExecutionException e) {
			annotatedMembers = Lists.newArrayList();
		}
		for (MemberAnnotatedWithAtStat annotatedMember : annotatedMembers) {
			Stat stat = annotatedMember.getStat();
			stats.register(StatDescriptor.of(stat.value(), stat.description(), StatReaders.forStaticMember(annotatedMember.<Member> getMember()),
					stat.exposer()));
		}
	}

	public void registerAllStatsOn(Object target) {
		List<MemberAnnotatedWithAtStat> annotatedMembers;
		try {
			annotatedMembers = classesToInstanceMembers.get(target.getClass());
		} catch (ExecutionException e) {
			annotatedMembers = Lists.newArrayList();
		}
		for (MemberAnnotatedWithAtStat annotatedMember : annotatedMembers) {
			Stat stat = annotatedMember.getStat();
			stats.register(StatDescriptor.of(stat.value(), stat.description(), StatReaders.forMember(annotatedMember.<Member> getMember(), target),
					stat.exposer()));
		}
		registerStaticStatsOn(target.getClass());
	}
}
