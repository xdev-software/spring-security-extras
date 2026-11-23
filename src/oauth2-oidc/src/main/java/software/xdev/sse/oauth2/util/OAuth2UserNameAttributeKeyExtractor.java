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

import java.lang.reflect.Field;

import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;


public final class OAuth2UserNameAttributeKeyExtractor
{
	/**
	 * Tries to extract <code>nameAttributeKey</code> from {@link OAuth2User} as there is no getter.
	 *
	 * @throws RuntimeException If an error occurs
	 */
	@SuppressWarnings({"java:S112", "java:S3011"})
	public static String extract(final OAuth2User oAuth2User)
	{
		if(!(oAuth2User instanceof final DefaultOAuth2User defaultOAuth2User))
		{
			throw new IllegalArgumentException("Not a DefaultOAuth2User");
		}
		
		try
		{
			final Field fnameAttributeKey = DefaultOAuth2User.class.getDeclaredField("nameAttributeKey");
			fnameAttributeKey.setAccessible(true);
			return (String)fnameAttributeKey.get(defaultOAuth2User);
		}
		catch(final IllegalAccessException | NoSuchFieldException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	private OAuth2UserNameAttributeKeyExtractor()
	{
	}
}
