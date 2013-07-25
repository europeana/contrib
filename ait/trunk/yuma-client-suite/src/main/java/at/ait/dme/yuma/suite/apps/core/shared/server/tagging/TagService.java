package at.ait.dme.yuma.suite.apps.core.shared.server.tagging;

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../tags")
public interface TagService extends RemoteService {
	
    public Collection<SemanticTag> getTagSuggestions(String text, int limit);
    
    public Collection<String> getVocabularies();
    
    public Collection<SemanticTag> getChildren(String parentUri);

}