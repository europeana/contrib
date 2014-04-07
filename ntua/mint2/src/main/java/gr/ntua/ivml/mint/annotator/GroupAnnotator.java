package gr.ntua.ivml.mint.annotator;

import gr.ntua.ivml.mint.concurrent.Queues;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.LockManager;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.persistent.DataUpload;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Lock;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.Triple;
import gr.ntua.ivml.mint.util.Tuple;
import net.minidev.json.JSONArray;
import nu.xom.Attribute;
import nu.xom.Builder;
import nu.xom.Comment;
import nu.xom.DocType;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Nodes;
import nu.xom.ParsingException;
import nu.xom.ProcessingInstruction;
import nu.xom.Text;
import nu.xom.ValidityException;
import nu.xom.XPathContext;
 

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.TestCase;

import org.apache.log4j.Logger;

public class GroupAnnotator {
	
	protected final Logger log = Logger.getLogger(getClass());
	private Dataset dataset;
	private User user;
		
	public GroupAnnotator(long datasetId, User user) {
		log.debug("Initialize GroupAnnotator with datasetId: " + datasetId);
		this.dataset = DB.getDatasetDAO().findById(datasetId, false);
		if (dataset == null)
			throw new IllegalArgumentException(datasetId + " is not a valid dataset id");	
		this.user = user;
	}
	
	public void modifyElementsInItemsList(JSONArray itemsXpathActions) {
		//LockManager lm = DB.getLockManager();
    	GroupAnnotatorConc gac = new GroupAnnotatorConc(dataset, user, itemsXpathActions);
    	//Queue as ConditionedRunnable to check the locks
    	Queues.queue(gac, "db");
    }
}
