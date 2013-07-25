package at.ait.dme.yuma.server.gui.documentation;

import org.apache.wicket.Page;

import at.ait.dme.yuma.server.gui.Navbar;

/**
 * The navbar for the documentation section.
 * 
 * @author Rainer Simon
 */
public class DocumentationNavbar extends Navbar {

	private static final long serialVersionUID = -7540625212385536895L;

	public DocumentationNavbar(Class<? extends Page> selected) {
		addNavbarItem("Overview", Overview.class, Overview.class == selected);
		addNavbarItem("API", API.class, API.class == selected);
		addNavbarItem("Developers", Developer.class, Developer.class == selected);
		addNavbarItem("Contact", Contact.class, Contact.class == selected);
	}
	
}
