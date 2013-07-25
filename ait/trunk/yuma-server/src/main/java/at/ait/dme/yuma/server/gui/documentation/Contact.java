package at.ait.dme.yuma.server.gui.documentation;

import org.apache.wicket.PageParameters;

import at.ait.dme.yuma.server.gui.BaseTextPage;

public class Contact extends BaseTextPage {
	
	private static final String TITLE = "YUMA Annotation Server - Documentation - Get in Touch";
	private static final String HEADING = "yuma server > docs > get in touch";
	
	public Contact(final PageParameters parameters) {
    	super(TITLE, HEADING, new DocumentationNavbar(Contact.class));
	}
	
}
