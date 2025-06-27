package software.xdev.sse.demo.tci.selenium.testbase;

import java.util.Objects;
import java.util.function.Function;

import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.lifecycle.TestDescription;

import software.xdev.sse.demo.tci.selenium.BrowserTCI;


/**
 * Extension for Selenium integration tests that creates records for tests if required
 */
public abstract class SeleniumIntegrationTestExtension implements AfterTestExecutionCallback
{
	private static final Logger LOG = LoggerFactory.getLogger(SeleniumIntegrationTestExtension.class);
	
	protected final Function<ExtensionContext, BrowserTCI> tciExtractor;
	
	protected SeleniumIntegrationTestExtension(
		final Function<ExtensionContext, BrowserTCI> tciExtractor)
	{
		this.tciExtractor = Objects.requireNonNull(tciExtractor);
	}
	
	@Override
	public void afterTestExecution(final ExtensionContext context) throws Exception
	{
		final BrowserTCI browserTCI = this.tciExtractor.apply(context);
		if(browserTCI != null)
		{
			// Wait a moment, so everything is safe on tape
			Thread.sleep(100);
			
			LOG.debug("Trying to capture video");
			
			browserTCI.afterTest(new TestDescription()
			{
				@Override
				public String getTestId()
				{
					return this.getFilesystemFriendlyName();
				}
				
				@SuppressWarnings("checkstyle:MagicNumber")
				@Override
				public String getFilesystemFriendlyName()
				{
					final String testClassName = this.cleanForFilename(context.getRequiredTestClass().getSimpleName());
					final String displayName = this.cleanForFilename(context.getDisplayName());
					return System.currentTimeMillis()
						+ "_"
						+ testClassName
						+ "_"
						// Cut off otherwise file name is too long
						+ displayName.substring(0, Math.min(displayName.length(), 200));
				}
				
				private String cleanForFilename(final String str)
				{
					return str.replace(' ', '_')
						.replaceAll("[^A-Za-z0-9#_-]", "")
						.toLowerCase();
				}
			}, context.getExecutionException());
		}
		LOG.debug("AfterTestExecution done");
	}
}
