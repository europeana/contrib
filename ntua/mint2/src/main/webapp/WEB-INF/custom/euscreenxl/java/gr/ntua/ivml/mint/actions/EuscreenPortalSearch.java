package gr.ntua.ivml.mint.actions;


import gr.ntua.ivml.mint.concurrent.Queues;
import gr.ntua.ivml.mint.concurrent.Solarizer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.PrintWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.util.NamedList;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;


@Results({
	  @Result(name="input", location="home.jsp"),
	  @Result(name="error", location="home.jsp"),
	  @Result(name="success", location="xom_output.jsp"),
	  @Result( name="text", type="stream", params={"inputName", "stream"})
	})

public class EuscreenPortalSearch extends PortalSearch {

	static String[] relatedFields = { 
		"/eus:ContentDescriptiveMetadata/eus:ThesaurusTerm_s" 
//		, 
//		"/eus:ContentDescriptiveMetadata/eus:LocalKeyword_s",
//		"/eus:ObjectDescriptiveMetadata/eus:contributor_s",
//		"/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInOriginalLanguage/eus:seriesTitle_s",
//		"/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:SpatialInformation/eus:GeographicalCoverage/@geocovCode_s"
	};
	
	static String NO_RESULT_QUERY = "item_id:x"; // item_id is numeric :-)
	
	static String idField = "/eus:AdministrativeMetadata/eus:identifier_s";
	static String[][] weightedQuery = {
		{ "12", "/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInOriginalLanguage/eus:title_tg" },
		{ "12", "/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInEnglish/eus:title_tg" },
		{ "8", "/eus:ContentDescriptiveMetadata/eus:summary_tg" },
		{ "8", "/eus:ContentDescriptiveMetadata/eus:summaryInEnglish_tg" },
		{ "4", "/eus:ContentDescriptiveMetadata/eus:ThesaurusTerm_tg" },
		{ "4", "/eus:ContentDescriptiveMetadata/eus:genre_tg" },
		{ "4", "/eus:ContentDescriptiveMetadata/eus:topic_tg" },
		{ "2", "/eus:AdministrativeMetadata/eus:provider_tg" },		
		{ "2", "/eus:AdministrativeMetadata/eus:publisherbroadcaster_tg" },		
		{ "2", "/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:broadcastDate_tg" },		
		{ "2", "/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:productionYear_tg" }
	};
	@Override
    @Action(value="Search",interceptorRefs=@InterceptorRef("defaultStack"))
    public String execute() throws Exception {
		if( params.containsKey("sitemap")) return "text";
		return super.execute();
	}
	
	@Override
	protected void preQuery( SolrQuery sq ) throws Exception {
		if( params.containsKey( "relatedItems" )) {
			String itemId = params.get( "relatedItems" )[0];
			String newQuery = relatedItemQuery( itemId );
			String[] sa = new String[1];
			sa[0] = newQuery;
			params.put( "query", sa );
		} else if( params.containsKey( "query" )) {
			String[] originalQuery = params.get( "query" );
			String rewrittenQuery = simpleQueryRewrite(originalQuery[0]);
			originalQuery[0] = rewrittenQuery;
		}
	}
	
	
	/**
	 * Take a query that is basic and against the "all" field and
	 * split it into a query for all the weighted fields as well.
	 * @return
	 */
	protected String simpleQueryRewrite( String originalQuery ) {
		Matcher m = Pattern.compile( "all:\\(([^)(]+)\\)").matcher(originalQuery);
		if( m.find() ) {
			String baseQuery = m.group(1);
			String replaceQuery = constructRelevanzQuery(baseQuery);
			// escape backslashes
			replaceQuery = replaceQuery.replaceAll("\\\\", "\\\\\\\\" );
			return m.replaceFirst(replaceQuery);
		}
		return originalQuery;
	}
	
		
	public String relatedItemQuery( String itemId ) throws Exception {
		SolrDocument sd = itemForRelated( itemId );
		StringBuilder sb = new StringBuilder();
		int maxValueCount = 30;
		
		for( String fieldname: relatedFields ) {
			if( sd.getFieldValues(fieldname) == null ) {
				log.warn( "Field '" + fieldname + "' doesn't have values.");
				continue;
			}
			if( sd.getFieldValues(fieldname).size() == 0 ) continue;
			if( maxValueCount <= 0 ) break;
			sb.append( fieldEscape( fieldname ));
			sb.append( ":( " );
			for( Object val: sd.getFieldValues(fieldname)) {
				sb.append( "\"" + val.toString() + "\" " );
				maxValueCount--;
				if( maxValueCount <=0 ) break;
			}
			sb.append( " ) ");
		}
		if( maxValueCount <=0 )
			log.warn( "Related Item Query for " + itemId + " was cut short." );
		if( sb.length()==0) return NO_RESULT_QUERY;
		sb.append( " AND -");
		sb.append( fieldEscape( idField ));
		sb.append( ":" );
		sb.append( "\"" + itemId + "\"");
		sb.append( " AND +production:yes" );
		return sb.toString();
	}
	
