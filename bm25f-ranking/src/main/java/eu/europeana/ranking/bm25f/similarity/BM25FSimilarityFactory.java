package eu.europeana.ranking.bm25f.similarity;



import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.common.util.NamedList;
import org.apache.solr.schema.SimilarityFactory;

import eu.europeana.ranking.bm25f.enums.SolrFields;
import eu.europeana.ranking.bm25f.params.BM25FParameters;

public class BM25FSimilarityFactory extends SimilarityFactory {

	public BM25FSimilarityFactory() {

	}

	private static BM25FSimilarity similarity;
	
	@SuppressWarnings("rawtypes")
	private static BM25FSimilarity getInstance(BM25FParameters params,NamedList pars){
		if (similarity==null){
			SolrFields fields = SolrFields.getInstance();
			fields.createSimilarityFields(pars);
			params.setFields(fields.getFieldNames());
			params.setbParams(fields.getBParams());
			params.setBoosts(fields.getBoosts());
			similarity = new BM25FSimilarity(params);
		}
		return similarity;
	}
	
	@Override

	public Similarity getSimilarity() {
		
		BM25FParameters params = BM25FParameters.getInstance();
		if (getParams() != null) {
			return getInstance(params,getParams().toNamedList());
		}
		return null;
	}
	
	
}
