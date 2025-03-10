package software.xdev.sse.web.sidecar.actuator;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;


class NoErrorBasicAuthenticationEntryPointTest
{
	@Test
	void checkInstall()
	{
		Assertions.assertDoesNotThrow(() ->
			NoErrorBasicAuthenticationEntryPoint.install(new HttpBasicConfigurer<HttpSecurity>()));
	}
}
