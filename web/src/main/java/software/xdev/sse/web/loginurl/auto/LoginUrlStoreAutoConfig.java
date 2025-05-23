package software.xdev.sse.web.loginurl.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.web.loginurl.DefaultLoginUrlStore;
import software.xdev.sse.web.loginurl.LoginUrlStore;


@ConditionalOnProperty(value = "sse.web.login-url-store.enabled", matchIfMissing = true)
@AutoConfiguration
public class LoginUrlStoreAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	public LoginUrlStore loginUrlStore()
	{
		return new DefaultLoginUrlStore();
	}
}
