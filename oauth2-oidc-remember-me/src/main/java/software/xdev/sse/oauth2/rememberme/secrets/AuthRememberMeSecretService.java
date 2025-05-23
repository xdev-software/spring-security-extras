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
package software.xdev.sse.oauth2.rememberme.secrets;

import java.time.LocalDateTime;
import java.util.Optional;


public interface AuthRememberMeSecretService
{
	default AuthRememberMeSecret createNew(
		final String identifier,
		final String cryptoAlgorithm,
		final byte[] secret,
		final String userEmailAddress)
	{
		return new DefaultAuthRememberMeSecret(identifier, cryptoAlgorithm, secret, userEmailAddress);
	}
	
	Optional<AuthRememberMeSecret> findByIdentifier(
		String identifier,
		LocalDateTime createdAfterUtc);
	
	void insert(AuthRememberMeSecret secret);
	
	void delete(String identifier);
	
	int cleanUp(LocalDateTime createdBeforeUtc, int maxPerUser);
}
