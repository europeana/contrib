package at.researchstudio.dme.annotation.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

@Provider
public class AnnotationNotFoundExceptionMapper 
	implements ExceptionMapper<AnnotationNotFoundException> {

	@Override
	public Response toResponse(AnnotationNotFoundException anfe) {
		return Response.status(Status.NOT_FOUND).entity(anfe.getMessage()).build();
	}
}
