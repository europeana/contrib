/* Copyright 2008-2010 Austrian Institute of Technology
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

package at.ait.dme.yuma.suite.apps.image.core.client.tagging;

import java.util.Collection;
import java.util.HashMap;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagService;
import at.ait.dme.yuma.suite.apps.core.shared.server.tagging.TagServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.MultiWordSuggestOracle;
import com.google.gwt.user.client.ui.SuggestBox;
import com.google.gwt.user.client.ui.SuggestOracle.Suggestion;

/**
 * 'Add tag' input field with auto-suggest functionality based
 * on the TagService.
 *  
 * @author Miki Zehetner
 * @author Rainer Simon
 */
public class TagSuggestBox extends Composite {
	
	/**
	 * The Oracle
	 */
	private MultiWordSuggestOracle oracle = new MultiWordSuggestOracle();
	
	/**
	 * The SuggestBox
	 */
	private SuggestBox suggestBox = new SuggestBox(oracle);
	
	/**
	 * Maximum items shown in the suggestBox;
	 */
	private int limit;
	
	/**
	 * Reference to the RPC Tag Service
	 */
	private TagServiceAsync tagService;
	
	/**
	 * Lookup table Label->SemanticTag
	 */
	private HashMap<String, SemanticTag> tags = new HashMap<String, SemanticTag>();
	
	public TagSuggestBox(int maxItems) {
		this.limit = maxItems;
		
		tagService = (TagServiceAsync) 
		GWT.create(TagService.class);
		
		suggestBox.setLimit(limit);
		suggestBox.getTextBox().addKeyUpHandler(new KeyUpHandler() {
			@Override
			public void onKeyUp(KeyUpEvent evt) {
				int key = evt.getNativeKeyCode();
				if (key != KeyCodes.KEY_DOWN && key != KeyCodes.KEY_UP) {
					tagService.getTagSuggestions(suggestBox.getText().trim(), limit, 
						new AsyncCallback<Collection<SemanticTag>>() {
							@Override
							public void onFailure(Throwable arg0) {
								// Ignore
							}

							@Override
							public void onSuccess(Collection<SemanticTag> result) {
								for (SemanticTag t : result) {
									oracle.add(t.getPrimaryLabel());
									tags.put(t.getPrimaryLabel(), t);
								}
								suggestBox.showSuggestionList();
							}
						}	
					);	
				}
			}
		});
		initWidget(suggestBox);
	}
	
	@Override
	public void setStyleName(String style) {
		suggestBox.getTextBox().setStyleName(style);
	}
	
	public void addSelectionHandler(SelectionHandler<Suggestion> handler) {
		suggestBox.addSelectionHandler(handler);
	}
	
	public void clear() {
		suggestBox.getTextBox().setText("");
	}
	
	public SemanticTag getTag() {
		return tags.get(suggestBox.getTextBox().getText());
	}

}
