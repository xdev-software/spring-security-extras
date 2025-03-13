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
package software.xdev.sse.vaadin.csp.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;

import software.xdev.sse.csp.CSPProvider;
import software.xdev.sse.vaadin.csp.VaadinDefaultCSPProvider;
import software.xdev.sse.vaadin.csp.VaadinDevToolsCSPProvider;


@ConditionalOnProperty(value = "sse.vaadin.csp.enabled", matchIfMissing = true)
@ConditionalOnClass(CSPProvider.class)
@AutoConfiguration
public class VaadinCSPAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	@Order(1)
	public VaadinDefaultCSPProvider vaadinDefaultCSPProvider()
	{
		return new VaadinDefaultCSPProvider();
	}
	
	@ConditionalOnClass(name = "com.vaadin.base.devserver.startup.DevModeInitializer")
	@ConditionalOnMissingBean
	@Bean
	@Order(10)
	public VaadinDevToolsCSPProvider vaadinDevToolsCSPProvider()
	{
		return new VaadinDevToolsCSPProvider();
	}
}
