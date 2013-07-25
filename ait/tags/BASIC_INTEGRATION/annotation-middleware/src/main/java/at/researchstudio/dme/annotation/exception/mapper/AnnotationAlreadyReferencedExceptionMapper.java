package at.researchstudio.dme.annotation.exception.mapper;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;

@Provider
public class AnnotationAlreadyReferencedExceptionMapper 
	implements ExceptionMapper<AnnotationAlreadyReferencedException> {

	@Override
	public Response toResponse(AnnotationAlreadyReferencedException aare) {
		return Response.status(Status.CONFLICT).entity(aare.getMessage()).build();
	}
}
