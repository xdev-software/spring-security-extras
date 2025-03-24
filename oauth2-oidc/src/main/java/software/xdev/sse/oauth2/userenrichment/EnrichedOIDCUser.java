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
package software.xdev.sse.oauth2.userenrichment;

import java.lang.ref.SoftReference;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jakarta.annotation.Nullable;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import software.xdev.sse.oauth2.util.OAuth2UserNameAttributeKeyExtractor;


@SuppressWarnings("java:S2160")
public class EnrichedOIDCUser<C> extends DefaultOidcUser implements EnrichedOAuth2User<C>
{
	public static final String ROLE_PREFIX = "ROLE_";
	protected final transient SoftReference<C> refCachedData;
	
	public EnrichedOIDCUser(final OidcUser oidcUser, final C cachedData)
	{
		this(oidcUser, oidcUser.getAuthorities(), cachedData);
	}
	
	public EnrichedOIDCUser(
		final OidcUser oidcUser,
		final Set<String> roles,
		final C cachedData)
	{
		this(
			oidcUser,
			Stream.concat(
					// Add computed roles
					roles.stream()
						.map(r -> ROLE_PREFIX + r)
						.map(SimpleGrantedAuthority::new),
					// Keep all other existing authorities WITHOUT previous roles
					oidcUser.getAuthorities()
						.stream()
						.filter(a -> !(a instanceof final SimpleGrantedAuthority sga
							&& sga.getAuthority().startsWith(ROLE_PREFIX))))
				.collect(Collectors.toSet()),
			cachedData
		);
	}
	
	public EnrichedOIDCUser(
		final OidcUser oidcUser,
		final Collection<? extends GrantedAuthority> authorities,
		final C cachedData)
	{
		super(
			authorities,
			oidcUser.getIdToken(),
			oidcUser.getUserInfo(),
			OAuth2UserNameAttributeKeyExtractor.extract(oidcUser));
		this.refCachedData = new SoftReference<>(cachedData);
	}
	
	@Override
	@Nullable
	public C getCachedAuthData()
	{
		return this.refCachedData != null ? this.refCachedData.get() : null;
	}
	
	@Override
	public void clearCached()
	{
		this.refCachedData.clear();
	}
}
