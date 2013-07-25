package at.ait.dme.dbpedix.maintenance.indexing;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import at.ait.dme.dbpedix.config.Config;

/**
 * The DBpediaIndexWriter provides typed read/write access to
 * the underlying Lucene index.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DBpediaIndexWriter {
	
	public static final String KEY_URI = "uri";
	public static final String KEY_LABEL = "label_";
	public static final String KEY_SHORT_ABSTRACT = "abstract_";
	
	private IndexWriter writer = null;
	
	private IndexReader reader = null;
	
	private IndexSearcher searcher = null;
	
	public DBpediaIndexWriter() throws IOException {		
		Directory dir = 
			FSDirectory.open(new File(Config.getInstance().getIndexDirectory()));
		
		IndexWriterConfig config = new IndexWriterConfig(
				Version.LUCENE_33, 
				new StandardAnalyzer(Version.LUCENE_33));
		config.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
		
		writer = new IndexWriter(dir, config);
		writer.commit();
		
		reader = IndexReader.open(dir, true);
		searcher = new IndexSearcher(reader);
	}
	
	/**
	 * Checks if the index contains the DBpedia resource with the
	 * specified URI.
	 * @param uri the URI
	 * @return <code>true</code> if the resource with the specified URI is in the index
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public boolean contains(URI uri) throws IOException {
		Term uriTerm = new Term(KEY_URI, uri.toString());
		return searcher.docFreq(uriTerm) > 0;
	}
	
	/**
	 * Returns the Lucene {@link Document} indexed under the specified URI.
	 * @param uri the URI
	 * @return the Document
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public Document getDBpediaDocument(URI uri) throws IOException {
		PhraseQuery q = new PhraseQuery();
		q.add(new Term(KEY_URI, uri.toString()));
		TopDocs topDocs = searcher.search(q, 1);
		if (topDocs.totalHits < 1)
			return null;
		
		ScoreDoc[] docs = topDocs.scoreDocs;
		return searcher.doc(docs[0].doc);
	}
	
	/**
	 * Adds a {@link Document} to the Lucene index 
	 * @param doc the Document
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public void addDBpediaDocument(Document doc) throws IOException {
		writer.addDocument(doc);
	}
	
	/**
	 * Updates the existing {@link Document}  with the specified URI.
	 * @param uri the URI
	 * @param doc the updated Document
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public void updateDBpediaDocument(URI uri, Document doc) throws IOException {
		Term uriTerm = new Term(KEY_URI, uri.toString());
		writer.updateDocument(uriTerm, doc);
	}
	
	/**
	 * Commits all updates to the Lucene index
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public void commit() throws IOException {
		writer.commit();
		reader = reader.reopen(true);
		searcher.close();
		searcher = new IndexSearcher(reader);
	}
	
	/**
	 * Important: ALWAYS close the DBpediaIndexWriter after using it. Otherwise the
	 * index updates might not get written to the file system!
	 * @throws IOException if anything is wrong with the Lucene index
	 */
	public void close() throws IOException {
		writer.optimize();
		writer.close();
		searcher.close();
	}
	
}
