package software.xdev.sse.oauth2.loginurl.auto;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;

import software.xdev.sse.oauth2.loginurl.OAuth2LoginUrlStoreAdapter;
import software.xdev.sse.web.loginurl.LoginUrlStore;
import software.xdev.sse.web.loginurl.auto.LoginUrlStoreAutoConfig;


@ConditionalOnProperty(value = "sse.oauth2.oauth2-login-url-store-adapter.enabled", matchIfMissing = true)
@AutoConfiguration(after = LoginUrlStoreAutoConfig.class)
public class OAuth2LoginUrlStoreAdapterAutoConfig
{
	@ConditionalOnMissingBean
	@Bean
	@ConditionalOnBean(LoginUrlStore.class)
	public OAuth2LoginUrlStoreAdapter oAuth2LoginUrlStoreAdapter(final LoginUrlStore loginUrlStore)
	{
		return new OAuth2LoginUrlStoreAdapter(loginUrlStore);
	}
}
