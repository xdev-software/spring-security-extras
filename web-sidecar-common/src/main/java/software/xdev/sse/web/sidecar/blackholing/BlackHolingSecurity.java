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
package software.xdev.sse.web.sidecar.blackholing;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * <a href="https://en.wikipedia.org/wiki/Black_hole_(networking)">Blackholes</a> certain requests so that they are not
 * processed further.
 */
@EnableWebSecurity
@AutoConfiguration
public class BlackHolingSecurity
{
	private static final Logger LOG = LoggerFactory.getLogger(BlackHolingSecurity.class);
	
	@Bean
	public SimpleUrlHandlerMapping blackHolingMapping(
		final List<BlackHolingPathsProvider> blackHolingPathsProviders,
		final BlackHoleController blackHoleController)
	{
		final SimpleUrlHandlerMapping mapping = new SimpleUrlHandlerMapping(
			blackHolingPathsProviders.stream()
				.filter(BlackHolingPathsProvider::enabled)
				.map(BlackHolingPathsProvider::paths)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toMap(Function.identity(), x -> blackHoleController)),
			1);
		
		LOG.info("Pointing {}x path-patterns into black hole", mapping.getUrlMap().size());
		
		return mapping;
	}
	
	@Controller
	public static class BlackHoleController extends AbstractController
	{
		private static final Logger LOG = LoggerFactory.getLogger(BlackHoleController.class);
		
		public BlackHoleController()
		{
			super(false);
			
			LOG.debug("Instantiated black hole");
		}
		
		@Override
		protected ModelAndView handleRequestInternal(final HttpServletRequest req, final HttpServletResponse resp)
		{
			resp.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}
}
