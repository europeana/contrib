package gr.ntua.ivml.awareness.search;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.util.ApplyI;
import gr.ntua.ivml.awareness.util.MongoDB;
import gr.ntua.ivml.awareness.util.Tuple;

import java.util.Collection;

import org.apache.log4j.Logger;
import org.apache.solr.common.SolrInputDocument;

/**
 * Modify the general json to solr translation for the specifics of this project
 * So that the general part can be reused better.
 *  
 * @author Arne Stabenau 
 *
 */
public class SolrModifier implements ApplyI<Tuple<SolrInputDocument,Object>> {

	public static final Logger log = Logger.getLogger( SolrModifier.class );
	public static final String[] digitalStorySearchFields = new String[] {
		"_title_s",
		"_description_s",
		"_storyObjects_title_s",
		"_storyObjects_description_s"
	};
	@Override
	public void apply(Tuple<SolrInputDocument,Object> input) throws Exception {
		SolrInputDocument sid = input.first();
		Object sourceObject = input.second();
		Object valId = sid.getFieldValue("__id_s" );
		if( valId != null )
			sid.addField( "id", valId.toString());
		else {
			log.info( "Invalid document, no id field " + sid );
			throw new Exception( "Invalid document");
		}
		
		if( sourceObject instanceof DigitalStory ) 
			createDigitalStory(sid, (DigitalStory) sourceObject );
		if( sourceObject instanceof StoryImage ) 
			createStoryImage( sid, (StoryImage) sourceObject );
	}
	
	private void createDigitalStory( SolrInputDocument sid, DigitalStory ds ) {
		for( String key: digitalStorySearchFields ) {
			Collection<Object> vals = sid.getFieldValues(key);
			if( vals != null )
				for( Object val:vals ) {
					sid.addField( "digitalStory_txt", val );
				}
		}
		sid.addField("_dateCreated_dt", ds.getDateCreated());
		sid.addField( "ui_json_s", MongoDB.getDigitalStoryDAO().toUIJson(ds).toString());
	}
	
	private void createStoryImage( SolrInputDocument sid, StoryImage si ) {
		sid.addField( "ui_json_s", si.getBsonForSolr().toString());
		sid.addField("_dateCreated_dt", si.getDateCreated());
	}

}
