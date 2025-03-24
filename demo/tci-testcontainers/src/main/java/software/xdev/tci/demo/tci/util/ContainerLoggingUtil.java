package software.xdev.sse.demo.tci.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;


public class ContainerLoggingUtil
{
	private static final Logger LOG = LoggerFactory.getLogger(ContainerLoggingUtil.class);
	
	static final ContainerLoggingUtil INSTANCE = new ContainerLoggingUtil();
	
	ContainerLoggingUtil()
	{
		// No impl
	}
	
	public static void redirectJULtoSLF4J()
	{
		INSTANCE.redirectJULtoSLF4JInt();
	}
	
	void redirectJULtoSLF4JInt()
	{
		if(SLF4JBridgeHandler.isInstalled())
		{
			return;
		}
		
		LOG.debug("Installing SLF4JBridgeHandler");
		SLF4JBridgeHandler.removeHandlersForRootLogger();
		SLF4JBridgeHandler.install();
	}
}