	public SolrDocument itemForRelated( String itemId ) throws Exception {
		SolrQuery sq = new SolrQuery();
		String q = fieldEscape( idField ) +
		":" + itemId;
		log.debug( "Related Item query: " + q);
		sq.setQuery(q);
		
		sq.setFields( relatedFields );
		QueryResponse qr = Solarizer.getSolrServer().query(sq);
		
		SolrDocumentList sdl = qr.getResults();
		// there should only be one ..
		return sdl.get(0);
	}
	
	public static String constructRelevanzQuery( String baseQuery ) {
		StringBuffer result = new StringBuffer();
		result.append( "( " );
		for( String[] s2: weightedQuery ) {
			result.append( fieldEscape( s2[1]));
			result.append( ":" );
			result.append("(" +  baseQuery +")" );
			result.append( "^"+s2[0]+" ");
		}
		
		// make it search everything
		result.append( "all:("+baseQuery+")) ");
		return result.toString();
	}

	@Override 
	public void updateTaglist( QueryResponse qr ) {
		NamedList<Object> res = (NamedList<Object>) qr.getResponse().get("termVectors");

		for( Map.Entry<String, Object> e : res ) {
			if( e.getKey().startsWith( "doc")) {

				NamedList<Object> terms =(NamedList<Object>) ((NamedList<Object>) e.getValue()).get( "tags" );
				
				for( Map.Entry<String, Object> termObj : terms ) {
					String term = termObj.getKey();
					if( term.matches( "^\\p{L}{3,20}$" )) {
						NamedList<Object> termValues = (NamedList<Object>) termObj.getValue();
						int df = ((Integer) termValues.get("df")).intValue();
						double idf = ((Double) termValues.get( "tf-idf")).doubleValue();
						if( df >= 3) {
							// only terms that are in more documents make sense for 
							// tags
							mergeTag( term, idf );
						}
					}
				}
			}			
		}
	}



	private void mergeTag( String term, double score ) {
		Tag t1 = tagmap.get( term );
		if( t1 != null ) {
			t1.score += score;
		} else {
			Tag t2 = new Tag();
			t2.term = term; t2.score = score;
			tagmap.put( term, t2);
		}
	}
	
	private static void sitemap( PrintWriter result ) {
		// generate URLs and write them as UTF8 text out
		String pattern = "<url><loc>http://euscreen.eu/play.jsp?id=%s</loc></url>";
		result.println( "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<urlset xmlns=\"http://www.sitemaps.org/schemas/sitemap/0.9\">\n");
		try {
			int rowCount =  1;
			int start = 0;
			while( rowCount > 0 ) {
				SolrQuery sq = new SolrQuery();
				sq.setQuery("production:yes");
				sq.setFields( idField );
				sq.setRows(1000);
				sq.setStart(start);
		
				QueryResponse qr = Solarizer.getSolrServer().query(sq);

				SolrDocumentList sdl = qr.getResults();
				rowCount = sdl.size();
				for( SolrDocument sd: sdl) {
					if(sd.getFieldValues(idField) != null ) 
		    			for( Object val: sd.getFieldValues(idField)) 
		    				result.println( String.format(pattern, val.toString()));
				}
				result.flush();
				start += rowCount;
			}
			result.println( "</urlset>");
			result.flush();
		}	catch( Exception e ) {
			log.error( "Sitemap failed", e );
		}
	}
	
