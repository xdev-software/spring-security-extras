package software.xdev.sse.demo.rest.base;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import software.xdev.sse.demo.tci.db.DBTCI;


@TestInstance(Lifecycle.PER_CLASS)
public abstract class InfraPerClassTest extends BaseTest
{
	@BeforeAll
	public void beforeAll()
	{
		this.startBaseInfrastructure(this::onDataBaseMigrated);
	}
	
	protected void onDataBaseMigrated(final DBTCI dbCtrl)
	{
		// Default: Nothing
	}
	
	@AfterEach
	public void afterEach()
	{
		this.stopWebDriver();
	}
	
	@AfterAll
	public void afterAll()
	{
		this.stopEverything();
	}
}
