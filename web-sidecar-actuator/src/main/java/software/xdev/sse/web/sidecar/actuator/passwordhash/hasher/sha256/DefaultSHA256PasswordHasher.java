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
package software.xdev.sse.web.sidecar.actuator.passwordhash.hasher.sha256;

import software.xdev.sse.codec.hash.SHA256Hashing;
import software.xdev.sse.web.sidecar.actuator.passwordhash.hasher.PasswordHasher;


public class DefaultSHA256PasswordHasher implements PasswordHasher
{
	public static final String ID = "default-sha256";
	
	@Override
	public String id()
	{
		return ID;
	}
	
	@Override
	public String hash(final String input)
	{
		return SHA256Hashing.hash(input);
	}
}
