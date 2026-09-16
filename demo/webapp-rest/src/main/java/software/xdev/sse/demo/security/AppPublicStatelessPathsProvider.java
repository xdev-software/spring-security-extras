package software.xdev.sse.demo.security;

import java.util.Set;

import org.springframework.stereotype.Component;

import software.xdev.sse.web.sidecar.public_stateless.PublicStatelessPathsProvider;


@Component
public class AppPublicStatelessPathsProvider implements PublicStatelessPathsProvider
{
	@Override
	public Set<String> paths()
	{
		return Set.of(
			"/robots.txt",
			"/favicon.ico",
			"/assets/**",
			"/lib/**"
		);
	}
}
