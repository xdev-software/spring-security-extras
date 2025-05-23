package software.xdev.sse.oauth2.loginurl;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.ObjectPostProcessor;
import org.springframework.security.config.annotation.web.configurers.oauth2.client.OAuth2LoginConfigurer;

import software.xdev.sse.web.loginurl.LoginUrlStore;


public class OAuth2LoginUrlStoreAdapter implements ObjectPostProcessor<OAuth2LoginConfigurer<?>>
{
	private static final Logger LOG = LoggerFactory.getLogger(OAuth2LoginUrlStoreAdapter.class);
	
	private final LoginUrlStore store;
	
	public OAuth2LoginUrlStoreAdapter(final LoginUrlStore store)
	{
		this.store = store;
	}
	
	@Override
	public <O extends OAuth2LoginConfigurer<?>> O postProcess(final O object)
	{
		this.setIntoStore(object, this.store);
		return object;
	}
	
	protected void setIntoStore(final OAuth2LoginConfigurer<?> c, final LoginUrlStore store)
	{
		try
		{
			final Set<String> urls = this.extractLoginUrls(c);
			if(urls.size() == 1)
			{
				store.setLoginUrl(urls.iterator().next());
			}
		}
		catch(final NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			LOG.warn("Failed to access loginLinks", e);
		}
	}
	
	@SuppressWarnings({"java:S3011", "java:S112", "unchecked"})
	protected Set<String> extractLoginUrls(final OAuth2LoginConfigurer<?> c)
		throws NoSuchMethodException, IllegalAccessException, InvocationTargetException
	{
		final Method mGetLoginLinks = OAuth2LoginConfigurer.class.getDeclaredMethod("getLoginLinks");
		mGetLoginLinks.setAccessible(true);
		return ((Map<String, String>)mGetLoginLinks.invoke(c)).keySet();
	}
}
