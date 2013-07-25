package at.ait.dme.yuma.server.gui;

import org.apache.wicket.behavior.HeaderContributor;
import org.apache.wicket.markup.html.IHeaderContributor;
import org.apache.wicket.markup.html.IHeaderResponse;

/**
 * An additional HeaderContributor for RSS auto-discovery links.
 * 
 * @author Rainer Simon
 */
public class LinkHeaderContributor extends HeaderContributor {

	private static final long serialVersionUID = -2587519445989482910L;

	private static final String RSS_LINK =
		"<link rel=\"alternate\" type=\"application/rss+xml\" href=\"@href@\" />";
	
	public LinkHeaderContributor(IHeaderContributor headerContributor) {
		super(headerContributor);
	}

    public static final LinkHeaderContributor forRss(final String url) {

        return new LinkHeaderContributor(new IHeaderContributor() {
        	private static final long serialVersionUID = -3678334461356322549L;

			public void renderHead(IHeaderResponse response) {
                response.renderString(RSS_LINK.replace("@href@", url));
            }
        });
        
    }
} 