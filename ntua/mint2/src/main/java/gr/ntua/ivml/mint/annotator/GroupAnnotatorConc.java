package gr.ntua.ivml.mint.annotator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import gr.ntua.ivml.mint.Custom;
import gr.ntua.ivml.mint.concurrent.Solarizer;
import gr.ntua.ivml.mint.concurrent.Queues.ConditionedRunnable;
import gr.ntua.ivml.mint.db.DB;
import gr.ntua.ivml.mint.db.LockManager;
import gr.ntua.ivml.mint.persistent.Dataset;
import gr.ntua.ivml.mint.persistent.Item;
import gr.ntua.ivml.mint.persistent.Lock;
import gr.ntua.ivml.mint.persistent.User;
import gr.ntua.ivml.mint.persistent.XmlSchema;
import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import org.apache.log4j.Logger;


public class GroupAnnotatorConc implements Runnable, ConditionedRunnable {

	protected final Logger log = Logger.getLogger(getClass());
	private JSONArray itemsXpathActions;
	private JSONObject schemaJSON;
	private Dataset dataset;
	private List<Lock> aquiredLocks = Collections.emptyList();
	private User user;
	//private HashSet<String> supportedEditFunctions = new HashSet<String>();
		
	public GroupAnnotatorConc(Dataset dataset, User user, JSONArray itemsXpathActions) {
		this.dataset = dataset;
		initSchema();
		this.itemsXpathActions = itemsXpathActions;
		this.user = user;
	}
	
	public void initSchema() {
		//this.dataset = dataset;
		XmlSchema sv = dataset.getSchema();
		if(dataset != null &&  sv!= null) {
			//this.schema = dataset.getSchema();
			this.schemaJSON = sv.getTemplate().asJSONObject();
		}
		else {
			throw new IllegalArgumentException("Invalid dataset or/and schema");
		}
	}
	
	
	
	public void run() {
		//refresh
		DB.getSession().beginTransaction();
		dataset = DB.getDatasetDAO().getById(dataset.getDbID(), false);
		dataset.setItemizerStatus(Dataset.ITEMS_RUNNING);
		long time = System.currentTimeMillis();
		dataset.logEvent("Group annotation started");
		DB.commit();
		int itemsChanged = 0;
		HashSet<Long> allItemIds = new HashSet<Long>();
		try {
			log.info("Start group annotation");
		    for (Object itemsXpathAction: itemsXpathActions) {
		    	List<Long> itemIds = (List<Long>) ((JSONObject) itemsXpathAction).get("itemIds");
		    	if (!itemIds.isEmpty()) {
		    		JSONArray actionList = (JSONArray) ((JSONObject) itemsXpathAction).get("actionList");
		    		if (!actionList.isEmpty()) {
			    		String actions = "";
			    		for (Object a: actionList) {
			    			JSONObject action = (JSONObject) a;
			    			if (!actions.isEmpty())
			    				actions += ", ";
			    			actions += (String) action.get("type");
			    		}
			    		dataset.logEvent("Applying edit actions", 
			    				"Actions: " +  actions + "\n" + 
			    				"Number of items: " + itemIds.size());
			    		ModifyItem mi = new ModifyItem(actionList, schemaJSON);
			    		DB.getItemDAO().applyForIdList(itemIds, mi);
			    		itemsChanged += mi.getItemsChanged();
			    		allItemIds.addAll(itemIds);
		    		}
		    	}
			} 
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		finally {
			dataset.setItemizerStatus(Dataset.ITEMS_OK);
			DB.commit();
			time = System.currentTimeMillis() - time;
			log.info("Group annotation took " + time +" msec");
			dataset.logEvent("Group annotation finished", itemsChanged + " changes have been applied");
			if (allItemIds.size() > 0 && dataset.getItemizerStatus().equals(Dataset.ITEMS_OK)) {
				if( Solarizer.isEnabled()) {
					if( Custom.allowSolarize(dataset)) {
						if (allItemIds.size() < 1000) {
							for (Long itemId: allItemIds) {
								Item item = DB.getItemDAO().findById(itemId, false);
								Solarizer sl = new Solarizer(item);
								sl.run();
							}
						}
						else {
							Solarizer sl = new Solarizer(dataset);
							sl.run();
						}
					}
				}
			}
			releaseLock();
			DB.commit();
			DB.closeStatelessSession();
			DB.closeSession();
		}
    	log.info("Group annotation finished");
	}
	
	public void releaseLock() {
		LockManager lm = DB.getLockManager();
		for( Lock l: aquiredLocks)
			lm.releaseLock(l);
	}
	
	public void setAcquiredLocks(List<Lock> locks ) {
		this.aquiredLocks = locks;
	}

	@Override
	public boolean isRunnable() {
		LockManager lm = DB.getLockManager();
		if ((lm.isLocked(dataset) != null)) 
			return false;
		else {
			Lock l = lm.directLock(user, "groupAnnotation", dataset);
			setAcquiredLocks(Arrays.asList(l));
			return true;
		}
	}

	@Override
	public boolean isUnRunnable() {		
		return false;
	}

	
}
