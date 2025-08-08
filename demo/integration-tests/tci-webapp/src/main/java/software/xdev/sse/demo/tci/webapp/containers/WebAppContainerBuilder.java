package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import software.xdev.testcontainers.imagebuilder.AdvancedImageFromDockerFile;
import software.xdev.testcontainers.imagebuilder.compat.DockerfileCOPYParentsEmulator;
import software.xdev.testcontainers.imagebuilder.transfer.fcm.FileLinesContentModifier;


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
					"demo/integration-tests/**",
					"**/src/test/**",
					// Ignore resources that are just used for development
					"demo/webapp-rest/src/main/resources-dev/**",
					// Most files from these folders need to be ignored -> Down there for highest prio
					"node_modules",
					"target")
				.withDockerFilePath(Paths.get("../../../demo/integration-tests/" + module + "/Dockerfile"))
				.withBaseDir(Paths.get("../../../"))
				.withDockerFileLinesModifier(new DockerfileCOPYParentsEmulator())
				.withTransferArchiveTARCompressorCustomizer(c -> c
					// Rewrite parent pom to exclude integration tests
					// This way changes in test pom's cause no redownload of dependencies
					.withContentModifier(new FileLinesContentModifier()
					{
						@Override
						public boolean shouldApply(
							final Path sourcePath,
							final String targetPath,
							final TarArchiveEntry tarArchiveEntry)
						{
							return "demo/pom.xml".equals(targetPath);
						}
						
						@Override
						public List<String> modify(
							final List<String> lines,
							final Path sourcePath,
							final String targetPath,
							final TarArchiveEntry tarArchiveEntry)
						{
							return lines.stream()
								// Remove integration tests module
								.filter(s -> !s.contains("<module>integration-tests"))
								.toList();
						}
						
						@Override
						public boolean isIdentical(final List<String> original, final List<String> created)
						{
							return original.size() == created.size();
						}
					}));
		
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
