package at.ait.dme.yuma.suite.apps.core.shared.server.ner;

import java.util.Collection;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("../ner")
public interface NERService extends RemoteService {
	
    public Collection<SemanticTag> getTagSuggestions(String text)
    	throws NERServiceException;

}
