package software.xdev.sse.web.sidecar;

import java.util.Set;


/**
 * Provides paths which are not covered by the default/main app security
 */
public interface OtherWebSecurityPathsProvider
{
	default boolean enabled()
	{
		return true;
	}
	
	/**
	 * @return A set of Ant-like paths, e.g. /abc/**
	 */
	Set<String> paths();
	
	default Set<String> paths(final boolean withLogin)
	{
		return this.paths();
	}
}
