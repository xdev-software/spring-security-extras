/*
 * Copyright © 2025 XDEV Software (https://xdev.software)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package software.xdev.sse.oauth2.userinfo;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


class OidcUserServiceCompatibilityTest
{
	@Test
	void superclass()
	{
		Assertions.assertEquals(
			org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService.class.getSuperclass(),
			OidcUserService.class.getSuperclass());
	}
	
	@Test
	void interfaces()
	{
		final Set<Class<?>> actual = Arrays.stream(OidcUserService.class.getInterfaces()).collect(Collectors.toSet());
		Assertions.assertTrue(Arrays.stream(
				org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService.class.getInterfaces())
			.allMatch(actual::contains));
	}
	
	@Test
	void fields()
	{
		final List<Field> actual = Arrays.stream(OidcUserService.class.getDeclaredFields()).toList();
		Assertions.assertTrue(Arrays.stream(
				org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService.class.getDeclaredFields())
			.allMatch(e -> actual.stream()
				.anyMatch(a -> a.getName().equals(e.getName())
					&& a.getType().equals(e.getType()))));
	}
	
	@Test
	void methods()
	{
		final Predicate<Method> filterOutMethods = m -> !m.isSynthetic();
		final List<Method> actual = Arrays.stream(OidcUserService.class.getDeclaredMethods())
			.filter(filterOutMethods)
			.toList();
		Assertions.assertTrue(Arrays.stream(
				org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService.class.getDeclaredMethods())
			.filter(filterOutMethods)
			.allMatch(e -> actual.stream()
				.anyMatch(a -> a.getName().equals(e.getName())
					&& a.getReturnType().equals(e.getReturnType())
					&& equalParamTypes(a.getParameterTypes(), e.getParameterTypes()))));
	}
	
	/**
	 * @apiNote Again a method that somehow is private and therefore not accessible :(
	 * @see java.lang.reflect.Executable#equalParamTypes
	 */
	static boolean equalParamTypes(final Class<?>[] params1, final Class<?>[] params2)
	{
		if(params1.length == params2.length)
		{
			for(int i = 0; i < params1.length; i++)
			{
				if(params1[i] != params2[i])
				{
					return false;
				}
			}
			return true;
		}
		return false;
	}
}
