package gr.ntua.ivml.awareness.db;


import java.util.Iterator;

import gr.ntua.ivml.awareness.persistent.StoryImage;
import gr.ntua.ivml.awareness.persistent.StoryObject;
import gr.ntua.ivml.awareness.search.SolrHelper;
import gr.ntua.ivml.awareness.util.ApplyI;
import gr.ntua.ivml.awareness.util.MongoDB;


import org.bson.types.ObjectId;

import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.WriteResult;


public class StoryImageDAO extends BasicDAO<StoryImage, ObjectId> {
    public StoryImageDAO(  ) {
        super(StoryImage.class, MongoDB.getDS());
    }

    @Override
    public WriteResult deleteById( ObjectId id ) {
    	// solr delete
    	SolrHelper.deleteById(id.toString());
    	return super.deleteById(id);
    }

    public void remove(String id){
       	 StoryImage si=MongoDB.getStoryImageDAO().get(new ObjectId(id));
		
         /* now delete all the story objects that use it */
         Iterable<StoryObject> stories = MongoDB.getStoryObjectDAO().createQuery().field("storyImage").equal(id);
     	 
     	 Iterator<StoryObject> it = stories.iterator();
     	 while(it.hasNext()){
     		StoryObject tmp = it.next();
     		/* and delete those objects from stories that use them*/
     		MongoDB.getDigitalStoryDAO().deleteStoryObjectFromStories(tmp.getId().toString());
     		/* and delete the story object */
     		MongoDB.getStoryObjectDAO().delete(tmp);
     		
     	 }
         /*remove images from gridfs*/
     	 MongoDB.getGridFS().remove(si.getCoverImage());
		 MongoDB.getGridFS().remove(si.getObjectPreview());
		 MongoDB.getGridFS().remove(si.getOriginal());
		 MongoDB.getGridFS().remove(si.getObjectThumbnail());
		 MongoDB.getGridFS().remove(si.getThumbnail());
		 /*delete from collection*/
         MongoDB.getStoryImageDAO().deleteById(new ObjectId(id)); 
     
    }
    
	   public void forAll( ApplyI<StoryImage> func ) throws Exception {
	    	try {
	    		for( StoryImage si: this.createQuery()) {
	    			func.apply( si );
	    		}
	    	} finally {
	    		// nothing to do, apparently cleans up itself :-)
	    	}
	    }

}
