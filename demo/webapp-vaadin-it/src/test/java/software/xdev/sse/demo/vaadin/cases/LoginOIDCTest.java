package software.xdev.sse.demo.vaadin.cases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import software.xdev.sse.demo.entities.UserDetail;
import software.xdev.sse.demo.persistence.jpa.dao.UserDetailDAO;
import software.xdev.sse.demo.tci.selenium.TestBrowser;
import software.xdev.sse.demo.vaadin.base.InfraPerCaseTest;
import software.xdev.sse.demo.vaadin.datageneration.DefaultDG;


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
