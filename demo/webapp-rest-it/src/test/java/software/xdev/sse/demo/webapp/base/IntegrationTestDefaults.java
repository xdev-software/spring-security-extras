package software.xdev.sse.demo.webapp.base;

import java.time.Duration;
import java.util.Objects;
import java.util.function.Function;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


@SuppressWarnings("java:S119")
public interface IntegrationTestDefaults<SELF extends BaseTest>
{
	@SuppressWarnings("unchecked")
	default SELF self()
	{
		return (SELF)this;
	}
	
	default void navigateToOIDCLoginPage()
	{
		this.navigateTo("login");
		this.waitUntil(d -> d.findElement(By.xpath("//a[@href='/oauth2/authorization/local']")))
			.click();
		this.waitUntil(ExpectedConditions.urlContains(this.self().oidcInfra().getInternalHttpBaseEndPoint()));
	}
	
	default void loginAndGotoMainSite()
	{
		this.loginAndGotoMainSite(
			this.self().oidcInfra().getDefaultUserEmail(),
			this.self().oidcInfra().getDefaultUserPassword());
	}
	
	default void loginAndGotoMainSite(final String email, final String pw)
	{
		this.navigateToOIDCLoginPage();
		
		// Login on Identity server
		this.doLoginOnOIDC(email, pw);
		
		this.checkForMainPage();
	}
	
	default void navigateToLogout()
	{
		this.navigateTo("logout");
	}
	
	default void logout()
	{
		this.navigateToLogout();
		
		this.waitUntil(d -> d.findElements(By.className("form-signin-heading")));
		this.waitUntil(d -> d.findElement(By.xpath("//button[@type='submit']"))).click();
	}
	
	default void navigateTo(final String... additionalPathSegments)
	{
		this.self().getWebDriver().get(this.self().getWebAppBaseUrl()
			+ (additionalPathSegments.length > 0 ? ("/" + String.join("/", additionalPathSegments)) : ""));
		
		// Wait for the document to load fully
		this.waitUntil(
			d -> Objects.equals(
				((JavascriptExecutor)d).executeScript("return document.readyState"),
				"complete"));
	}
	
	default void checkForMainPage()
	{
		this.waitUntil(d -> d.findElements(By.className("container")));
	}
	
	default void doLoginOnOIDC(final String email, final String pw)
	{
		this.waitUntil(d -> d.findElement(By.id("Input_Username"))).sendKeys(email);
		this.waitUntil(d -> d.findElement(By.id("Input_Password"))).sendKeys(pw);
		this.waitUntil(d -> d.findElement(By.xpath("//button[@value='login']"))).click();
	}
	
	default <V> V waitUntil(final Function<WebDriver, V> isTrue)
	{
		return this.waitUntil(isTrue, Duration.ofSeconds(10));
	}
	
	default <V> V waitUntil(final Function<WebDriver, V> isTrue, final Duration duration)
	{
		return new WebDriverWait(this.self().getWebDriver(), duration).until(isTrue);
	}
}
