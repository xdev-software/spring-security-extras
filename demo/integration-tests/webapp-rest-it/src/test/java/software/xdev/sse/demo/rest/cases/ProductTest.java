package software.xdev.sse.demo.rest.cases;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.rnorth.ducttape.unreliables.Unreliables;

import software.xdev.sse.demo.persistence.jpa.dao.ProductDAO;
import software.xdev.sse.demo.rest.base.InfraPerCaseTest;
import software.xdev.sse.demo.rest.datageneration.ProductDG;
import software.xdev.sse.demo.tci.selenium.TestBrowser;


class ProductTest extends InfraPerCaseTest
{
	@DisplayName("Create product")
	@ParameterizedTest
	@EnumSource(TestBrowser.class)
	void create(final TestBrowser browser)
	{
		this.startAll(
			browser,
			dbtci -> dbtci.useNewEntityManager(em -> new ProductDG(em).generateProduct("PLACEHOLDER")));
		
		this.loginAndGotoMainSite();
		
		this.navigateTo("swagger-ui/index.html#/product-controller/create");
		
		final WebElement taInput = Unreliables.retryUntilSuccess(
			2,
			() -> {
				// Sometimes the button is clicked but JS doesn't react (likely because it's not loaded yet)
				this.waitUntil(d -> d.findElement(By.className("try-out__btn"))).click();
				return this.waitUntil(d -> d.findElement(By.className("body-param__text")));
			});
		taInput.sendKeys(Keys.CONTROL + "a");
		taInput.sendKeys(Keys.DELETE);
		taInput.sendKeys("""
			{
			  "name": "TEST-ABC"
			}""");
		
		this.waitUntil(d -> d.findElement(By.className("execute"))).click();
		
		final WebElement liveResponseTable = this.waitUntil(d -> d.findElement(By.className("live-responses-table")));
		// First element contains response body
		final String responseText = liveResponseTable.findElements(By.className("microlight")).get(0).getText();
		// Id should be 2 as another product was created before
		Assertions.assertEquals("""
			{
			  "id": 2,
			  "name": "TEST-ABC"
			}""", responseText);
		
		this.dbInfra().useNewEntityManager(em -> {
			final ProductDAO productDAO = new ProductDAO(em);
			Assertions.assertNotNull(productDAO.findByName("TEST-ABC"));
		});
	}
}
