package software.xdev.sse.demo.ui;

import java.util.Map;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.server.AppShellSettings;


@Push
public class AppShell implements AppShellConfigurator
{
	@Override
	public void configurePage(final AppShellSettings settings)
	{
		// Don't use Vaadin's PWA implementation because it can only handle PNGs
		// Also it renders them for each IPhone resolution in existence, which bloats up the delivered content
		settings.addLink(
			"manifest.webmanifest",
			Map.of(
				"rel", "manifest",
				// use-credentials is required otherwise no Auth is sent and the request is discarded
				"crossorigin", "use-credentials"));
		settings.addLink("icon", "icons/icon.svg");
	}
}
