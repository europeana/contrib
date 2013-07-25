package gr.ntua.ivml.awareness.db;

import gr.ntua.ivml.awareness.persistent.DigitalStory;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.persistent.StoryObjectPlaceHolder;
import gr.ntua.ivml.awareness.persistent.User;
import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.util.ApplyI;
import gr.ntua.ivml.awareness.util.MongoDB;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.apache.log4j.Logger;
import org.bson.BSONObject;
import org.bson.types.ObjectId;

import com.google.code.morphia.Key;
import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

public class DigitalStoryDAO extends BasicDAO<DigitalStory, ObjectId> {
	public static final Logger log = Logger.getLogger( DigitalStoryDAO.class );
	
	
        public DigitalStoryDAO(  ) {
        super(DigitalStory.class, MongoDB.getDS());
       
    }
    
    public ArrayList<String> getDigitalStories(int from, int to){
    	List<Key<DigitalStory>> stories = this.createQuery().offset(from).limit(to-from).asKeyList();
    	ArrayList<String> res = new ArrayList<String>();
    	
    	for(Key<DigitalStory> key:stories){res.add(key.getId().toString());}
    	
    	return res;
    }
    
    public ArrayList<DBObject> getDigitalStories(int from, int to, String userId){
    	Iterable<DigitalStory> stories = this.createQuery().field("creator").equal(userId).order("-dateCreated").offset(from).limit(to-from).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	Iterator<DigitalStory> stor = stories.iterator();
    	while(stor.hasNext()){
    		DigitalStory tmp = stor.next();
    		if(tmp.coverImage!=null){
    			tmp.coverImage=tmp.getUrlImage();
    		}
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		res.add(tmpObj);
    		//res.add(JSON.serialize(tmpObj));
    	}
    	//for(Key<DigitalStory> key:stories){res.add(key.getId().toString());}
    	
    	return res;
    }
    
    public long getDigitalStoriesByUserTotalNumber(String userId){return this.createQuery().field("creator").equal(userId).countAll();}
    public int hasDigitalStoriesTheme(String id){
    	List<Key<DigitalStory>> stories = this.createQuery().field("theme").equal(id).asKeyList();
    	return stories.size();    
    }
    
    public long getAllStoriesByTheme(String themeid){return this.createQuery().field("theme").equal(themeid).filter("isPublished", Boolean.TRUE).countAll();}

    public long numberStoriesByTheme(String themeid){return this.createQuery().field("theme").equal(themeid).countAll();}
    
    
   public void deleteStoryObjectFromStories(String id){
	   /*deletes the story object with the given id from all stories*/
	   
	   Query<DigitalStory> q = ds.createQuery(DigitalStory.class);
	   q.field("storyObjects.StoryObjectID").equal(id);
	   
	   Iterable<DigitalStory> stories=((Query<DigitalStory>) this.find(q)).fetch();
   	
	   
	   //Iterable<DigitalStory> 	stories = this.createQuery().disableValidation().field("storyObjects.StoryObjectId").equal(id);
	   Iterator<DigitalStory> it=stories.iterator();
	   while(it.hasNext()){
		   DigitalStory ds=(DigitalStory)it.next();
		   List<StoryObjectPlaceHolder> storyObjects=ds.getStoryObjects();
		   List<StoryObjectPlaceHolder> newStoryObjects=new ArrayList<StoryObjectPlaceHolder>();
		   for(StoryObjectPlaceHolder sop:storyObjects){
			   if(sop==null || sop.getStoryObjectID()==null || sop.getStoryObjectID().equalsIgnoreCase(id)){
				  
			   }else{newStoryObjects.add(sop);}
		   }
		   ds.setStoryObjects(newStoryObjects);
		   ds.updateCoverImage();
		   /* check constraints if europeana story object was deleted, at least one europeana object should be present*/
		   ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
		   Validator validator= factory.getValidator();
		   Set<ConstraintViolation<DigitalStory>> violations = validator.validate(ds);
		   if(violations.size()>0){
			  ds.setPublished(false);
			  ds.setPublishable(false);    	
			}
			
		   this.save(ds);
	   }
   	
   }
    
