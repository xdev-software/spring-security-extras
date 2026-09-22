package software.xdev.sse.demo.ui;

import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.vaadin.flow.function.DeploymentConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.SessionInitEvent;
import com.vaadin.flow.server.SessionInitListener;
import com.vaadin.flow.server.VaadinService;
import com.vaadin.flow.server.VaadinServiceInitListener;

import software.xdev.sse.demo.buisness.service.UserService;
import software.xdev.sse.demo.buisness.service.session.SessionService;


@org.springframework.stereotype.Component
@SuppressWarnings("java:S1948")
public class AppInitListener
	implements VaadinServiceInitListener, SessionInitListener
{
	private static final Logger LOG = LoggerFactory.getLogger(AppInitListener.class);
	
	private static final String DETECTED_PROBABLE_SECURITY_PROBLEM = "DETECTED PROBABLE SECURITY PROBLEM";
	private static final String SEC_LEAK = "SEC_LEAK";
	
	@Autowired
	protected SessionService sessionService;
	
	@Autowired
	protected UserService userService;
	
	@Override
	public void sessionInit(final SessionInitEvent event)
	{
		if(Optional.ofNullable(SecurityContextHolder.getContext())
			.map(SecurityContext::getAuthentication)
			.map(Authentication::getDetails)
			.filter(WebAuthenticationDetails.class::isInstance)
			.map(WebAuthenticationDetails.class::cast)
			.map(WebAuthenticationDetails::getSessionId)
			.isEmpty())
		{
			final String highlightBorder =
				"!".repeat(DETECTED_PROBABLE_SECURITY_PROBLEM.length() + 4);
			LOG.error(highlightBorder);
			LOG.error("! {} !", DETECTED_PROBABLE_SECURITY_PROBLEM);
			LOG.error(highlightBorder);
			LOG.error(
				"Vaadin Session initiated but no original authentication session detected! "
					+ "Assuming serious security problem (e.g. unsecured endpoint) which might cause Session leak; "
					+ "Breaking off session initialization process with crash... "
					+ "requestPathInfo=[{}] securityContextAuth=[{}] ",
				event.getRequest().getPathInfo(),
				SecurityContextHolder.getContext().getAuthentication());
			throw new IllegalStateException(SEC_LEAK + " - CRASH SESSION INIT");
		}
		
		this.userService.updateLastLoginToNowAsync(this.sessionService.currentUser().userID());
	}
	
	@Override
	public void serviceInit(final ServiceInitEvent event)
	{
		LOG.debug("ServiceInit");
		
		event.getSource().addSessionInitListener(this);
		
		final DeploymentConfiguration deploymentConfig = VaadinService.getCurrent().getDeploymentConfiguration();
		LOG.info("VaadinService-InitParams:");
		for(final Map.Entry<Object, Object> entry : deploymentConfig.getInitParameters().entrySet())
		{
			LOG.info(" - {}='{}'", entry.getKey(), entry.getValue());
		}
	}
}
