package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.testcontainers.imagebuilder.AdvancedImageFromDockerFile;


public final class WebAppContainerBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger(WebAppContainerBuilder.class);
	private static final Logger LOG_CONTAINER_BUILD =
		LoggerFactory.getLogger("container.build.webapp");
	
	private static String builtImageName;
	
	private WebAppContainerBuilder()
	{
	}
	
	public static synchronized String getBuiltImageName()
	{
		if(builtImageName != null)
		{
			return builtImageName;
		}
		
		LOG.info("Building WebApp-DockerImage...");
		
		final AdvancedImageFromDockerFile builder =
			new AdvancedImageFromDockerFile("webapp-it-local", false)
				.withLoggerForBuild(LOG_CONTAINER_BUILD)
				.withAdditionalIgnoreLines(
					// Ignore git-folder, as it will be provided in the Dockerfile
					".git/**",
					// Ignore other unused folders and extensions
					".iml",
					".md",
					"target/**",
					".config/**",
					".idea/**",
					"_dev_infra/**",
					"_resource_metrics/**",
					// Ignore not required test-modules that may have changed
					// sources only - otherwise the parent pom doesn't find the resources
					"tci-*/src/**",
					"persistence-it/src/**",
					"webapp-it/src/**",
					// Ignore resources that are just used for development
					"webapp/src/main/resources-dev/**")
				.withDockerFilePath(Paths.get("../tci-webapp/Dockerfile"))
				.withBaseDir(Paths.get("../"))
				// File is in root directory - we can't access it
				.withBaseDirRelativeIgnoreFile(null);
		
		try
		{
			builtImageName = builder.get(5, TimeUnit.MINUTES);
		}
		catch(final TimeoutException tex)
		{
			throw new IllegalStateException("Timed out", tex);
		}
		
		LOG.info("Built Image; Name ='{}'", builtImageName);
		
		return builtImageName;
	}
}
