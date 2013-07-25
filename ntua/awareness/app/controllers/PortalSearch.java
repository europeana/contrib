package controllers;



import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.search.SolrProxy;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;

import play.mvc.Controller;
import play.mvc.Result;




public class PortalSearch extends Controller {
	public static final Logger log = Logger.getLogger(PortalSearch.class);
	
		
	public static Result search() throws Exception {
		response().setContentType("application/json");
		Map<String,String[]> params = request().queryString();
		if( params.size() == 0 ) return help();
		JSONObject result = new SolrProxy().search( params );
		if( result.optJSONArray("errors") != null )
			return badRequest( result.toString(2));
		else 
			return ok( result.toString(2) );
	}


	public static Result reindex() {
		// check for root user or some other privilege
		SolrHelper.reIndex();
		return ok();
	}

	public static Result help() throws Exception {
		SolrProxy sp = new SolrProxy();
		return( ok(sp.help().toString(2)));
	}
}
