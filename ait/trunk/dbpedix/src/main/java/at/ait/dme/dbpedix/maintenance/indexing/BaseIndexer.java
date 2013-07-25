package at.ait.dme.dbpedix.maintenance.indexing;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.log4j.Logger;
import org.semanticweb.yars.nx.Node;
import org.semanticweb.yars.nx.parser.NxParser;
import org.semanticweb.yars.nx.parser.ParseException;

/**
 * An abstract base class for indexing DBpedia files of
 * different types. Extending this class should help you
 * to handle new types of data without the need for a
 * lot of boilerplate code. See the .impl package for
 * examples!
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 *
 */
public abstract class BaseIndexer {
	
	private DBpediaIndexWriter index = null;
	
	protected Logger log = Logger.getLogger(getClass());
	
	/**
	 * The main parse/index loop. This method parses an N-Triples file
	 * line by line (using NxParser). If the triple contains data about
	 * a resource already in the index, this resource is retrieved from
	 * the index. Otherwise, a new resource is created. The resource is
	 * handed to the .updateResource() method. Different Indexer implementations
	 * can than update the resource in different ways (add labels, coordinates,
	 * whatever). After that, the BaseIndexer handles the actual Lucene 
	 * update. 
	 * @param file the N-Triples dump file
	 * @throws ParseException if anything goes wrong parsing the dump file
	 * @throws IOException if anything is wrong with the dump file or Lucene index
	 * @throws URISyntaxException if anything is wrong with the URIs in the dump file
	 */
	public void index(File file) throws ParseException, IOException, URISyntaxException {
		log.info("Starting indexing: " + file.getAbsolutePath());
		
		int additions = 0;
		int updates = 0;
		NxParser nxParser = new NxParser(new FileInputStream(file));
		while (nxParser.hasNext()) {
			Node[] nodes = nxParser.next();
			if (nodes.length == 3) {
				URI uri = new URI(nodes[0].toString());
				if (getIndex().contains(uri)) {
					updates++;
					updateIndex(uri, nodes[1], nodes[2]);
				} else {
					additions++;
					addToIndex(uri, nodes[1], nodes[2]);
				}
   			}
		}
		
		index.commit();
		log.info("Indexing complete");
		log.info(additions + " resources added to index");
		log.info(updates + " resources updated");
	}
	
	protected DBpediaIndexWriter getIndex() throws IOException {
		if (index == null)
			index = new DBpediaIndexWriter();
		
		return index;
	}
	
	private void addToIndex(URI uri, Node predicate, Node object) throws IOException {		
		DBpediaDocumentBuilder builder = new DBpediaDocumentBuilder(uri);
		if (updateResource(builder, predicate, object))
			getIndex().addDBpediaDocument(builder.getDocument());
	}

	private void updateIndex(URI uri, Node predicate, Node object) throws IOException {
		DBpediaIndexWriter index = getIndex();
		DBpediaDocumentBuilder builder = new DBpediaDocumentBuilder(index.getDBpediaDocument(uri));
		if (updateResource(builder, predicate, object))
			index.updateDBpediaDocument(uri, builder.getDocument());
	}
	
	/**
	 * This method is where the actual update to the DBpedia resource happens. Classes 
	 * extending {@link BaseIndexer} only need to implement their bit of custom data handling
	 * in this method, the rest is handled by the {@link BaseIndexer}. Note: this method has a 
	 * boolean return value. The {@link BaseIndexer} will only modify the Lucene index, if the 
	 * method returns <code>true</code>. Implementations can use this to improve indexing 
	 * performance in case no modifications were made to the resource! 
	 * @param resource the DBpedia resource
	 * @param predicate the predicate of the triple retrieved from the dump file
	 * @param object the object of the triple retrieved from the dump file
	 * @return a flag indicating whether the index should be update, or whether the update should be skipped
	 */
	protected abstract boolean updateResource(DBpediaDocumentBuilder resource, Node predicate, Node object);
	
	protected String getLang(Node plainLiteral) {
		String n3 = plainLiteral.toN3();
		return n3.substring(n3.lastIndexOf('@') + 1).toLowerCase();
	}
	
	/**
	 * Important: ALWAYS close the indexer after using it. Otherwise the index updates might not
	 * get written to the file system!
	 * @throws IOException if anything is wrong with the Lucene index 
	 */
	public void close() throws IOException {
		index.close();
	}

}
