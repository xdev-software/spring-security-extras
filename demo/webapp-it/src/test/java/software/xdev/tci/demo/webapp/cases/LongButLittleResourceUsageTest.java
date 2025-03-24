package software.xdev.sse.demo.webapp.cases;

import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import software.xdev.sse.demo.tci.selenium.TestBrowser;
import software.xdev.sse.demo.webapp.base.InfraPerCaseTest;


// These tests were written to show the advantages of PreStarting
class LongButLittleResourceUsageTest extends InfraPerCaseTest
{
	public static Stream<Arguments> simulatedLongTest()
	{
		return IntStream.rangeClosed(0, 2)
			.boxed()
			.flatMap(i -> Stream.of(TestBrowser.values()))
			.map(Arguments::of);
	}
	
	@SuppressWarnings({"java:S2699", "java:S2925"}) // Wanted
	@DisplayName("Simulate long test with little resource usage")
	@ParameterizedTest
	@MethodSource
	void simulatedLongTest(final TestBrowser browser)
	{
		this.startAll(browser);
		
		try
		{
			Thread.sleep(20_000);
		}
		catch(final InterruptedException iex)
		{
			Thread.currentThread().interrupt();
		}
	}
}
