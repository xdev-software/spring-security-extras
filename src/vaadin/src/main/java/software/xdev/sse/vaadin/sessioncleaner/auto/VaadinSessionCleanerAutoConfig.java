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
package software.xdev.sse.vaadin.sessioncleaner.auto;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.vaadin.flow.server.DefaultDeploymentConfiguration;

import software.xdev.sse.vaadin.sessioncleaner.ProcessedScheduleConfig;
import software.xdev.sse.vaadin.sessioncleaner.VaadinSessionCleaner;
import software.xdev.sse.vaadin.sessioncleaner.VaadinSessionCleanerServiceInitListener;
import software.xdev.sse.vaadin.sessioncleaner.config.SessionCleanerConfig;
import software.xdev.sse.vaadin.sessioncleaner.task.SessionUIsVaadinSessionCleanerTask;
import software.xdev.sse.vaadin.sessioncleaner.task.UIFreeUpVaadinSessionCleanerTask;
import software.xdev.sse.vaadin.sessioncleaner.task.VaadinSessionCleanerTask;


@ConditionalOnProperty(value = "sse.vaadin.session-cleaner.enabled", matchIfMissing = false)
@AutoConfiguration
public class VaadinSessionCleanerAutoConfig
{
	public static final double SCHEDULE_DEFAULT_INIT_DELAY_MULTIPLIER = 1.4;
	
	@ConditionalOnMissingBean
	@Bean
	@ConfigurationProperties("sse.vaadin.session-cleaner")
	public SessionCleanerConfig sessionCleanerConfig()
	{
		return new SessionCleanerConfig();
	}
	
	@ConditionalOnMissingBean
	@Bean(name = VaadinSessionCleaner.CLEANUP_SCHEDULE_CONFIG_BEAN_NAME)
	public ProcessedScheduleConfig vaadinSessionCleanerScheduleConfig(
		// @formatter:off
		@Value("${vaadin.heartbeatInterval:#{null}}")
		final Optional<Integer> optVaadinHeartbeatIntervalSec,
		@Value("${sse.vaadin.session-cleaner.schedule.initialDelaySec:#{null}}")
		final Optional<Integer> optInitialDelaySec,
		@Value("${sse.vaadin.session-cleaner.schedule.fixedDelaySec:#{null}}")
		final Optional<Integer> optFixedDelaySec
		// @formatter:on
	)
	{
		final int vaadinHeartbeatIntervalSec = optVaadinHeartbeatIntervalSec
			.orElse(DefaultDeploymentConfiguration.DEFAULT_HEARTBEAT_INTERVAL);
		
		return new ProcessedScheduleConfig(
			optInitialDelaySec
				.orElseGet(() -> (int)(vaadinHeartbeatIntervalSec * SCHEDULE_DEFAULT_INIT_DELAY_MULTIPLIER)),
			optFixedDelaySec.orElse(vaadinHeartbeatIntervalSec)
		);
	}
	
	@ConditionalOnProperty(
		value = "sse.vaadin.session-cleaner.tasks.session-uis.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public SessionUIsVaadinSessionCleanerTask sessionUIsVaadinSessionCleanerTask()
	{
		return new SessionUIsVaadinSessionCleanerTask();
	}
	
	@ConditionalOnProperty(
		value = "sse.vaadin.session-cleaner.tasks.ui-freeup.enabled",
		matchIfMissing = true)
	@ConditionalOnMissingBean
	@Bean
	public UIFreeUpVaadinSessionCleanerTask uiFreeUpVaadinSessionCleanerTask(final SessionCleanerConfig config)
	{
		return new UIFreeUpVaadinSessionCleanerTask(config.getUiFreeUp());
	}
	
	@ConditionalOnMissingBean
	@Bean
	public VaadinSessionCleaner vaadinSessionCleaner(final List<VaadinSessionCleanerTask> tasks)
	{
		return new VaadinSessionCleaner(tasks);
	}
	
	@ConditionalOnMissingBean
	@Bean
	public VaadinSessionCleanerServiceInitListener vaadinSessionCleanerServiceInitListener(
		final SessionCleanerConfig config,
		final VaadinSessionCleaner cleaner)
	{
		return config.isInstallServiceInitListener()
			? new VaadinSessionCleanerServiceInitListener(cleaner)
			: null;
	}
}
