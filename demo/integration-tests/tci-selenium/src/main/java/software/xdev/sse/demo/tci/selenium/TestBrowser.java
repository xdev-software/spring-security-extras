package software.xdev.sse.demo.tci.selenium;

import java.util.function.Supplier;

import org.openqa.selenium.Capabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;


public enum TestBrowser
{
	FIREFOX(() -> {
		final FirefoxOptions firefoxOptions = new FirefoxOptions();
		
		final FirefoxProfile firefoxProfile = new FirefoxProfile();
		// Allows to type into console without an annoying SELF XSS popup
		firefoxProfile.setPreference("devtools.selfxss.count", "100");
		firefoxOptions.setProfile(firefoxProfile);
		
		return firefoxOptions;
	}),
	CHROME(ChromeOptions::new);
	
	private final Supplier<Capabilities> capabilityFactory;
	
	TestBrowser(final Supplier<Capabilities> driverFactory)
	{
		this.capabilityFactory = driverFactory;
	}
	
	public Supplier<Capabilities> getCapabilityFactory()
	{
		return this.capabilityFactory;
	}
}
