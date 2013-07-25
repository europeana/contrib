package gr.ntua.ivml.awareness.search;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.request.LukeRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.LukeResponse;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.json.JSONArray;
import org.json.JSONObject;

public class SolrProxy {
	public static final int FIELD_LIMIT=100;
	private static final Logger log = Logger.getLogger( SolrProxy.class );
	public static String PROFILE_STORIES = "story";
	public static String PROFILE_IMAGES = "image";
	
	JSONObject result = new JSONObject();
	
	public JSONObject search(  Map<String, String[]> params ) throws Exception {
		JSONArray errors = new JSONArray();

		try {
			// Try to create a SolrQuery
			SolrQuery sq = new SolrQuery();
			if( params.containsKey( "profile")) {
				String profile = params.get("profile")[0];
				if( PROFILE_STORIES.equals( profile )) {
					// do a simple story search
					if( params.containsKey( "theme")) {
						 sq.addFilterQuery("_theme_title_s:(\"" + params.get("theme")[0] + "\")");;
					}
					sq.addFilterQuery("_className_s:gr.ntua.ivml.awareness.persistent.DigitalStory" );
					sq.addFilterQuery("_isPublished_s:true" );
					//sq.addSortField("_dateCreated_dt", ORDER.desc);
					if (params.containsKey("query")) {
						sq.setQuery("digitalStory_txt:(" + params.get("query")[0] + ")");
					} else {
						return help();
					}
					
				} else if( PROFILE_IMAGES.equals( profile )) {
					sq.addFilterQuery("_className_s:gr.ntua.ivml.awareness.persistent.StoryImage" );

					if (params.containsKey("query")) {
						sq.setQuery("_title_tg:(" + params.get("query")[0] + ") OR " +
								"_description_tg:(" + params.get("query")[0] + ")" );
					} else {
						return help();
					}
				}
			} else {
				if (params.containsKey("query")) {
					sq.setQuery(params.get("query")[0]);
				} else {
					return help();
				}
				
			}
			if (params.containsKey("start")) {
				try {
					int start = Integer.parseInt(params.get("start")[0]);
					sq.setStart(start);
				} catch (NumberFormatException e) {
					errors.put(params.get("start")[0]
							+ " not a valid number.");
				}
			}

			// from is synonym for start
			if (params.containsKey("from")) {
				try {
					int start = Integer.parseInt(params.get("from")[0]);
					sq.setStart(start);
				} catch (NumberFormatException e) {
					errors.put(params.get("from")[0]
							+ " not a valid number.");
				}
			}
			
			// to is the item right after the one to fetch
			// so from:0 to:10 retrieves 10 items numbered 0...9
			// from:10 to:20 retrieves 10 items numbered from 10...19
			if (params.containsKey("to")) {
				try {
					int to = Integer
							.parseInt(params.get("to")[0]);
					int start = sq.getStart();
					
					sq.setRows(to-start);
				} catch (NumberFormatException e) {
					errors.put(params.get("max_results")[0]
							+ " is not a valid number.");
				}
			}

			if (params.containsKey("max_results")) {
				try {
					int maxRows = Integer
							.parseInt(params.get("max_results")[0]);
					sq.setRows(maxRows);
				} catch (NumberFormatException e) {
					errors.put(params.get("max_results")[0]
							+ " is not a valid number.");
				}
			}
			// preQuery( sq );

			if( params.containsKey( "filter_query")) {
				sq.setFilterQueries(params.get("filter_query" ));
			}



			if (params.containsKey("field")) {
				sq.setFields(params.get("field"));
			}


			if (params.containsKey("sort")) {
				for (String sortfield : params.get("sort")) {
					if (sortfield.startsWith("-"))
						sq.addSortField(sortfield.substring(1), ORDER.desc);
					else
						sq.addSortField(sortfield, ORDER.asc);
				}
			}

			if (params.containsKey("facet_field")) {
				for (String facetField : params.get("facet_field")) {
					sq.addFacetField(facetField);
				}
			}

			
			log.info( "Solr Query = \"" + sq.getQuery() +"\"");
			QueryResponse qr = SolrHelper.getSolrServer().query(sq);
			if( params.containsKey( "profile")) {
				String profile = params.get("profile")[0];
				if( PROFILE_STORIES.equals( profile )) {
					storyResult( qr );
				} else if( PROFILE_IMAGES.equals( profile )) {
					imageResult(qr);
				}
			} else 
				responseResult(qr );

		} catch (Exception e) {
			StringWriter outString = new StringWriter();
			e.printStackTrace(new PrintWriter(outString));

			errors.put(outString.toString());
		}

		if( errors.length() > 0 ) 
			return new JSONObject().put( "errors", errors );
		else 
			return result;
	}

	/**
	 * Create the json for DigitalStory search
	 * @param qr
	 */
	public void storyResult( QueryResponse qr ) throws Exception {
		SolrDocumentList sdl = qr.getResults();
		JSONObject searchMeta = new JSONObject()
		.put( "hits",Long.toString( sdl.getNumFound()) )
		.put( "first", Long.toString( sdl.getStart()))
		.put( "count", Long.toString( sdl.size()));

		result.put( "searchMeta", searchMeta );

		JSONArray docs = new JSONArray();
		result.put( "digitalStories", docs );

		for( SolrDocument sd: sdl ) {
			for( Object field: sd.getFieldValues("ui_json_s") ) { 
				log.debug( "ui_json_s " + field);
				JSONObject story = new JSONObject( field.toString());
				docs.put( story );
			}
		}
	}
	
