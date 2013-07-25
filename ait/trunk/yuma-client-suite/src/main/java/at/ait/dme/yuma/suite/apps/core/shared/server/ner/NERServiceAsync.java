package at.ait.dme.yuma.suite.apps.core.shared.server.ner;

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface NERServiceAsync {
	
	public void getTagSuggestions(String text, 
			AsyncCallback<Collection<SemanticTag>> callback);

}
