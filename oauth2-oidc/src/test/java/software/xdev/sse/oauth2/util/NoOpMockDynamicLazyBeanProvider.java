package software.xdev.sse.oauth2.util;

import java.util.Collection;
import java.util.List;


public class NoOpMockDynamicLazyBeanProvider<T> extends DynamicLazyBeanProvider<T>
{
	public NoOpMockDynamicLazyBeanProvider(final Class<T> beanClazz)
	{
		super(null, beanClazz);
	}
	
	@Override
	public Collection<T> get()
	{
		return List.of();
	}
	
	@Override
	public boolean hasExecutedResolve()
	{
		return true;
	}
}
