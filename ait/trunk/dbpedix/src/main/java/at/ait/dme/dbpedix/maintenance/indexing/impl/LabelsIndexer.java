package at.ait.dme.dbpedix.maintenance.indexing.impl;

import org.semanticweb.yars.nx.Node;

import at.ait.dme.dbpedix.maintenance.indexing.BaseIndexer;
import at.ait.dme.dbpedix.maintenance.indexing.DBpediaDocumentBuilder;

/**
 * An implementation of the {@link BaseIndexer} for indexing DBpedia
 * label dumps.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class LabelsIndexer extends BaseIndexer {

	@Override
	protected boolean updateResource(DBpediaDocumentBuilder builder, Node predicate, Node label) {
		builder.setLabel(label.toString(), getLang(label));
		return true;
	}

}
