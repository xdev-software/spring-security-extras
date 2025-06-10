package software.xdev.sse.demo.rest.base;

import org.junit.jupiter.api.AfterEach;


public abstract class InfraPerCaseTest extends BaseTest
{
	@AfterEach
	public void afterEach()
	{
		this.stopEverything();
	}
}
