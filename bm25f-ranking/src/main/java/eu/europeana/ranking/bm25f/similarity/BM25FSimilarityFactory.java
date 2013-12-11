package eu.europeana.ranking.bm25f.similarity;

import org.apache.lucene.search.similarities.Similarity;
import org.apache.solr.schema.SimilarityFactory;

public class BM25FSimilarityFactory extends SimilarityFactory {

	public BM25FSimilarityFactory() {

	}

	@Override
	public Similarity getSimilarity() {
		return new BM25FSimilarity();
	}

}
