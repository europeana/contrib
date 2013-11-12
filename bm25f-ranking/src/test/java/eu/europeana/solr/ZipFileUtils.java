package eu.europeana.solr;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.handler.loader.XMLLoader;

/**
 * Util class used to read zip files
 * 
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
@SuppressWarnings({ "unchecked", "restriction" })
public class ZipFileUtils {

	/**
	 * Read a list of solr xml documents from a zip file
	 * 
	 * @param path
	 *            The path of the zip file
	 * @return A list of SolrInputDocuments for indexing
	 */
	public static List<SolrInputDocument> readDocuments(String path) {
		try {

			ZipFile zFile = new ZipFile(path);

			Enumeration<ZipArchiveEntry> zipEntries = zFile.getEntries();
			List<SolrInputDocument> docs = new ArrayList<SolrInputDocument>();
			while (zipEntries.hasMoreElements()) {
				ZipArchiveEntry zEntry = zipEntries.nextElement();
				// Avoid directories if present
				if (zEntry.isDirectory()) {
					continue;
				}
				// Read the file in memory
				InputStream in = zFile.getInputStream(zEntry);
				XMLInputFactory factory = XMLInputFactory.newInstance();
				XMLLoader loader = new XMLLoader();
				// create an XML reader required to convert the document through
				// the XMLLoader of solr
				XMLStreamReader reader = factory.createXMLStreamReader(in);
				// get to the first START_ELEMENT (doc) as the XMLLoader does
				// not handle START_DOC event types
				reader.next();
				docs.add(loader.readDoc(reader));
			}
			zFile.close();
			return docs;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}
}
