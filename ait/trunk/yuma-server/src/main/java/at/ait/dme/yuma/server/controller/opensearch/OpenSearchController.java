package at.ait.dme.yuma.server.controller.opensearch;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.AbstractAnnotationController;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.model.Annotation;

@Path("/api")
public class OpenSearchController extends AbstractAnnotationController {
	
	@GET
	@Produces("application/rss+xml")
	@Path("/search")	
	public Response getResults(@QueryParam("q") String query, 
			@QueryParam("count") int count, @QueryParam("start") int startIndex) 
			throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		List<Annotation> results = find(query);

		if (count > 0)
			results = results.subList(startIndex, startIndex + count);
		
		OpenSearchFormatHandler format = new OpenSearchFormatHandler(
				query, results.size(), 0, results.size());
		
		return Response.ok(format.serialize(results)).build();		
	}

	private List<Annotation> find(String query)
		throws AnnotationDatabaseException, UnsupportedEncodingException {
		
		AbstractAnnotationDB db = null;
		try {
			db = Config.getInstance().getAnnotationDatabase();
			db.connect(request, response);
			return db.findAnnotations(URLDecoder.decode(query, URL_ENCODING));
		} finally {
			if(db != null) db.disconnect();
		}		
	}
	
}
