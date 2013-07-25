package at.ait.dme.yuma.suite.framework;

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

	private static final String RDF_LINK =
		"<link rel=\"alternate\" type=\"application/rdf+xml\" href=\"@href@\" " + 
		"title=\"Structured Descriptor Document (RDF/XML format)\"/>";
	
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
    
    public static final LinkHeaderContributor forRDF(final String url) {
        return new LinkHeaderContributor(new IHeaderContributor() {
			private static final long serialVersionUID = 3303794269797901376L;

			public void renderHead(IHeaderResponse response) {
                response.renderString(RDF_LINK.replace("@href@", url));
            }
        });  
    }
    
} 