package at.ait.dme.yuma.server.exception.mapper;

import java.io.UnsupportedEncodingException;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

@Provider
public class UnsupportedEncodingExceptionMapper 
	implements ExceptionMapper<UnsupportedEncodingException> {

	@Override
	public Response toResponse(UnsupportedEncodingException e) {
		return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
	}
}
