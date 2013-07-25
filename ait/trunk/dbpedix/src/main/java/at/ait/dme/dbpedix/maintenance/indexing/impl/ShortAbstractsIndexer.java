package at.ait.dme.dbpedix.maintenance.indexing.impl;

import org.semanticweb.yars.nx.Node;

import at.ait.dme.dbpedix.maintenance.indexing.BaseIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.DBpediaDocumentBuilder;

/**
 * An implementation of the {@link BaseIndexer} for indexing DBpedia
 * short abstract dumps.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class ShortAbstractsIndexer extends BaseIndexer {

	@Override
	protected boolean updateResource(DBpediaDocumentBuilder builder, Node predicate, Node shortAbstract) {
		builder.setShortAbstract(shortAbstract.toString(), getLang(shortAbstract));
		return true;
	}

}
