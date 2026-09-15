package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import software.xdev.tci.imagebuild.BuildImage;
import software.xdev.testcontainers.imagebuilder.transfer.fcm.FileLinesContentModifier;


public final class WebAppContainerBuilder
{
	public static String getImageName(final String module)
	{
		return BuildImage.nativeImage(
			module + "-demo",
			Duration.ofMinutes(5),
			builder -> {
				builder
					// NOTE: AOT can't be used properly when JaCoCo is active
					.withBuildArg("ENABLE_AOT", "1")
					.withDockerFilePath(Paths.get("../../../demo/integration-tests/" + module + "/Dockerfile"))
					.withBaseDir(Paths.get("../../../"))
					.configureFilesToTransferHandler(h -> h
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
						// File is in root directory - we can't access it
						.withBaseDirRelativeIgnoreFile(null)
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
							})));
				
				return builder;
			});
	}
	
	private WebAppContainerBuilder()
	{
	}
}
