package at.ait.dme.dbpedix.maintenance.indexing;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import junit.framework.Assert;

import org.junit.BeforeClass;
import org.junit.Test;
import org.semanticweb.yars.nx.parser.ParseException;

import at.ait.dme.dbpedix.DBpediaIndexReader;
import at.ait.dme.dbpedix.DBpediaResource;
import at.ait.dme.dbpedix.LatLon;
import at.ait.dme.dbpedix.config.Config;
import at.ait.dme.dbpedix.maintenance.indexing.impl.GeoCoordinatesIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.impl.LabelsIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.impl.ShortAbstractsIndexer;

public class IndexingTest {
	
	private static final String DBPEDIA_INDEX_DIR = "test-index";
	
	@BeforeClass
	public static void config() {
		deleteIndex();
		
		Properties testProperties = new Properties();
		testProperties.setProperty(Config.DBPEDIA_DOWNLOAD_BASEURL, "http://downloads.dbpedia.org/3.6/");
		testProperties.setProperty(Config.DBPEDIA_DUMP_DIR, "src/test/resources" );
		testProperties.setProperty(Config.DBPEDIA_INDEX_DIR, DBPEDIA_INDEX_DIR);
		Config.override(testProperties);
	}
	
	@Test
	public void testIndexing() throws FileNotFoundException, IOException, URISyntaxException, ParseException {
		System.out.println("Initializing test index.");
		File dumpDir = new File(Config.getInstance().getDumpDirectory());
		
		LabelsIndexer labelsIndexer = new LabelsIndexer();
		labelsIndexer.index(new File(dumpDir, "sample_labels_en.nt"));
		labelsIndexer.index(new File(dumpDir, "sample_labels_de.nt"));
		labelsIndexer.close();
		
		GeoCoordinatesIndexer geocoordsIndexer = new GeoCoordinatesIndexer();
		geocoordsIndexer.index(new File(dumpDir, "sample_geo_coordinates.nt"));
		geocoordsIndexer.close();		

		ShortAbstractsIndexer shortAbstractsIndexer = new ShortAbstractsIndexer();
		shortAbstractsIndexer.index(new File(dumpDir, "sample_short_abstracts_en.nt"));
		shortAbstractsIndexer.close();	
		System.out.println("Done.");
		
		File indexDir = new File(DBPEDIA_INDEX_DIR);
		Assert.assertTrue(indexDir.exists());
		Assert.assertTrue(new File(indexDir, "segments.gen").exists());
	}
	
	@Test
	public void testQuerying() throws IOException, org.apache.lucene.queryParser.ParseException {
		System.out.println("Running a few test queries.");
		DBpediaIndexReader index = DBpediaIndexReader.getInstance();
		
		List<DBpediaResource> results = index.findResources("atlantic ocean", "en", 2);
		Assert.assertEquals(1, results.size());
		DBpediaResource result = results.get(0);
		Assert.assertEquals("Atlantic Ocean", result.getLabel("en"));		
		Assert.assertEquals("Atlantischer Ozean", result.getLabel("de"));
		Assert.assertTrue(result.getShortAbstract("en").startsWith("The Atlantic Ocean is the second-largest of the world's oceanic divisions"));
		Assert.assertEquals(new LatLon(0.0, -30.0), result.getGeoCoordinates());

		results = index.findResources("Aristoteles", "de", 2);
		Assert.assertEquals(1, results.size());
		result = results.get(0);
		Assert.assertEquals("Aristotle", result.getLabel("en"));		
		Assert.assertEquals("Aristoteles", result.getLabel("de"));
		Assert.assertNull(result.getShortAbstract("en"));
		Assert.assertNull(result.getGeoCoordinates());
		
		index.close();
		System.out.println("Done.");
	}
	
	private static void deleteIndex() {
		File indexDir = new File(DBPEDIA_INDEX_DIR);
		if (indexDir.exists()) {
			for (File f : indexDir.listFiles()) {
				f.delete();
			}
			indexDir.delete();
		}
	}

}
