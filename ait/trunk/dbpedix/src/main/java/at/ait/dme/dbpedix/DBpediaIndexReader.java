package at.ait.dme.dbpedix;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import at.ait.dme.dbpedix.config.Config;

/**
 * The DBpediaIndexReader provides typed read-only access to 
 * the underlying Lucene index.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DBpediaIndexReader {
	
	private static DBpediaIndexReader instance = null;
		
	private IndexReader reader = null;
	
	private IndexSearcher searcher = null;
	
	/**
	 * Returns the singleton instance of the DBpediaIndexReader.
	 * @return the singleton index
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public static DBpediaIndexReader getInstance() throws IOException {
		if (instance == null)
			instance = new DBpediaIndexReader();
		
		return instance;
	}
	
	private DBpediaIndexReader() throws IOException {		
		Directory dir = 
			FSDirectory.open(new File(Config.getInstance().getIndexDirectory()));
		
		reader = IndexReader.open(dir, true);
		searcher = new IndexSearcher(reader);
	}
	
	/**
	 * Returns the {@link DBpediaResource} with the specified URI
	 * or <code>null</code> if it is not in the index.
	 * @param uri the URI
	 * @return the resource
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public DBpediaResource getResource(URI uri) throws IOException {
		PhraseQuery q = new PhraseQuery();
		q.add(new Term(IndexFields.URI, uri.toString()));
		TopDocs topDocs = searcher.search(q, 1);
		if (topDocs.totalHits < 1)
			return null;
		
		ScoreDoc[] docs = topDocs.scoreDocs;
		return new DBpediaResource(searcher.doc(docs[0].doc));
	}
	
	/**
	 * Performs a search on the labels in the index. Searches are
	 * language-specific. I.e. if 'en' is used as the language code,
	 * only the English-language labels will be searched.
	 * @param query the search query
	 * @param lang the language code
	 * @param limit the maximum number of results to return
	 * @return the list of {@link DBpediaResource}s
	 * @throws IOException if anything is wrong with the Lucene index
	 * @throws ParseException if the query could not be handled by the query parser
	 */
	public List<DBpediaResource> findResources(String query, String lang, int limit) throws IOException, ParseException {
		QueryParser queryParser = new QueryParser(Version.LUCENE_33 , IndexFields.LABEL + lang.toLowerCase(), 
				new StandardAnalyzer(Version.LUCENE_33));
		Query q = queryParser.parse(query);
		
		TopDocs topDocs = searcher.search(q, limit);
		List<DBpediaResource> resources = new ArrayList<DBpediaResource>();
		for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
			resources.add(new DBpediaResource(searcher.doc(scoreDoc.doc)));
		}
		return resources;
	}
	
	/**
	 * Be kind. Always close the DBpediaIndexReader after use.
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public void close() throws IOException {
		reader.close();
		searcher.close();
	}
	
}
