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

package at.ait.dme.yuma.server.enrichment;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.jboss.resteasy.client.ClientResponse;

/**
 * The interface to the RESTful DBpediaLookup Web service.
 *
 * @author Rainer Simon
 */
public interface DBpediaLookupEndpoint {

    @GET
    @Produces({ MediaType.APPLICATION_XML })
    @Path("/api/search.asmx/KeywordSearch")
    public ClientResponse<String> keyWordSearch(
    		@QueryParam("QueryString") String queryString, 
    		@QueryParam("QueryClass") String queryClass, 
    		@QueryParam("MaxHits") String maxHits);
}
