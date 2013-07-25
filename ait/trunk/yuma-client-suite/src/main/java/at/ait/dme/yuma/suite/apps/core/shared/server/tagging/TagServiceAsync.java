package at.ait.dme.yuma.suite.apps.core.shared.server.tagging;

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TagServiceAsync {
	
	public void getTagSuggestions(String text, int maxItems, AsyncCallback<Collection<SemanticTag>> callback);

    public void getVocabularies(AsyncCallback<Collection<String>> callback);
    
    public void getChildren(String parentUri, AsyncCallback<Collection<SemanticTag>> callback);
	
}
