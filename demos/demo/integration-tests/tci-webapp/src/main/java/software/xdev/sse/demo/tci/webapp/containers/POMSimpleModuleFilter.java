package software.xdev.sse.demo.tci.webapp.containers;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Predicate;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;

import software.xdev.testcontainers.imagebuilder.transfer.fcm.FileLinesContentModifier;


/**
 * @deprecated Replace with TCI version once released
 */
@Deprecated
public class POMSimpleModuleFilter implements FileLinesContentModifier
{
	private final String targetPath;
	private final List<String> keepModules;
	private final List<String> removeModules;
	
	protected POMSimpleModuleFilter(
		final String targetDir,
		final List<String> keepModules,
		final List<String> removeModules)
	{
		this.targetPath = targetDir + (targetDir.isEmpty() ? "" : "/") + "pom.xml";
		this.keepModules = keepModules;
		this.removeModules = removeModules;
	}
	
	public static POMSimpleModuleFilter keep(final String targetDir, final String... keepModules)
	{
		return new POMSimpleModuleFilter(targetDir, List.of(keepModules), List.of());
	}
	
	public static POMSimpleModuleFilter remove(final String targetDir, final String... removeModules)
	{
		return new POMSimpleModuleFilter(targetDir, List.of(), List.of(removeModules));
	}
	
	@Override
	public boolean shouldApply(
		final Path sourcePath,
		final String targetPath,
		final TarArchiveEntry tarArchiveEntry)
	{
		return this.targetPath.equals(targetPath);
	}
	
	@Override
	public List<String> modify(
		final List<String> lines,
		final Path sourcePath,
		final String targetPath,
		final TarArchiveEntry tarArchiveEntry)
	{
		return lines.stream()
			.filter(s -> {
				if(!s.contains("<module>"))
				{
					return true;
				}
				
				final Predicate<String> containsModule = module -> s.contains("<module>" + module);
				if(!this.removeModules.isEmpty() && this.removeModules.stream().anyMatch(containsModule))
				{
					return false;
				}
				
				return this.keepModules.isEmpty()
					|| this.keepModules.stream().anyMatch(containsModule);
			})
			.toList();
	}
	
	@Override
	public boolean isIdentical(final List<String> original, final List<String> created)
	{
		return original.size() == created.size();
	}
}
