package software.xdev.sse.demo.ui.structure;

import jakarta.annotation.security.PermitAll;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("")
@Route(value = "", autoLayout = false)
@PermitAll
public class RootRedirectView extends Div implements BeforeEnterObserver
{
	@Override
	public void beforeEnter(final BeforeEnterEvent event)
	{
		event.forwardTo(MainView.class);
	}
}
