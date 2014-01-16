package gr.ntua.ivml.mint.util;

import java.text.SimpleDateFormat;

import gr.ntua.ivml.mint.db.Meta;
import gr.ntua.ivml.mint.persistent.Item;

import org.apache.solr.common.SolrInputDocument;

public class EuscreenIndexHelper {
	public static final String broadcastField = "_ObjectDescriptiveMetadata_SpatioTemporalInformation/" + 
									"eus:TemporalInformation_broadcastDate_sd";
	public static final String productionField = "_ObjectDescriptiveMetadata_SpatioTemporalInformation/"+ 
									"eus:TemporalInformation_productionYear_s";

	public static final String[] tagFields = new String[] {
		"_ContentDescriptiveMetadata_LocalKeyword_s",
		"_ContentDescriptiveMetadata_ThesaurusTerm_s",
		"_ContentDescriptiveMetadata_TitleSet_TitleSetInEnglish_seriesTitle_s",
		"_ContentDescriptiveMetadata_TitleSet_TitleSetInEnglish_title_s",
		"_ContentDescriptiveMetadata_TitleSet_TitleSetInOriginalLanguage_seriesTitle_s",
		"_ContentDescriptiveMetadata_TitleSet_TitleSetInOriginalLanguage_title_s",
		"_ContentDescriptiveMetadata_clipTitle_s",
		"_ContentDescriptiveMetadata_extendedDescription_s",
		"_ContentDescriptiveMetadata_genre_s",
		"_ContentDescriptiveMetadata_summaryInEnglish_s",
		"_ContentDescriptiveMetadata_summary_s",
		"_ContentDescriptiveMetadata_topic_s",
		"_ObjectDescriptiveMetadata_SpatioTemporalInformation_SpatialInformation_GeographicalCoverage_s"
	};
	
	public static void addTags( SolrInputDocument doc ) {
		for( String fieldname: tagFields ) {
			if( doc.getFieldValues( fieldname ) != null ) { 
				for( Object val: doc.getFieldValues( fieldname )) {
					doc.addField( "tags", val );
				}
			}
		}
	}

	public static void modifyDocument(Item item, SolrInputDocument doc) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		
		doc.addField("annotationDate_sd", sdf.format(item.getLastModified()) );

		// need a merged Date from broadcast and production 
		// so that if broadcast is missing, the other one is used for 
		// sorting.
		String mergeDate = null;
		String pDecade = "";
		String bDecade = "";
		Object sf = doc.getFieldValue( broadcastField );
		if( sf != null ) {
			mergeDate = sf.toString();
			bDecade = mergeDate.substring(0, 3) +"0s";
			doc.addField("decade_s", bDecade );
		} else {
			sf = doc.getFieldValue( productionField );
			if( sf != null ) {
				mergeDate = sf.toString() + "0101";
			}
		}
		sf = doc.getFieldValue( productionField );
		if( sf != null ) {
			// create a decade field for faceting ..
			if( sf.toString().length() == 4 ) {
				String decade = sf.toString().substring(0, 3) +"0s";
				pDecade = decade;
				doc.addField("productionYearDecade_s", decade );
			} 
		}

		if( !pDecade.equals( bDecade ) && !StringUtils.empty(pDecade)) doc.addField("decade_s", pDecade );

		if( mergeDate != null )
			doc.addField("mergedSortingDate_sd", mergeDate );
		
		// get EuScreenid
		String euScreenId = null;
		Object field = doc.getFieldValue( "native_id_s" );
		if( field != null ) {
			euScreenId = field.toString();
			// find production status from ItemMeta
			String res = Meta.get( "ItemOnPortal["+euScreenId+"]");
			if(! StringUtils.empty( res )) {
				if("true".equals( res )) 
					doc.addField( "production", "yes" );
				else
					doc.addField("production", "no");
			} else {
				// here is the default treatment
				doc.addField( "production", "no" );
				Meta.put( "ItemOnPortal["+euScreenId+"]", "false" );
			}
		}
		addTags( doc );
	}
}
