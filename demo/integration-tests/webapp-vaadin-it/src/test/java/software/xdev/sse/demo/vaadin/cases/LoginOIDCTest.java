package software.xdev.sse.demo.vaadin.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.rnorth.ducttape.unreliables.Unreliables;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.persistence.jpa.dao.UserDetailDAO;
import software.xdev.sse.demo.vaadin.base.InfraPerCaseTest;
import software.xdev.sse.demo.vaadin.datageneration.DefaultDG;
import software.xdev.tci.selenium.TestBrowser;


class LoginOIDCTest extends InfraPerCaseTest
{
	@DisplayName("Check Login with existing user")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkLoginWithExistingUser(final TestBrowser browser)
	{
		this.startAll(browser, dbCtrl -> dbCtrl.useNewEntityManager(em -> new DefaultDG(em).generateAll()));
		
		this.loginAndGotoMainSite();
		
		this.checkForMainPage();
	}
	
	@DisplayName("Signup of user")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkSignupOfUser(final TestBrowser browser)
	{
		this.startAll(browser);
		
		this.loginAndGotoMainSite();
		
		this.checkForMainPage();
		
		final UserDetailDAO userDetailDAO = new UserDetailDAO(this.dbInfra().createEntityManager());
		final UserDetail user = userDetailDAO.getUserByEmail(this.oidcInfra().getDefaultUserEmail()).orElse(null);
		
		assertNotNull(user);
		assertEquals(this.oidcInfra().getDefaultUserName(), user.getFullName());
	}
	
	@DisplayName("Re-Login should keep url")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkReLoginShouldKeepUrl(final TestBrowser browser)
	{
		this.startAll(browser, dbCtrl -> dbCtrl.useNewEntityManager(em -> new DefaultDG(em).generateAll()));
		
		this.loginAndGotoMainSite();
		this.navigateTo("another");
		
		// Delete all cookies of the CURRENT domain
		// Retry because GHA machine is sometimes failing here
		Unreliables.retryUntilSuccess(
			3, () -> {
				this.getWebDriver().manage().deleteAllCookies();
				return this.getWebDriver().manage().getCookies().isEmpty();
			});
		this.getWebDriver().navigate().refresh();
		
		// Should be restored to the same path/view
		Assertions.assertDoesNotThrow(() ->
			this.waitUntil(ExpectedConditions.urlToBe(this.getWebAppBaseUrl() + "/another?continue")));
	}
	
	@DisplayName("Re-Login should not keep url if view does not exist")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkReLoginShouldNOTKeepUrlIfViewDoesNotExist(final TestBrowser browser)
	{
		this.startAll(browser, dbCtrl -> dbCtrl.useNewEntityManager(em -> new DefaultDG(em).generateAll()));
		
		this.loginAndGotoMainSite();
		this.navigateTo("anotherThatDoesNotExist");
		
		// Delete all cookies of the CURRENT domain
		this.getWebDriver().manage().deleteAllCookies();
		this.getWebDriver().navigate().refresh();
		
		// Should be restored to the same path/view
		Assertions.assertDoesNotThrow(() ->
			this.waitUntil(ExpectedConditions.urlToBe(this.getWebAppBaseUrl() + "/main")));
	}
	
	@DisplayName("Check Login works when OIDC offline")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void checkLoginWhenOIDCOffline(final TestBrowser browser)
	{
		this.startAll(browser, dbCtrl -> dbCtrl.useNewEntityManager(em -> new DefaultDG(em).generateAll()));
		
		this.loginAndGotoMainSite();
		
		// Assume that OIDC Provider is offline
		this.oidcInfra().stop();
		
		// Delete Session Cookie
		this.getWebDriver().manage().deleteCookieNamed("JSESSIONID");
		
		// NO OIDC Login should be required
		this.navigateTo("");
		this.checkForMainPage();
		
		assertTrue(this.appInfra().getContainer().getLogs().contains("Restored auth"));
	}
}
