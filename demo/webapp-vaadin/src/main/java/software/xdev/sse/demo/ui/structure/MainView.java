package software.xdev.sse.demo.ui.structure;

import static com.vaadin.flow.component.icon.VaadinIcon.SIGN_OUT;
import static software.xdev.sse.demo.ui.structure.MainView.NAV;

import jakarta.annotation.security.PermitAll;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import software.xdev.sse.demo.buisness.service.session.SessionService;
import software.xdev.sse.demo.buisness.service.session.SessionUser;


@PageTitle("Main")
@Route(value = NAV)
@PermitAll
public class MainView extends VerticalLayout
{
	public static final String NAV = "main";
	
	@Autowired
	protected SessionService sessionService;
	
	private final Span spDummy = new Span("");
	
	public MainView()
	{
		this.addClassName("container");
		
		final Button logoutButton = new Button("Logout", SIGN_OUT.create());
		final Anchor aLogout = new Anchor("/logout", logoutButton);
		// IMPORTANT!
		// This is external and Vaadin tries to do stuff otherwise that crashes it
		aLogout.setRouterIgnore(true);
		
		this.add(this.spDummy, aLogout);
	}
	
	@Override
	protected void onAttach(final AttachEvent attachEvent)
	{
		final SessionUser user = this.sessionService.currentUser();
		
		this.spDummy.setText(user.fullName() + " " + user.email());
	}
}
