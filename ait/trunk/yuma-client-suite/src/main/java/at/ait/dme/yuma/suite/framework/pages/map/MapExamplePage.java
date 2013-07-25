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

package at.ait.dme.yuma.suite.framework.pages.map;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma.suite.framework.YUMASuite;
import at.ait.dme.yuma.suite.framework.pages.BaseExamplePage;

/**
 * Example page for the map annotation tool.
 * 
 * @author Rainer Simon
 */
public class MapExamplePage extends BaseExamplePage {
	
	public MapExamplePage(final PageParameters parameters) {
		super("YUMA Map Annotation - Examples", parameters);
		
		List<MapExampleLink> links = new ArrayList<MapExampleLink>();
		
		links.add(new MapExampleLink(
				"Map of Upper Austria",
				"../map?objectURI=http://upload.wikimedia.org/wikipedia/commons/4/49/Hirschvogel_Map_Austria.jpg"
		));
		
		links.add(new MapExampleLink(
				"Ortelius World Map 1570",
				"../map?objectURI=" + 
					YUMASuite.getBaseUrl(getWebRequestCycle().getWebRequest().getHttpServletRequest()) +
					"images/samples/OrteliusWorldMap1570.jpg"
		));		
		
		add(new ListView<MapExampleLink>("links", links) {
			private static final long serialVersionUID = 2565049376850724577L;

			@Override
			protected void populateItem(ListItem<MapExampleLink> item) {
				MapExampleLink link = item.getModelObject();
				item.add(new ExternalLink("link", link.href, link.label));
			}
		});
	}
	
	private class MapExampleLink {
		private String label, href;
		
		MapExampleLink(String label, String href) {
			this.label = label;
			this.href = href + devModeParams;
		}	
	}

}
