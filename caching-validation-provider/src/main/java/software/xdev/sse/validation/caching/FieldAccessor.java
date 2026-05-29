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
