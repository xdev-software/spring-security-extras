package software.xdev.sse.demo.tci.util;

public final class ContainerMemory
{
	// Docker uses 1024 as conversion
	public static final long M128M = 128 * 1024L * 1024L;
	public static final long M256M = M128M * 2;
	public static final long M512M = M256M * 2;
	public static final long M1G = M512M * 2;
	public static final long M2G = M1G * 2;
	
	private ContainerMemory()
	{
	}
}
