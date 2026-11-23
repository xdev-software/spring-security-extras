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
package software.xdev.sse.validation.caching;

import java.lang.reflect.Field;


@SuppressWarnings("java:S3011")
final class FieldAccessor
{
	static <T> void set(
		final Class<?> clazz,
		final String fieldName,
		final Object obj,
		final T value)
	{
		try
		{
			getAccessibleField(clazz, fieldName).set(obj, value);
		}
		catch(final IllegalAccessException e)
		{
			throw new IllegalStateException("Failed to access", e);
		}
	}
	
	@SuppressWarnings("unchecked")
	static <T> T get(
		final Class<?> clazz,
		final String fieldName,
		final Object obj,
		@SuppressWarnings({"unused", "java:S1172"}) final Class<T> valueClazz)
	{
		try
		{
			return (T)getAccessibleField(clazz, fieldName).get(obj);
		}
		catch(final IllegalAccessException e)
		{
			throw new IllegalStateException("Failed to access", e);
		}
	}
	
	private static Field getAccessibleField(final Class<?> clazz, final String fieldName)
	{
		final Field field;
		try
		{
			field = clazz.getDeclaredField(fieldName);
		}
		catch(final NoSuchFieldException e)
		{
			throw new IllegalStateException("Failed to find field", e);
		}
		field.setAccessible(true);
		return field;
	}
	
	private FieldAccessor()
	{
	}
}
