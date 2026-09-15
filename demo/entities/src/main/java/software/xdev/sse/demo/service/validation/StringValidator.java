package software.xdev.sse.demo.service.validation;

import java.util.Objects;

import software.xdev.sse.demo.entities.IdentifiableEntity;


public final class StringValidator
{
	public static String notTooLongDefault(final String fieldName, final String input)
	{
		return notTooLong(fieldName, IdentifiableEntity.DEFAULT_STRING_LENGTH, input);
	}
	
	public static String notTooLong(final String fieldName, final int maxLength, final String input)
	{
		Objects.requireNonNull(input, fieldName + " is null");
		if(input.length() > maxLength)
		{
			throw new IllegalArgumentException(fieldName + " too long");
		}
		return input;
	}
	
	private StringValidator()
	{
	}
}
