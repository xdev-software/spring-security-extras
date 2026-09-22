package software.xdev.sse.demo.persistence.util;

import java.util.Map;

import org.hibernate.cfg.MappingSettings;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.JavaType;
import org.hibernate.type.format.FormatMapper;


/**
 * Under normal circumstances Hibernate tries to automatically look up a formatMapper for JSON and XML.
 * <p/>
 * There are multiple problems with this:
 * <ul>
 *     <li>It tries to use Jackson for XML which is not configured for this in the app -> CRASH</li>
 *     <li>Storing XML, JSON or any other data structure inside a RELATIONAL DATABASE is idiotic</li>
 *     <li>Lookup slows down boot</li>
 * </ul>
 *
 * @since Hibernate 6.3
 */
public final class DisableHibernateFormatMapper
{
	private DisableHibernateFormatMapper()
	{
	}
	
	public static Map<String, Object> properties()
	{
		return Map.ofEntries(
			Map.entry(MappingSettings.XML_MAPPING_ENABLED, false),
			Map.entry(MappingSettings.JSON_FORMAT_MAPPER, new NoOpFormatMapper()),
			Map.entry(MappingSettings.XML_FORMAT_MAPPER, new NoOpFormatMapper())
		);
	}
	
	public static class NoOpFormatMapper implements FormatMapper
	{
		@Override
		public <T> T fromString(
			final CharSequence charSequence,
			final JavaType<T> javaType,
			final WrapperOptions wrapperOptions)
		{
			throw new UnsupportedOperationException();
		}
		
		@Override
		public <T> String toString(final T value, final JavaType<T> javaType, final WrapperOptions wrapperOptions)
		{
			throw new UnsupportedOperationException();
		}
	}
}
