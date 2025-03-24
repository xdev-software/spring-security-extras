package software.xdev.sse.demo.webapp.cases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import software.xdev.sse.demo.tci.selenium.TestBrowser;
import software.xdev.sse.demo.webapp.base.InfraPerCaseTest;


class LoginOIDCTest extends InfraPerCaseTest
{
	@DisplayName("Check Login and Logout")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkLoginAndLogout(final TestBrowser browser)
	{
		this.startAll(browser);
		
		Assertions.assertDoesNotThrow(() ->
		{
			this.loginAndGotoMainSite();
			this.logout();
		});
	}
	
	@DisplayName("Check Login and Logout2")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkLoginAndLogout2(final TestBrowser browser)
	{
		// Better testcase could be here but due to simplicity currently not
		this.checkLoginAndLogout(browser);
	}
	
	@DisplayName("Check Login and Logout3")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkLoginAndLogout3(final TestBrowser browser)
	{
		// Better testcase could be here but due to simplicity currently not
		this.checkLoginAndLogout2(browser);
	}
}
