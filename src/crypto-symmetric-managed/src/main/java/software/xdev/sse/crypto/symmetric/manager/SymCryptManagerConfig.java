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
package software.xdev.sse.crypto.symmetric.manager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import org.springframework.validation.annotation.Validated;

import software.xdev.sse.crypto.symmetric.manager.provider.SymCryptorProviderConfig;
import software.xdev.sse.crypto.symmetric.manager.provider.aesgcm.AESGCMSymCryptorProviderConfig;
import software.xdev.sse.crypto.symmetric.manager.provider.chacha20.ChaCha20SymCryptorProviderConfig;


@Validated
public class SymCryptManagerConfig
{
	@NotBlank
	private String standard;
	@NotNull
	private Map<String, ChaCha20SymCryptorProviderConfig> chacha20 = new HashMap<>();
	@NotNull
	private Map<String, AESGCMSymCryptorProviderConfig> aesgcm = new HashMap<>();
	
	public String getStandard()
	{
		return this.standard;
	}
	
	public void setStandard(final String standard)
	{
		this.standard = standard;
	}
	
	public Map<String, ChaCha20SymCryptorProviderConfig> getChacha20()
	{
		return this.chacha20;
	}
	
	public void setChacha20(final Map<String, ChaCha20SymCryptorProviderConfig> chacha20)
	{
		this.chacha20 = chacha20;
	}
	
	public Map<String, AESGCMSymCryptorProviderConfig> getAesgcm()
	{
		return this.aesgcm;
	}
	
	public void setAesgcm(final Map<String, AESGCMSymCryptorProviderConfig> aesgcm)
	{
		this.aesgcm = aesgcm;
	}
	
	@SuppressWarnings("java:S1452")
	public List<Map<String, ? extends SymCryptorProviderConfig>> symmetricCryptorProviderConfigs()
	{
		return List.of(
			this.getChacha20(),
			this.getAesgcm()
		);
	}
	
	@Override
	public String toString()
	{
		return "SymCryptManagerConfig ["
			+ "standard="
			+ this.standard
			+ ", chacha20="
			+ this.chacha20
			+ ", aesgcm="
			+ this.aesgcm
			+ "]";
	}
}
