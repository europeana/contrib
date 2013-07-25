package at.ait.dme.yuma.server.gui;

import org.apache.wicket.behavior.SimpleAttributeModifier;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma.server.gui.Navbar.NavbarItem;
import at.ait.dme.yuma.server.gui.search.Search;

/**
 * Base class for all pages that display text (i.e. documentation
 * pages).
 * 
 * @author Rainer Simon
 */
public abstract class BaseTextPage extends WebPage {

	private static final String SPAN = "<span class=\"grad\"></span>";
	
	public BaseTextPage (String title, String heading, Navbar navbar) {
		add(new Label("title", title));	
		add(new Label("heading", SPAN + heading).setEscapeModelStrings(false));

		add(new ListView<NavbarItem>("navbar", navbar.getItems()) {
			private static final long serialVersionUID = 9140947690636209796L;

			@SuppressWarnings({ "unchecked", "rawtypes" })
			protected void populateItem(ListItem<NavbarItem> item) {
				NavbarItem n = item.getModelObject();
				item.add(
					new BookmarkablePageLink("nav-item", n.getPageClass())
						.add(new Label("nav-label", n.getLabel())));
				
				if (n.isSelected())
					item.add(new SimpleAttributeModifier("class", "selected"));
		    }
		});
		
		add(new BookmarkablePageLink<String>("home", Search.class));
	}

}
