package software.xdev.sse.demo.rest.cases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import software.xdev.sse.demo.rest.base.InfraPerCaseTest;
import software.xdev.sse.demo.tci.selenium.TestBrowser;


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
}
