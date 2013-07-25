package at.ait.dme.yuma.server.gui.documentation;

import org.apache.wicket.PageParameters;

import at.ait.dme.yuma.server.gui.BaseTextPage;

/**
 * The developer doc page.
 * 
 * @author Rainer Simon
 */
public class Developer extends BaseTextPage {
	
	private static final String TITLE = "YUMA Annotation Server - Documentation - Developer";
	private static final String HEADING = "yuma server > docs > developers";
	
	public Developer(final PageParameters parameters) {
    	super(TITLE, HEADING, new DocumentationNavbar(Developer.class));
	}

}
