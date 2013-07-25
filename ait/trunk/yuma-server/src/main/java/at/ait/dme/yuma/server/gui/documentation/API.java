package at.ait.dme.yuma.server.gui.documentation;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.basic.Label;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.gui.BaseTextPage;

/**
 * The API doc page.
 * 
 * @author Rainer Simon
 */
public class API extends BaseTextPage {
	
	private static final String TITLE = "YUMA Annotation Server - Documentation - API";
	private static final String HEADING = "yuma server > docs > api";
	private static final String BASE_URL = Config.getInstance().getServerBaseUrl() + "api/";
	
	public API(final PageParameters parameters) {
    	super(TITLE, HEADING, new DocumentationNavbar(API.class));
    	
    	add(new Label("api-base-url", BASE_URL));
    	add(new Label("api-annotation-base-url", BASE_URL + "annotation"));
    	add(new Label("api-annotation-url", BASE_URL + "annotation/{id}"));
    	add(new Label("api-tree-url", BASE_URL + "tree/{objectUri}"));    	
    }

}
