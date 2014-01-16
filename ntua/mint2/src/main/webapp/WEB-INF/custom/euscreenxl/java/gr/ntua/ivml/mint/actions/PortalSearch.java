package gr.ntua.ivml.mint.actions;


import gr.ntua.ivml.mint.concurrent.Solarizer;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.XMLNode;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;

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
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ParameterAware;


@Results({
	  @Result(name="input", location="home.jsp"),
	  @Result(name="error", location="home.jsp"),
	  @Result(name="success", location="xom_output.jsp")
	})

public class PortalSearch extends GeneralAction implements ParameterAware {
	
	public static Logger log = Logger.getLogger( PortalSearch.class );
	public static final int FIELD_LIMIT = 20;
	
	Map<String, String[]> params;
	
	Element result;
	Element errors;

	static class Tag implements Comparable<Tag> {
		String term;
		double score;
		public int compareTo( Tag b ) {
			return term.compareTo(b.term);
		}
		public boolean equals( Tag b ) {
			return term.equals( b.term );
		}
	}

	static Element helpPage = null;
	
	Map<String,Tag> tagmap = new HashMap<String,Tag>();
	
	// Requested number of Tags
	int tags = -1;
	
    //@Action(value="Search",interceptorRefs=@InterceptorRef("defaultStack"))
    public String execute() throws Exception {
    	
    	// which parameters do we need
    	// start position
    	// count
    	// fields set / field list
    	// query
    	// sort ascending descending
    	// field name translation??
    	// Definitely no prefixes on ebucore
    	
    	if( params == null ) {
    		log.error( "No parameters passed." );
    		return ERROR;
    	}
    	
		try {
			result = new Element("results");
			errors = new Element("errors");

			// request for item id's gives back the whole xml for those items
			// wrapped in <result> /record[item_id=""]
			if (params.containsKey("item_id")) {
				itemsResult(params.get("item_id"));
			} else {
				// Try to create a SolrQuery
				SolrQuery sq = new SolrQuery();

				preQuery( sq );
				
				if (params.containsKey("query")) {
					sq.setQuery(params.get("query")[0]);
				} else {
					help();
					return SUCCESS;
				}

				if( params.containsKey( "filter_query")) {
						sq.setFilterQueries(params.get("filter_query" ));
				}

				if (params.containsKey("start")) {
					try {
						int start = Integer.parseInt(params.get("start")[0]);
						sq.setStart(start);
					} catch (NumberFormatException e) {
						errorNode(params.get("start")[0]
								+ " not a valid number.");
					}
				}

				if (params.containsKey("max_rows")) {
					try {
						int maxRows = Integer
								.parseInt(params.get("max_rows")[0]);
						sq.setRows(maxRows);
					} catch (NumberFormatException e) {
						errorNode(params.get("max_rows")[0]
								+ " is not a valid number.");
					}
				}

				if( !params.containsKey("max_rows") ||
						sq.getRows() > 0 ) {
					if( params.containsKey( "tags" )) {
						try {
							tags = Integer.parseInt( params.get("tags" )[0]);
							sq.setParam("tv", true);
							sq.setParam("tv.fl", "tags" );
							sq.setParam("tv.tf_idf ", true );
							sq.setParam("tv.all", true );
							
						} catch (NumberFormatException e2) {
							errorNode(params.get("tags")[0]
							                             + " is not a valid number.");
						}
					}
				}
				
				if (params.containsKey("field")) {
					sq.setFields(params.get("field"));
				}

				if (params.containsKey("field_set")) {
					log.debug("field_set not yet supported");
					errorNode("Fieldset support is not there yet.");
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
				QueryResponse qr = Solarizer.getSolrServer().query(sq);
				responseResult(qr);

			}
		} catch (Exception e) {
			StringWriter outString = new StringWriter();
			e.printStackTrace(new PrintWriter(outString));
			errorNode(outString.toString());
		}
		
    	return SUCCESS;
    }

    /**
     * Override to make modification to sq before the main functionality
     * or mess with the params - Map
     * @param sq
     */
    protected void preQuery(SolrQuery sq) throws Exception {

    }

	private void helpParam( Element parent, String paramName, String helptext ) {
    	Element param = new Element( "param" );
    	param.addAttribute( new Attribute( "name",paramName ));
    	param.appendChild( helptext );
    	parent.appendChild( param );
    }
    
    /**
     * XML - ized help for server use
     * 
     */
    public void help() {
    	if( helpPage != null ) {
    		result =(Element) helpPage.copy();
    		return;
    	}
    	
    	Element help = new Element( "help" );
    	Element elem = new Element( "supported_params");
    	help.appendChild(elem);
    
    	helpParam( elem, "query", "A lucene query in QueryParser format." + 
    			"The all:(...) field queries are modified to hit a subset of fields with" +
    			" different weights to have relevance sorted results." );
    	helpParam( elem, "filter_query", "filter queries are \"anded\" to you query and improve the performance of caching. Multiple filter_query parameters possible.");
    	helpParam( elem, "max_rows", "How many fieldsets do you want to get back maximally?" );
    	helpParam( elem, "start", "Where in the result list should the output start.");
    	helpParam( elem, "field", "You can specify which fields you want back. Multiple fields possible." );
    	helpParam( elem, "item_id", "When you have the item_ids you can specify to get the XML for those items back." + 
    			" They are wrapped in <items> and <item> tags. Add as many item_id=??? as you like." );
    	helpParam( elem, "sort", "Specify a field to sort by, preceed the name with '-' for descending sort. Multiple sorts possible." );
    	helpParam( elem, "facet_field", "Field name where facets should be reported. (value and hit count for field).");
    	helpParam( elem, "relatedItems", "Searches for related items based on given EUScreen id of item.");
    	helpParam( elem, "tags", "If and how many tags for a tag cloud should be generated.");
    	helpParam( elem, "sitemap", "With any value will return a list of production=yes video urls.");
    	
    	elem = new Element( "fields" );
    	help.appendChild( elem );
    	LukeRequest lr = new LukeRequest();
    	try {
			LukeResponse lp  = lr.process( Solarizer.getSolrServer());
			List<String> fieldNames = new ArrayList<String>();
			fieldNames.addAll( lp.getFieldInfo().keySet());
			Collections.sort( fieldNames );
			for( String name: fieldNames ) {
				Element fieldName = new Element( "fieldname");
				fieldName.appendChild( name );
				elem.appendChild( fieldName );
			}
		}  catch (Exception e) {
			log.error( "Request for field names failed!", e);
		}	
    	
		result.appendChild( help );
		helpPage=(Element) result.copy();
    }
    
    /**
     * Convert into XML nodes.
     * @param sr
     */
    public void responseResult( QueryResponse qr ) {
    	Element searchMeta = new Element( "searchMeta" );
    	Element tagsElem = null;
    	
    	SolrDocumentList sdl = qr.getResults();

    	Element elem = new Element( "hits" );
    	elem.appendChild( Long.toString( sdl.getNumFound()));
    	searchMeta.appendChild( elem );

    	elem = new Element( "first" );
    	elem.appendChild( Long.toString( sdl.getStart()));
    	searchMeta.appendChild( elem );
    	
    	elem = new Element( "count" );
    	elem.appendChild( Long.toString( sdl.size()));
    	searchMeta.appendChild( elem );
    	
    	Element docs = new Element( "partialItems");

    	if( tags > 0 ) {
    		updateTaglist( qr );
    		appendTagXml( searchMeta );
    	}

    	
    	for( SolrDocument sd: sdl ) {
    		Element item = new Element( "partialItem" );
    		
    		for( String fieldName: sd.getFieldNames() ) {
    			int limit = FIELD_LIMIT;
    			for( Object val: sd.getFieldValues(fieldName)) {
    				if( limit == 0 ) break;
    				else limit -= 1 ;
    				
    				elem = new Element( "field" );
    				elem.addAttribute( new Attribute( "name", fieldName ));
    				elem.appendChild( val.toString() );
    				item.appendChild( elem );
    			}
    		}
    		docs.appendChild( item );
    	}
    	
    	if( qr.getFacetFields() != null ) {
    		for( FacetField ff : qr.getFacetFields() ) {
    			Element facet = new Element( "facet_field" );
    			facet.addAttribute(new Attribute( "name", ff.getName()));
    			for( Count c: ff.getValues()) {
    				Element facetValue = new Element( "facetValue" );
    				facetValue.addAttribute(new Attribute( "value", c.getName()));
    				facetValue.appendChild(Long.toString( c.getCount()));
    				facet.appendChild( facetValue );
    			}
    			searchMeta.appendChild( facet );
    		}
    	}
    	result.appendChild(searchMeta);
    	result.appendChild( docs );
    }
    
     /**
     * In case items are requested directly, this is what we give back.
     * @param itemIds
     */
    public void itemsResult( String[] itemIds) {
    	ArrayList<Item> itemNodes = new ArrayList<Item>();
    	for( String itemId: itemIds ) {
    		try {
    			long dbId = Long.parseLong(itemId);
    			Item item = DB.getItemDAO().getById(dbId, false);
    			
    			if( item == null ) {
    				errorNode( itemId + " is not a valid item id.");
    				continue;
    			} 
    			itemNodes.add( item );
    		} catch( NumberFormatException nf ) {
    			errorNode( "item_id " + itemId + " is not a valid number" );
    		}
    	}
    	if( itemNodes.size()>0 ) {
    		Element items = new Element( "items" );
    		for( Item item: itemNodes ) {
    			Element itemElement = new Element( "item" );
    			itemElement.addAttribute(new Attribute( "item_id", Long.toString( item.getDbID()) ));
    			// need to DOM the xml
    			itemElement.appendChild(item.getXml());
    			items.appendChild(itemElement);
    		}
    		result.appendChild( items );
    	}
    }
    
    public String getXml() {
    	if( errors.getChildCount() > 0 ) {
    		result.appendChild(errors);
    	}
    	String xml = new Document( result ).toXML();
    	return xml;
    }
    
    public void setXml( String ignored ) {
    	// not sure this one is needed
    }
    
    
    private void errorNode( String mesg ) {
    	Element err = new Element( "error" );
    	err.appendChild( mesg );
    	errors.appendChild( err );
    }
    
    public void updateTaglist( QueryResponse qr ) {
    	
    }
    
    public void appendTagXml( Element result ) {
    	Element tagsElem = new Element( "tags");
		
		result.appendChild(tagsElem);
		
		double maxScore = 0.0;
		SortedSet<Tag> sortedList = new TreeSet<Tag>( new Comparator<Tag>() {
			public int compare( Tag a, Tag b ) {
				if( a.score > b.score ) return -1;
				if( a.score < b.score ) return 1;
				if( a.term.length() > b.term.length() ) return -1;
				if( a.term.length() < b.term.length() ) return 1;
				return 0;
			}
		});

		sortedList.addAll(tagmap.values());
		if( sortedList.size() > 0 ) {
			maxScore = sortedList.first().score;
			for( Tag t: sortedList ) {

					Element tagElem = new Element( "tag" );
					tagElem.addAttribute(new Attribute( "term", t.term ));
					tagElem.addAttribute(new Attribute( "score", Integer.toString( (int) (100*(t.score/maxScore)))));
					tagsElem.appendChild(tagElem);

					if( --tags <=0 ) break;
			}
		}
    }
    
    
	@Override
	public void setParameters(Map<String, String[]> params) {
		this.params = params;
	}
	
	/**
	 * escape fieldname so its usable in a query string.
	 * @param fieldname
	 * @return
	 */
	public static String fieldEscape( String fieldname ) {
		return fieldname.replaceAll(":", "\\\\:" );
	}
}
