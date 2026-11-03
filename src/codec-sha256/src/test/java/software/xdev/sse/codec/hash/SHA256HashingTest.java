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
