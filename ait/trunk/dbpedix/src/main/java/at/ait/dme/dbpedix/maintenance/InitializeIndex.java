package at.ait.dme.dbpedix.maintenance;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

import org.semanticweb.yars.nx.parser.ParseException;

import at.ait.dme.dbpedix.config.Config;
import at.ait.dme.dbpedix.maintenance.indexing.impl.GeoCoordinatesIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.impl.LabelsIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.impl.ShortAbstractsIndexer;

/**
 * A sample executable class that initializes the index with a set of DBpedia
 * dump files. Use this as a guide to design your own index building scripts!
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class InitializeIndex {
	
	public static void main(String[] args) throws ParseException, IOException, URISyntaxException {
		System.out.println("Initializing the index.");
		File dumpDir = new File(Config.getInstance().getDumpDirectory());
		
		LabelsIndexer labelsIndexer = new LabelsIndexer();
		labelsIndexer.index(new File(dumpDir, "labels_de.nt.txt"));
		labelsIndexer.index(new File(dumpDir, "labels_en.nt.txt"));
		System.out.println("Optimizing...");
		labelsIndexer.close();
		
		GeoCoordinatesIndexer geocoordsIndexer = new GeoCoordinatesIndexer();
		geocoordsIndexer.index(new File(dumpDir, "geo_coordinates_en.nt.txt"));
		System.out.println("Optimizing...");
		geocoordsIndexer.close();		

		ShortAbstractsIndexer shortAbstractsIndexer = new ShortAbstractsIndexer();
		shortAbstractsIndexer.index(new File(dumpDir, "short_abstracts_en.nt.txt"));
		shortAbstractsIndexer.close();	
		System.out.println("Optimizing...");
		System.out.println("Initialization complete.");
	}

}
