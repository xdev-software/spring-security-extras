package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Paths;
import java.time.Duration;

import software.xdev.tci.imagebuild.BuildImage;
import software.xdev.tci.imagebuild.integrations.maven.POMSimpleModuleFilter;


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
					.withDockerFilePath(Paths.get("../../../../demos/demo/integration-tests/" + module
						+ "/Dockerfile"))
					.withBaseDir(Paths.get("../../../../"))
					.configureFilesToTransferHandler(h -> h
						.withPostGitIgnoreLines(
							// Ignore git-folder, as it will be provided in the Dockerfile
							".git/**",
							// Ignore other unused folders and extensions
							".github/**",
							".config/**",
							".idea/**",
							".run/**",
							"*.iml",
							"*.cmd",
							"*.md",
							"assets/**",
							"demos/demo/_dev_infra/**",
							// Ignore other Dockerfiles (our required file will always be transferred)
							"Dockerfile",
							// Ignore not required test-modules that may have changed
							// sources only - otherwise the parent pom doesn't find the resources
							"demos/demo/integration-tests/**",
							"**/src/test/**",
							// Ignore resources that are just used for development
							"demos/demo/webapp-rest/src/main/resources-dev/**",
							// Most files from these folders need to be ignored -> Down there for highest prio
							"node_modules",
							"target")
						.withTransferArchiveTARCompressorCustomizer(c -> c
							// Rewrite parent pom to exclude integration tests
							// This way changes in test pom's cause no redownload of dependencies
							.withContentModifier(POMSimpleModuleFilter.keep("demos", "demo"))
							.withContentModifier(POMSimpleModuleFilter.remove(
								"demos/demo", "integration-tests"))));
				
				return builder;
			});
	}
	
	private WebAppContainerBuilder()
	{
	}
}
