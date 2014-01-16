package eu.europeana.ranking.bm25f.similarity;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.schema.SimilarityFactory;

/**
 * A factory generating the BM25F similarity function.
 * 
 * @author Diego Ceccarelli <diego.ceccarelli@isti.cnr.it>
 * 
 *         Created on Jan 14, 2014
 */
public class BM25FSimilarityFactory extends SimilarityFactory {

	public BM25FSimilarityFactory() {

	}

	@Override
	public Similarity getSimilarity() {
		return new BM25FSimilarity();
	}

}
