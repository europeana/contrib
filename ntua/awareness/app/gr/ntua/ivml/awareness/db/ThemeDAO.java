package gr.ntua.ivml.awareness.db;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;
import gr.ntua.ivml.awareness.util.MongoDB;



import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.mongodb.DBObject;



import gr.ntua.ivml.awareness.persistent.Theme;
import gr.ntua.ivml.awareness.persistent.User;


public class ThemeDAO extends BasicDAO<Theme, ObjectId> {
    public ThemeDAO(  ) {
        super(Theme.class, MongoDB.getDS());
    }
    
    public Theme getDefault( ) {
		
		return createQuery().field("defaultTheme").equal(true).get();
		}
    
    public ArrayList<DBObject> getThemesByOrder(int from,int size,String sortby){
    	Iterable<Theme> tlist=createQuery().offset(from).limit(size).order(sortby).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	
    	Iterator<Theme> it = tlist.iterator();
    	
		
    	while(it.hasNext()){
    		Theme tmp = it.next();
    		tmp.getId();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		tmpObj.removeField("_id");
    		
    		tmpObj.put("id", tmp.getId());
    		res.add(tmpObj);
    	}
    	
    	return res;
    }

    public ArrayList<DBObject> getThemesBySearch(int from,int size,String sortby,String searchterm){

        Pattern regex = Pattern.compile(".*" + searchterm + ".*", Pattern.CASE_INSENSITIVE);
        
        Query<Theme> q = MongoDB.getDS().createQuery(Theme.class);
        q.or(
                q.criteria("title").equal(regex),
                q.criteria("description").equal(regex)
        ); 
    	Iterable<Theme> tlist=((Query<Theme>) this.find(q)).offset(from).limit(size).order(sortby).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	
    	Iterator<Theme> it = tlist.iterator();
    	//ArrayNode uarray=new ArrayNode(JsonNodeFactory.instance);
		
    	while(it.hasNext()){
    		Theme tmp = it.next();
    		tmp.getId();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		
    		tmpObj.removeField("_id");
    		
    		tmpObj.put("id", tmp.getId());
    		res.add(tmpObj);
    	}
    	
    	return res;
    }
    
    
}