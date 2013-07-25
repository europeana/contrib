package gr.ntua.ivml.awareness.search;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.util.ApplyI;
import gr.ntua.ivml.awareness.util.Config;
import gr.ntua.ivml.awareness.util.MongoDB;
import gr.ntua.ivml.awareness.util.Tuple;

import java.util.List;

import org.apache.log4j.Logger;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.bson.BSONObject;

public class SolrHelper {
	
	private static Runnable reIndex = null;
	
	public static SolrServer solrServer = null;
	public static ApplyI<Tuple<SolrInputDocument, Object>> docModifier = null;
	
	private static final Logger log = Logger.getLogger( SolrHelper.class );

	public static void solrize(BSONObject obj, Object source ) {
		// write the DBDObject into solr
		if( obj == null ) return;
		
		SolrInputDocument sid = new SolrInputDocument();
		try {
			appendBSONObject( sid, "", obj );
			if( docModifier != null ) {
				docModifier.apply( new Tuple<SolrInputDocument, Object>(sid, source));
			}
			getSolrServer().add( sid );
			getSolrServer().commit();
		} catch( Exception e ) {
			log.error( "Solrizing problem", e );
		}
	}
	
	private static void appendBSONObject( SolrInputDocument sid, String fieldRoot, Object obj ) {
		if( obj instanceof List ) {
			List<Object> lo = (List) obj;
			for( Object elem: lo ) {
				appendBSONObject( sid, fieldRoot, elem );
			}
		} else if( obj instanceof BSONObject ) {
			for( String key: ((BSONObject) obj).keySet()) {
				appendBSONObject( sid, fieldRoot+"_"+key, ((BSONObject) obj).get(key));
			}
		} else {
			sid.addField( fieldRoot+"_tg", obj.toString());
			sid.addField( fieldRoot+"_s", obj.toString());
		}
	}

	
	public static void deleteById( String id ) {
		try {
			getSolrServer().deleteById(id);
			getSolrServer().commit();
			log.debug( "Solr deleted " + id);
		} catch( Exception e ) {
			log.error( "Solr delete failed!", e );
		}
	}
	
	public static SolrServer getSolrServer() {
		if( solrServer == null ) {
			solrServer = new HttpSolrServer(Config.get("solr.url"));
		}
		return solrServer;
	}

	/**
	 * Runs a thread which rebuilds the search index. If one is already running, does nothing!
	 */
	synchronized public static void reIndex() {
		if(reIndex == null ) {
		 reIndex = new Runnable() {
			@Override
			public void run() {
				try {
					clearIndex();
					// iterate over all StoryObjects and solrize them
					// iterate over all DigitalStories and solrize them
					ApplyI<DigitalStory> solrizer = new ApplyI<DigitalStory>() {
						@Override
						public void apply(DigitalStory ds) throws Exception {
							BSONObject bs = ds.getBsonForSolr();
							log.debug( "Solarize: " + bs.toString());
							SolrHelper.solrize(bs, ds);
						}			
					};
					MongoDB.getDigitalStoryDAO().forAll(solrizer);
					
					ApplyI<StoryImage> solrizeStoryImage = new ApplyI<StoryImage>() {
						@Override
						public void apply(StoryImage si) throws Exception {
							BSONObject bs = si.getBsonForSolr();
							SolrHelper.solrize(bs, si);
						}			
					};
					MongoDB.getStoryImageDAO().forAll(solrizeStoryImage);

				} catch( Exception e ) {
					log.error( "ReIndex in queue failed.", e );
				} finally { reIndex = null; }
			}
		};
		(new Thread( reIndex )).start();
		} else {
			log.info( "ReIndex already running");
		}
	}
	

	public static void clearIndex() {
		try {
			getSolrServer().deleteByQuery( "*:*" );
			getSolrServer().commit();
		} catch( Exception e) {
			log.error( "Clear Index failed", e );
		}
	}
	
	public static void setDocumentModifier( ApplyI<Tuple<SolrInputDocument, Object>> docModifier ) {
		SolrHelper.docModifier = docModifier;
	}
}
