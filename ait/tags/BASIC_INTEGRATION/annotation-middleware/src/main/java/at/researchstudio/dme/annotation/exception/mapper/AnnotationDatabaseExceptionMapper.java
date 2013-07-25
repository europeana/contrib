package at.researchstudio.dme.annotation.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;

@Provider
public class AnnotationDatabaseExceptionMapper 
	implements ExceptionMapper<AnnotationDatabaseException> {

	@Override
	public Response toResponse(AnnotationDatabaseException ade) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(ade.getMessage()).build();
	}
}
