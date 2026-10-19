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
package software.xdev.sse.oauth2.util;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;


class OAuth2UserNameAttributeKeyExtractorTest
{
	@Test
	void checkExtractWorking()
	{
		final String expectedNameAttributeKey = "abc";
		final DefaultOAuth2User oauth2User = new DefaultOAuth2User(
			List.of(),
			Map.of(expectedNameAttributeKey, "test"),
			expectedNameAttributeKey);
		
		final String actualNameAttributeKey =
			assertDoesNotThrow(() -> OAuth2UserNameAttributeKeyExtractor.extract(oauth2User));
		
		assertEquals(expectedNameAttributeKey, actualNameAttributeKey);
	}
}
