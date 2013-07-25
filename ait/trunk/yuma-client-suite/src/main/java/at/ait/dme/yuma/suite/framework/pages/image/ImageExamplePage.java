/*
 * Copyright 2008-2010 Austrian Institute of Technology
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */

package at.ait.dme.yuma.suite.framework.pages.image;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma.suite.framework.YUMASuite;
import at.ait.dme.yuma.suite.framework.pages.BaseExamplePage;

/**
 * Example page for the image annotation tool.
 * 
 * @author Rainer Simon
 */
public class ImageExamplePage extends BaseExamplePage {
	
	public ImageExamplePage(final PageParameters parameters) {
		super("YUMA Image Annotation - Examples", parameters);
		
		List<ImageExampleLink> links = new ArrayList<ImageExampleLink>();
		
		links.add(new ImageExampleLink(
				"Perth",
				"../image?objectURI=http://www.destination360.com/australia-south-pacific/australia/images/s/australia-perth.jpg"
		));
		
		links.add(new ImageExampleLink(
				"Sample from Swedish National Heritage Board",
				"../image?objectURI=" +
				YUMASuite.getBaseUrl(getWebRequestCycle().getWebRequest().getHttpServletRequest()) +
				"images/samples/snhb-sample.jpg"
		));

		add(new ListView<ImageExampleLink>("links", links) {
			private static final long serialVersionUID = 2565049376850724577L;

			@Override
			protected void populateItem(ListItem<ImageExampleLink> item) {
				ImageExampleLink link = item.getModelObject();
				item.add(new ExternalLink("link", link.href, link.label));
			}
		});
	}
	
	private class ImageExampleLink {
		private String label, href;
		
		ImageExampleLink(String label, String href) {
			this.label = label;
			this.href = href + devModeParams;
		}	
	}

}
