package software.xdev.sse.demo.ui.structure;

import static software.xdev.sse.demo.ui.structure.AnotherView.NAV;

import jakarta.annotation.security.PermitAll;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;


@PageTitle("Another")
@Route(value = NAV)
@PermitAll
public class AnotherView extends VerticalLayout
{
	public static final String NAV = "another";
	
	public AnotherView()
	{
		this.add("Another view");
	}
}
