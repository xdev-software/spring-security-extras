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

public class DefaultAuthRememberMeSecret implements AuthRememberMeSecret
{
	private final String identifier;
	private final String cryptoAlgorithm;
	private final byte[] secret;
	private final String userEmailAddress;
	
	public DefaultAuthRememberMeSecret(
		final String identifier,
		final String cryptoAlgorithm,
		final byte[] secret,
		final String userEmailAddress)
	{
		this.identifier = identifier;
		this.cryptoAlgorithm = cryptoAlgorithm;
		this.secret = secret;
		this.userEmailAddress = userEmailAddress;
	}
	
	@Override
	public String identifier()
	{
		return this.identifier;
	}
	
	@Override
	public String cryptoAlgorithm()
	{
		return this.cryptoAlgorithm;
	}
	
	@Override
	public byte[] secret()
	{
		return this.secret;
	}
	
	@Override
	public String userEmailAddress()
	{
		return this.userEmailAddress;
	}
}
