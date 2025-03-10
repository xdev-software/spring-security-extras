package software.xdev.sse.web.sidecar.blackholing;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class BlackHolingSecurity
{
	@Bean
	public SimpleUrlHandlerMapping blackHolingMapping(
		final List<BlackHolingPathsProvider> blackHolingPathsProviders,
		final BlackHoleController blackHoleController)
	{
		return new SimpleUrlHandlerMapping(
			blackHolingPathsProviders.stream()
				.filter(BlackHolingPathsProvider::enabled)
				.map(BlackHolingPathsProvider::paths)
				.flatMap(Collection::stream)
				.distinct()
				.collect(Collectors.toMap(Function.identity(), x -> blackHoleController)),
			1);
	}
	
	@Controller
	public static class BlackHoleController extends AbstractController
	{
		public BlackHoleController()
		{
			super(false);
		}
		
		@Override
		protected ModelAndView handleRequestInternal(final HttpServletRequest req, final HttpServletResponse resp)
		{
			resp.setStatus(HttpStatus.NOT_FOUND.value());
			return null;
		}
	}
}
