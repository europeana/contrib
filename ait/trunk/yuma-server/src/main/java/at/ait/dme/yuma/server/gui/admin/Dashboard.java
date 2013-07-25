package at.ait.dme.yuma.server.gui.admin;

import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;

@AuthorizeInstantiation("ADMIN")
public class Dashboard extends WebPage {

	public Dashboard() {
		add(new BookmarkablePageLink<String>("logout", LogoutPage.class));
	}
	
}
