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
package software.xdev.sse.oauth2.rememberme;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import software.xdev.sse.oauth2.rememberme.config.OAuth2CookieRememberMeServicesCleanupScheduleConfig;
import software.xdev.sse.oauth2.rememberme.config.OAuth2CookieRememberMeServicesConfig;
import software.xdev.sse.oauth2.rememberme.secrets.AuthRememberMeSecretService;


public class OAuth2CookieRememberMeServicesCleaner
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2CookieRememberMeServicesCleaner.class);
	
	protected final OAuth2CookieRememberMeServicesConfig config;
	protected final AuthRememberMeSecretService authRememberMeSecretService;
	
	public OAuth2CookieRememberMeServicesCleaner(
		final OAuth2CookieRememberMeServicesConfig config,
		final OAuth2CookieRememberMeServicesCleanupScheduleConfig cleanupScheduleConfig,
		final AuthRememberMeSecretService authRememberMeSecretService)
	{
		this.config = config;
		this.authRememberMeSecretService = authRememberMeSecretService;
		
		LOG.info("Instantiated with schedule: {}", cleanupScheduleConfig);
	}
	
	// region Cleanup
	public static final String CLEANUP_CONFIG_BEAN_NAME = "oAuth2CookieRememberMeServicesCleanupConfig";
	
	@Scheduled(
		initialDelayString = "#{@" + CLEANUP_CONFIG_BEAN_NAME + ".initialDelaySec}",
		fixedRateString = "#{@" + CLEANUP_CONFIG_BEAN_NAME + ".fixedRateSec}",
		timeUnit = TimeUnit.SECONDS)
	public void cleanUpPersisted()
	{
		try
		{
			final StopWatch sw = StopWatch.createStarted();
			
			final int deleted = this.authRememberMeSecretService.cleanUp(
				LocalDateTime.now(ZoneOffset.UTC).minus(this.config.getExpiration()),
				this.config.getMaxPerUser());
			
			sw.stop();
			LOG.info(
				"Finished cleaning AuthRememberMeSecrets, took {}ms to delete {}x",
				sw.getTime(),
				deleted);
		}
		catch(final Exception e)
		{
			LOG.error("Failed to clean deleted AuthRememberMeSecrets", e);
		}
	}
	
	// endregion
}
