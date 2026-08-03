package software.xdev.sse.demo.security;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Component;

import software.xdev.sse.demo.security.support.resources.AuthResLibs;
import software.xdev.sse.web.sidecar.public_stateless.PublicStatelessPathsProvider;


@Component
public class AppPublicStatelessPathsProvider implements PublicStatelessPathsProvider
{
	@Override
	public Set<String> paths()
	{
		return Stream.concat(
				Stream.of("/robots.txt"),
				AuthResLibs.all())
			.collect(Collectors.toSet());
	}
}
