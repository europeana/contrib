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

package at.ait.dme.yuma.suite.apps.core.server.ner;

import java.util.Collection;

import org.apache.commons.httpclient.HttpClient;
import org.jboss.resteasy.client.ClientResponse;
import org.jboss.resteasy.client.ProxyFactory;
import org.jboss.resteasy.client.core.executors.ApacheHttpClientExecutor;

import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERService;
import at.ait.dme.yuma.suite.apps.core.shared.server.ner.NERServiceException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * Implementation of the {@link NERService}
 * 
 * @author Manuel Gay
 * @author Rainer Simon
 */
public class NERServiceImpl extends RemoteServiceServlet implements NERService {

    private static final long serialVersionUID = 828296400911475297L;
    
	/**
	 * DBpedia Spotlight base URL
	 */
	private static final String DBPEDIA_SPOTLIGHT_URL = "http://spotlight.dbpedia.org/";
	    
	@Override
	public Collection<SemanticTag> getTagSuggestions(String text) 
			throws NERServiceException {
		
    	HttpClient client = new HttpClient();
        DBpediaSpotlightEndpoint endpoint = 
        	ProxyFactory.create(DBpediaSpotlightEndpoint.class, DBPEDIA_SPOTLIGHT_URL,
        		new ApacheHttpClientExecutor(client));
      
        ClientResponse<String> response = endpoint.annotate(text, 0.2, 20);
        DBpediaSpotlightParser parser = new DBpediaSpotlightParser();
        
        return parser.parseJsonResponse(response.getEntity());
	}
    
}
