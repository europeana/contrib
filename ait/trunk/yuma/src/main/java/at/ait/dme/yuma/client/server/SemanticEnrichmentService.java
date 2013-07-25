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

package at.ait.dme.yuma.client.server;

import java.util.Collection;

import at.ait.dme.yuma.client.annotation.SemanticTagGroup;
import at.ait.dme.yuma.client.server.exception.SemanticEnrichmentServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("semanticenrichment")
public interface SemanticEnrichmentService extends RemoteService {
    
	/**
	 * String constant for Uni Vienna link discovery service
	 */
	public static final String UNIVIE_LINK_DISCOVERY_SERVICE = "univieLinkDiscoveryService";
	
	/**
	 * String constant for OpenCalais + DBpedia combination
	 */
	public static final String OPENCALAIS_DBPEDIA_LOOKUP = "openCalaisDBpediaLookup";
	
    public Collection<SemanticTagGroup> getTagSuggestions(String text, String service) throws SemanticEnrichmentServiceException;

}
