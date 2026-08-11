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
package software.xdev.sse.oauth2.util;

import java.util.Collection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;


/**
 * Utility class which provides the following functionality
 * <pre>
 * &#064;Autowired
 * &#064;Lazy
 * List&#60;T&#62; beans
 * </pre>
 * (This is not working by default in Spring Boot)
 */
public class DynamicLazyBeanProvider<T>
{
	private static final Logger LOG = LoggerFactory.getLogger(DynamicLazyBeanProvider.class);
	
	protected ListableBeanFactory listableBeanFactory;
	protected Class<T> beanClazz;
	
	protected Collection<T> resolvedBeans;
	
	public DynamicLazyBeanProvider(
		final ListableBeanFactory listableBeanFactory,
		final Class<T> beanClazz)
	{
		this.listableBeanFactory = listableBeanFactory;
		this.beanClazz = beanClazz;
	}
	
	public Collection<T> get()
	{
		if(this.resolvedBeans == null)
		{
			this.init();
		}
		return this.resolvedBeans;
	}
	
	protected synchronized void init()
	{
		if(this.resolvedBeans != null)
		{
			return;
		}
		
		this.resolvedBeans =
			BeanFactoryUtils.beansOfTypeIncludingAncestors(this.listableBeanFactory, this.beanClazz).values();
		
		if(LOG.isDebugEnabled())
		{
			LOG.debug(
				"Resolved {}x {}: {}",
				this.resolvedBeans.size(),
				this.beanClazz.getSimpleName(),
				this.resolvedBeans.stream()
					.map(Object::getClass)
					.map(Class::getSimpleName)
					.toList());
		}
		
		// Free up
		this.listableBeanFactory = null;
		this.beanClazz = null;
	}
	
	public boolean hasExecutedResolve()
	{
		return this.resolvedBeans != null;
	}
}
