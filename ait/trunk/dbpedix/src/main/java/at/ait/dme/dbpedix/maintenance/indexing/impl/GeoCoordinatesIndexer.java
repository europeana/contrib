package at.ait.dme.dbpedix.maintenance.indexing.impl;

import org.semanticweb.yars.nx.Node;

import at.ait.dme.dbpedix.maintenance.indexing.BaseIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.DBpediaDocumentBuilder;

/**
 * An implementation of the {@link BaseIndexer} for indexing DBpedia
 * geo-coordinate dumps.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class GeoCoordinatesIndexer extends BaseIndexer {

	@Override
	protected boolean updateResource(DBpediaDocumentBuilder builder, Node predicate, Node point) {
		if (predicate.toString().toLowerCase().endsWith("georss/point")) {
			String[] latLon = point.toString().split(" ");
			if (latLon.length == 2) {
				builder.setLat(Double.parseDouble(latLon[0]));
				builder.setLon(Double.parseDouble(latLon[1]));
				return true;
			}
		};
		return false;
	}

}
