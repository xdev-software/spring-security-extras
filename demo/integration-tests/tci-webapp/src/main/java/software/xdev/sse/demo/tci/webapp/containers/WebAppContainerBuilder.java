package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.testcontainers.imagebuilder.AdvancedImageFromDockerFile;
import software.xdev.testcontainers.imagebuilder.compat.DockerfileCOPYParentsEmulator;


@SuppressWarnings("PMD.MoreThanOneLogger")
public final class WebAppContainerBuilder
{
	private static final Logger LOG = LoggerFactory.getLogger(WebAppContainerBuilder.class);
	private static final Logger LOG_CONTAINER_BUILD =
		LoggerFactory.getLogger("container.build.webapp");
	
	private static String builtImageName;
	
	private WebAppContainerBuilder()
	{
	}
	
	public static synchronized String getBuiltImageName(final String module)
	{
		if(builtImageName != null)
		{
			return builtImageName;
		}
		
		LOG.info("Building WebApp-DockerImage...");
		
		final AdvancedImageFromDockerFile builder =
			new AdvancedImageFromDockerFile(module + "-local", false)
				.withLoggerForBuild(LOG_CONTAINER_BUILD)
				.withPostGitIgnoreLines(
					// Ignore git-folder, as it will be provided in the Dockerfile
					".git/**",
					// Ignore other unused folders and extensions
					"*.iml",
					"*.cmd",
					"*.md",
					".config/**",
					".github/**",
					".idea/**",
					".run/**",
					"demo/_dev_infra/**",
					// Ignore other Dockerfiles (our required file will always be transferred)
					"Dockerfile",
					// Ignore not required test-modules that may have changed
					// sources only - otherwise the parent pom doesn't find the resources
					"demo/integration-tests/*/src/**",
					"**/src/test/**",
					// Ignore resources that are just used for development
					"demo/webapp-rest/src/main/resources-dev/**",
					// Most files from these folders need to be ignored -> Down there for highest prio
					"node_modules",
					"target")
				.withDockerFilePath(Paths.get("../../../demo/integration-tests/" + module + "/Dockerfile"))
				.withBaseDir(Paths.get("../../../"))
				.withDockerFileLinesModifier(new DockerfileCOPYParentsEmulator());
		
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
