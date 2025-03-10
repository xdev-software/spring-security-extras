package software.xdev.sse.web.sidecar;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;


/**
 * Manages paths that are not covered by the main/default app security
 */
@Component
public class OtherWebSecurityPaths
{
	@Autowired
	protected List<OtherWebSecurityPathsProvider> pathsProviders;
	
	public Set<String> all(final boolean withLogin)
	{
		return this.pathsProviders.stream()
			.filter(OtherWebSecurityPathsProvider::enabled)
			.map(p -> p.paths(withLogin))
			.flatMap(Collection::stream)
			.collect(Collectors.toSet());
	}
	
	public OrRequestMatcher requestMatcher(final boolean withLogin)
	{
		return new OrRequestMatcher(this.all(withLogin).stream()
			.map(AntPathRequestMatcher::new)
			.map(RequestMatcher.class::cast)
			.toList());
	}
}
