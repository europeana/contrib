package gr.ntua.ivml.awareness.db;

import java.util.ArrayList;
import java.util.Iterator;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.DigitalStoryComment;
import gr.ntua.ivml.awareness.util.MongoDB;

import org.bson.types.ObjectId;

import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.DBObject;

public class StoryCommentsDAO extends BasicDAO<DigitalStoryComment, ObjectId>{

	public StoryCommentsDAO(){
		super(DigitalStoryComment.class, MongoDB.getDS());
	}
	
    public ArrayList<DBObject> getStoryCommentsByUser(int from, int to, String userId){
    	Iterable<DigitalStoryComment> stories = this.createQuery().field("userId").equal(userId).order("-dateCreated").offset(from).limit(to-from).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	Iterator<DigitalStoryComment> stor = stories.iterator();
    	while(stor.hasNext()){
    		DigitalStoryComment tmp = stor.next();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		res.add(tmpObj);
    	}
    	return res;
    }
    
    public ArrayList<DBObject> getStoryComments(int from, int to, String storyId){
    	Iterable<DigitalStoryComment> stories = this.createQuery().field("storyId").equal(storyId).order("-dateCreated").offset(from).limit(to-from).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	Iterator<DigitalStoryComment> stor = stories.iterator();
    	while(stor.hasNext()){
    		DigitalStoryComment tmp = stor.next();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		res.add(tmpObj);
    	}
    	return res;
    }

    public long getUsersCommentsTotalNumber(String userId){return this.createQuery().field("userId").equal(userId).countAll();}
    public long getCommentsTotalNumber(String storyId){return this.createQuery().field("storyId").equal(storyId).countAll();}
}
