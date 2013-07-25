package at.ait.dme.yuma.server.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;

@Provider
public class AnnotationDatabaseExceptionMapper 
	implements ExceptionMapper<AnnotationDatabaseException> {

	@Override
	public Response toResponse(AnnotationDatabaseException e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}
}
