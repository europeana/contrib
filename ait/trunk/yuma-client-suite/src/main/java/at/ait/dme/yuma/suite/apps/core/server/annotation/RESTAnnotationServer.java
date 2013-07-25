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

package at.ait.dme.yuma.suite.apps.core.server.annotation;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.jboss.resteasy.client.ClientResponse;

/**
 * The interface to the RESTful YUMA Annotation Server
 * 
 * @author Christian Sadilek
 * @author Rainer Simon
 */
@Path("/")
public interface RESTAnnotationServer {
	
	@POST
	@Consumes("application/json")
	@Path("/annotation")
	public ClientResponse<String> createAnnotation(String annotation);
	
	@PUT
	@Consumes("application/json")
	@Path("/annotation/{id}")
	public ClientResponse<String> updateAnnotation(@PathParam("id") String id, String annotation);
	
	@DELETE
	@Path("/annotation/{id}")
	public ClientResponse<String> deleteAnnotation(@PathParam("id") String id);
	
	@GET
	@Produces("application/json")
	@Path("/tree/{objectId}")
	public ClientResponse<String> getAnnotationTree(@PathParam("objectId") String ojbectId);

}