	public InputStream getStream() throws IOException {
		PipedInputStream pis = new PipedInputStream();
		final PipedOutputStream pos = new PipedOutputStream(pis);
		Runnable r = new Runnable() {

			@Override
			public void run() {
				try {
					PrintWriter pw = new PrintWriter(
							new OutputStreamWriter( pos, "UTF8"));
					sitemap(pw);
					pw.close();
				} catch( Exception e ) {
					log.error( "Shouldnt happen", e );
				}
				// TODO Auto-generated method stub
			}
		};
		
		Queues.queue( r, "now");
		return pis;
	}
	
	public String getContentType() {
		return "Content-Type:text/plain; charset=UTF-8";
	}
}


/**
 *  Here are all the fields in the EUScreen indexing.
 *  The _s and _ss variants are omitted (for string and single_string)
 *  Those are for faceting and sorting.

/eus:AdministrativeMetadata/eus:filename_tg
/eus:AdministrativeMetadata/eus:firstBroadcastChannel_tg
/eus:AdministrativeMetadata/eus:identifier_tg
/eus:AdministrativeMetadata/eus:iprRestrictions_tg
/eus:AdministrativeMetadata/eus:originalIdentifier_tg
/eus:AdministrativeMetadata/eus:provider/@providerCode_tg
/eus:AdministrativeMetadata/eus:provider_tg
/eus:AdministrativeMetadata/eus:publisherbroadcaster_tg
/eus:AdministrativeMetadata/eus:rightsTermsAndConditions_tg
/eus:AdministrativeMetadata/eus:uri_tg
/eus:ContentDescriptiveMetadata/eus:LocalKeyword_tg
/eus:ContentDescriptiveMetadata/eus:ThesaurusTerm/@thestermCode_tg
/eus:ContentDescriptiveMetadata/eus:ThesaurusTerm_tg
/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInEnglish/eus:seriesTitle_tg
/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInEnglish/eus:title_tg
/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInOriginalLanguage/eus:seriesTitle_tg
/eus:ContentDescriptiveMetadata/eus:TitleSet/eus:TitleSetInOriginalLanguage/eus:title_tg
/eus:ContentDescriptiveMetadata/eus:clipTitle_tg
/eus:ContentDescriptiveMetadata/eus:extendedDescription_tg
/eus:ContentDescriptiveMetadata/eus:genre/@genreCode_tg
/eus:ContentDescriptiveMetadata/eus:genre_tg
/eus:ContentDescriptiveMetadata/eus:originallanguage/@olcode_tg
/eus:ContentDescriptiveMetadata/eus:originallanguage_tg
/eus:ContentDescriptiveMetadata/eus:summaryInEnglish_tg
/eus:ContentDescriptiveMetadata/eus:summary_tg
/eus:ContentDescriptiveMetadata/eus:topic/@topicCode_tg
/eus:ContentDescriptiveMetadata/eus:topic_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:languageUsed/@luCode_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:languageUsed_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:metadatalanguage/@mlCode_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:metadatalanguage_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:subtitleLanguage/@slCode_tg
/eus:ObjectDescriptiveMetadata/eus:LanguageInformation/eus:subtitleLanguage_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:SpatialInformation/eus:CountryofProduction/@copCode_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:SpatialInformation/eus:CountryofProduction_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:SpatialInformation/eus:GeographicalCoverage/@geocovCode_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:SpatialInformation/eus:GeographicalCoverage_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:broadcastDate_tg
/eus:ObjectDescriptiveMetadata/eus:SpatioTemporalInformation/eus:TemporalInformation/eus:productionYear_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:aspectRatio/@ratioCode_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:aspectRatio_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:itemColor/@colorCode_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:itemColor_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:itemDuration_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:itemSound/@soundCode_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:itemSound_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:materialType/@materialCode_tg
/eus:ObjectDescriptiveMetadata/eus:TechnicalInformation/eus:materialType_tg
/eus:ObjectDescriptiveMetadata/eus:contributor_tg
/eus:ObjectDescriptiveMetadata/eus:information_tg
/eus:ObjectDescriptiveMetadata/eus:itemType/@itemCode_tg
/eus:ObjectDescriptiveMetadata/eus:itemType_tg
/eus:ObjectDescriptiveMetadata/eus:relation/@relationCode_tg
/eus:ObjectDescriptiveMetadata/eus:relation/@relationtype_tg
/eus:ObjectDescriptiveMetadata/eus:relation_tg

all
item_id
production
xml_object_id

**/
