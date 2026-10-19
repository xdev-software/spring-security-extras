package software.xdev.sse.demo.vaadin.cases.urlmapping;

import software.xdev.sse.demo.tci.webapp.containers.VaadinWebAppContainer;


class UrlMappingBeforePatchTest extends BaseUrlMappingTest
{
	@Override
	protected void customizeWebAppContainer(final VaadinWebAppContainer c)
	{
		super.customizeWebAppContainer(c);
		c.withEnv("SSE_SIDECAR_HTTP-SECURITY-MATCHER_DEFAULT_CREATOR_ENABLED", "false");
	}
}