	/**
	 * Create the json for DigitalStory search
	 * @param qr
	 */
	public void imageResult( QueryResponse qr ) throws Exception {
		SolrDocumentList sdl = qr.getResults();
		JSONObject searchMeta = new JSONObject()
		.put( "hits",Long.toString( sdl.getNumFound()) )
		.put( "first", Long.toString( sdl.getStart()))
		.put( "count", Long.toString( sdl.size()));

		result.put( "searchMeta", searchMeta );

		JSONArray docs = new JSONArray();
		result.put( "images", docs );

		for( SolrDocument sd: sdl ) {
			for( Object field: sd.getFieldValues("ui_json_s") ) { 
				log.debug( "ui_json_s " + field);
				JSONObject story = new JSONObject( field.toString());
				docs.put( story );
			}
		}
	}

	public void responseResult( QueryResponse qr ) throws Exception {	   
		SolrDocumentList sdl = qr.getResults();
		JSONObject searchMeta = new JSONObject()
		.put( "hits",Long.toString( sdl.getNumFound()) )
		.put( "first", Long.toString( sdl.getStart()))
		.put( "count", Long.toString( sdl.size()));

		result.put( "searchMeta", searchMeta );


		JSONArray docs = new JSONArray();
		result.put( "partialItems", docs );


		for( SolrDocument sd: sdl ) {
			JSONArray item = new JSONArray();
			for( String fieldName: sd.getFieldNames() ) {
				int limit = FIELD_LIMIT;
				for( Object val: sd.getFieldValues(fieldName)) {
					if( limit == 0 ) break;
					else limit -= 1 ;
					JSONObject field = new JSONObject()
					.put("name", fieldName)
					.put("value", val.toString());
					item.put( field );
				}
			}
			docs.put( item );
		}

		if( qr.getFacetFields() != null ) {
			JSONArray facets = new JSONArray();
			for( FacetField ff : qr.getFacetFields() ) {
				JSONObject facet = new JSONObject();
				facet.put( "fieldname", ff.getName());
				JSONArray values = new JSONArray();
				for( Count c: ff.getValues()) {
					JSONObject facetValue = new JSONObject()
					.put("fieldValue", c.getName())
					.put("count", c.getCount());
					values.put( facetValue );
				}
				facet.put("values", values );
				facets.put( facet );
			}
			searchMeta.put( "facetFields", facets );
		}
	}

	public JSONObject help() throws Exception {
		JSONObject help = new JSONObject();
		JSONArray params = new JSONArray();

		helpParam( params, "query", "A lucene query in QueryParser format." + 
				"The all:(...) field queries are modified to hit a subset of fields with" +
				" different weights to have relevance sorted results." );
		helpParam( params, "filter_query", "filter queries are \"anded\" to you query and improve the performance of caching. Multiple filter_query parameters possible.");
		helpParam( params, "max_results", "How many results do you want to get back maximally (defaults to 10)?" );
		helpParam( params, "to", "The result item after the last to display. count = to-from" );
		helpParam( params, "start", "Where in the result list should the output start. (first and default is 0)");
		helpParam( params, "from", "Synonym to start" );
		helpParam( params, "field", "You can specify which fields you want back. Multiple fields possible." );
		helpParam( params, "sort", "Specify a field to sort by, preceed the name with '-' for descending sort. Multiple sorts possible." );
		helpParam( params, "facet_field", "Field name where facets should be reported. (value and hit count for field).");
		helpParam( params, "theme", "The theme the DigitalStory you are looking for belongs to" );
		helpParam( params, "profile", "Gives special treatment to input and output for certain usecases.\n" +
				"  -- \"story\", use this from the search stories on the UI.\n" + 
				"  -- \"image\", use this for story images on the dsp.");
		JSONArray fields=  new JSONArray();
		help.put( "fields", fields );
		help.put("supported_params", params );
		JSONArray examples = new JSONArray();
		
		help.put("examples", examples );

		examples.put( "A search from the Digital Story search field \"awareness/Search?profile=story&query=$userinput&theme=World War II" );
		examples.put(" - with paging ..Search?query=dogs&start=0&max_rows=10&theme=World War II&profile=story" );
		examples.put( " - list for one theme ..Search?query=*&profile=story&theme=Test Theme&start=10&max_results=10" );
		examples.put( "Search for Story images: ..awareness/Search?profile=image&query=testing&start=0&max_results=2");
		
		LukeRequest lr = new LukeRequest();
		try {
			LukeResponse lp  = lr.process( SolrHelper.getSolrServer());
			List<String> fieldNames = new ArrayList<String>();
			fieldNames.addAll( lp.getFieldInfo().keySet());
			Collections.sort( fieldNames );
			for( String name: fieldNames ) {
				fields.put(name);
			}
		}  catch (Exception e) {
			log.error( "Request for field names failed!", e);
		}	
		return( help );
	}

	private void helpParam( JSONArray parent, String param, String description ) throws Exception {
		JSONObject p = new JSONObject()
		.put("name", param )
		.put( "description", description );
		parent.put( p );
	}

}