      public BasicDBList getDigitalStoriesByTheme(String id,int from, int to){
    	Iterable<DigitalStory> stories;
    	if(from==-1 && to==-1)
    		stories = this.createQuery().field("theme").equal(id).filter("isPublished", Boolean.TRUE).order("-dateCreated").fetch();
    	else
    		stories = this.createQuery().field("theme").equal(id).filter("isPublished", Boolean.TRUE).order("-dateCreated").offset(from).limit(to-from).fetch();
		
    	/*get the details of the Digital Stories needed in ui*/
    	//title, id, coverimage, story object array with {id, position, thumbnail}
    	Iterator<DigitalStory> it=stories.iterator();
    	BasicDBList list=new BasicDBList();
    	
    	while(it.hasNext()){
    		DigitalStory dst=(DigitalStory)it.next();
    		list.add(toUIJson(dst));
    	}
        
    	
    	return list;
    }
     
/**
     * What the UI expects to see from a DigitalStory object.
     * @param dst The DigitalStory
     * @return The Json (BSONObject) that the UI likes.
     */
   public BSONObject toUIJson( DigitalStory dst ) {
  		BasicDBObject st=new BasicDBObject();
		
		String sid=dst.getId().toString();
		st.put("id", sid);
		
		
		nullSafePut(st, "creator", dst.getCreator());
		if(dst.getCreator()!=null){
			User u=MongoDB.getUserDAO().get(new ObjectId(dst.getCreator()));
    		nullSafePut(st, "creator_uname", u.getUsername());        			
		}
		nullSafePut( st, "coverimage", dst.getUrlImage());
		nullSafePut( st, "title", dst.getTitle());
		nullSafePut( st, "published", dst.isPublished());
	    List<StoryObjectPlaceHolder> l= dst.getStoryObjects();
	    BasicDBList objectlist=new BasicDBList();
	    if( l != null ) {
	    	Iterator<StoryObjectPlaceHolder> it2=l.iterator();
	    	while(it2.hasNext()){
	    		StoryObjectPlaceHolder sph=(StoryObjectPlaceHolder) it2.next();
	    		String soid=sph.getStoryObjectID();
	    		BasicDBObject bdo=new BasicDBObject();

	    		StoryObject so=MongoDB.getStoryObjectDAO().get(new ObjectId(soid));
	    		if(so!=null){
	    			nullSafePut( bdo, "storyobject_id",soid);
	    			nullSafePut( bdo, "position", sph.getPosition());
	    			String thumb=so.getThumbnail();
	    			nullSafePut( bdo, "thumbnail", thumb);

	    			objectlist.add(bdo);


	    		}
	    	}
			st.put("storyobjects",objectlist);
	    }
		return st;
    }
    
    private static void nullSafePut( BSONObject obj, String key, Object val ) {
    	if( val != null ) obj.put( key, val );
    }
    
    
    
    public void forAll( ApplyI<DigitalStory> func ) throws Exception {
    	try {
    		for( DigitalStory ds: this.createQuery()) {
    			func.apply( ds );
    		}
    	} finally {
    		// nothing to do, apparently cleans up itself :-)
    	}
    }
    
     @Override
    public WriteResult deleteById( ObjectId id ) {
    	// solr delete
    	SolrHelper.deleteById(id.toString());
    	return super.deleteById(id);
    }
    
    
    public void forAllBSON( ApplyI<BSONObject> func ) throws Exception {
    	BasicDBObject query = new BasicDBObject()
    		.append( "className", DigitalStory.class.getCanonicalName());
    	log.debug( "Query: " + query); 
    	for( DBObject obj: getCollection().find( query)) {
    		
    		// embed the StoryObjects ..
    		
    		log.debug( obj.toString());
    		func.apply(obj);
    	}
    }
}
