package gr.ntua.ivml.awareness.db;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.persistent.StoryObjectPlaceHolder;
import gr.ntua.ivml.awareness.util.MongoDB;

import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.DBObject;

public class StoryObjectDAO extends BasicDAO<StoryObject, ObjectId>{

	public StoryObjectDAO(){
		super(StoryObject.class, MongoDB.getDS());
	}
	
	public ArrayList<String> getStoryObjects(int from, int to){
		List<Key<StoryObject>> stories = this.createQuery().offset(from).limit(to-from).asKeyList();
    	ArrayList<String> res = new ArrayList<String>();
    	
    	for(Key<StoryObject> key:stories){res.add(key.getId().toString());}
    	
    	return res;
	}
	
	public ArrayList<DBObject> getStoryObjects(int from, int to, String userId){
		Iterable<StoryObject> stories = this.createQuery().field("creator").equal(userId).order("-dateCreated").offset(from).limit(to-from).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	
    	Iterator<StoryObject> it = stories.iterator();
    	while(it.hasNext()){
    		StoryObject tmp = it.next();
    		tmp.getThumbnail();
    		tmp.getUrl();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		res.add(tmpObj);
    		//res.add(JSON.serialize(tmpObj));
    	}
    	
    	return res;
	}
	
			
	public boolean checkUnique(StoryObject so){
		Iterable<StoryObject> stories = this.createQuery().field("source").equal(so.getSource()).field("creator").equal(so.getCreator()).field("title").equal(so.getTitle()).fetch();
    	
    	Iterator<StoryObject> it = stories.iterator();
    	if(it.hasNext()){
    		return false;
    	}
    		return true;
	}
	
	
	public long getDigitalObjectsByUserTotalNumber(String userId){return this.createQuery().field("creator").equal(userId).countAll();}
	
	public ArrayList<StoryObject> getStoryObjectsByPlaceHolders(List<StoryObjectPlaceHolder> objs){
		ArrayList<StoryObject> res = new ArrayList<StoryObject>();
		
		ObjectId id = null;
		StoryObject obj = null;
		if(objs != null){
			for(StoryObjectPlaceHolder hold : objs){
				String i = hold.getStoryObjectID();
				if(i != null){
					id = new ObjectId(i);
					obj = this.get(id);
					res.add(obj);
				}
			}
		}
		return res;
	}
	
	public ArrayList<DBObject> getDBObjectsByPlaceHolders(ArrayList<StoryObjectPlaceHolder> objs){
		ArrayList<DBObject> res = new ArrayList<DBObject>();
		
		ObjectId id = null;
		StoryObject obj = null;
		if(objs != null){
			for(StoryObjectPlaceHolder hold : objs){
				String i = hold.getStoryObjectID();
				if(i != null){
					id = new ObjectId(i);
					obj = this.get(id);
					res.add(MongoDB.getMorphia().toDBObject(obj));
				}
			}
		}
		return res;
	}
	
	public ArrayList<StoryObject> getStoryObjectsByIDs(ArrayList<String> ids){
		ArrayList<StoryObject> objs = new ArrayList<StoryObject>();
		
		ObjectId id = null;
		StoryObject obj = null;
		if(ids != null){
			for(String currentId : ids){
				id = new ObjectId(currentId);
				obj = this.get(id);
				objs.add(obj);
			}
		}
		return objs;
	}
}
