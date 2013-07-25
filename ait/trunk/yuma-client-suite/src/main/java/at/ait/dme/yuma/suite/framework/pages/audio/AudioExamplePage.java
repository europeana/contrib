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

package at.ait.dme.yuma.suite.framework.pages.audio;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.link.ExternalLink;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;

import at.ait.dme.yuma.suite.framework.pages.BaseExamplePage;

/**
 * Example page for the audio annotation tool.
 * 
 * @author Rainer Simon
 */
public class AudioExamplePage extends BaseExamplePage {
	
	public AudioExamplePage(final PageParameters parameters) {
		super("YUMA Audio Annotation - Examples", parameters);
		
		// TODO add links to sample material!
		List<AudioExampleLink> links = new ArrayList<AudioExampleLink>();

		add(new ListView<AudioExampleLink>("links", links) {
			private static final long serialVersionUID = 2565049376850724577L;

			@Override
			protected void populateItem(ListItem<AudioExampleLink> item) {
				AudioExampleLink link = item.getModelObject();
				item.add(new ExternalLink("link", link.href, link.label));
			}
		});
	}
	
	private class AudioExampleLink {
		private String label, href;
		
		AudioExampleLink(String label, String href) {
			this.label = label;
			this.href = href + devModeParams;
		}	
	}

}
