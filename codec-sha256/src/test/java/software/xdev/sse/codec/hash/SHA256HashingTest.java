package software.xdev.sse.codec.hash;

import java.util.stream.Stream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;


class SHA256HashingTest
{
	@ParameterizedTest(name = "Value of \"{0}\" should result in \"{1}\"")
	@MethodSource
	void checkResults(final String input, final String expected)
	{
		Assertions.assertEquals(expected, SHA256Hashing.hash(input));
	}
	
	static Stream<Arguments> checkResults()
	{
		return Stream.of(
			Arguments.of(null, null),
			Arguments.of("test", "9f86d081884c7d659a2feaa0c55ad015a3bf4f1b2b0b822cd15d6c15b0f00a08"),
			// There should be a zero at the end
			Arguments.of("23", "535fa30d7e25dd8a49f1536779734ec8286108d115da5045d77f3b4185d8f790")
		);
	}
}
