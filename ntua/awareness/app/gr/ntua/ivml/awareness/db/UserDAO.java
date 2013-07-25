package gr.ntua.ivml.awareness.db;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Pattern;

import org.bson.types.ObjectId;

import gr.ntua.ivml.awareness.util.MongoDB;


import com.google.code.morphia.dao.BasicDAO;
import com.google.code.morphia.query.Query;
import com.mongodb.DBObject;


import gr.ntua.ivml.awareness.persistent.User;

public class UserDAO extends BasicDAO<User, ObjectId> {
    public UserDAO(  ) {
        super(User.class, MongoDB.getDS());
    }
    
    public ArrayList<String> getUsersByRange(int from,int to){
    	List<User> ulist=createQuery().offset(from).limit(to-from).asList();
    	ArrayList<String> ids=new ArrayList<String>();
    	for(User u:ulist){
    		ids.add(u.getId().toString());
    	}
    	
    	return ids;
    }
    
    public ArrayList<DBObject> getUsersByOrder(int from,int size,String sortby){
    	Iterable<User> ulist=createQuery().offset(from).limit(size).order(sortby).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	
    	Iterator<User> it = ulist.iterator();
		
    	while(it.hasNext()){
    		User tmp = it.next();
    		tmp.getId();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		tmpObj.removeField("md5Password");
    		tmpObj.removeField("_id");
    		Date ac=tmp.getAccountCreated();
    		
    		tmpObj.put("id", tmp.getId());
    		tmpObj.put("accountCreated","/Date("+ac.getTime()+")/");
    		//tmpObj.put("accountCreated", tmp.getAccountCreated().toString());
    		
    		//res.add(json);
    		res.add(tmpObj);
    	}
    	
    	return res;
    }
    
    public ArrayList<DBObject> getUsersBySearch(int from,int size,String sortby,String searchterm){

        Pattern regex = Pattern.compile(".*" + searchterm + ".*", Pattern.CASE_INSENSITIVE);
        
        Query<User> q = MongoDB.getDS().createQuery(User.class);
        q.or(
                q.criteria("email").equal(regex),
                q.criteria("username").equal(regex)
        ); 
    	Iterable<User> ulist=((Query<User>) this.find(q)).offset(from).limit(size).order(sortby).fetch();
    	ArrayList<DBObject> res = new ArrayList<DBObject>();
    	
    	Iterator<User> it = ulist.iterator();
    	//ArrayNode uarray=new ArrayNode(JsonNodeFactory.instance);
		
    	while(it.hasNext()){
    		User tmp = it.next();
    		tmp.getId();
    		DBObject tmpObj = MongoDB.getMorphia().toDBObject(tmp);
    		tmpObj.removeField("md5Password");
    		tmpObj.removeField("_id");
    		SimpleDateFormat f = new SimpleDateFormat("yy-MM-dd");
    		Date ac=tmp.getAccountCreated();
    		
    		tmpObj.put("id", tmp.getId());
    		tmpObj.put("accountCreated","/Date("+ac.getTime()+")/");
    		res.add(tmpObj);
    	}
    	
    	return res;
    }
    

    public User getByEmailPassword( String email, String password ) {
		User u;
		u = new User();
		u.encryptAndSetEmailPassword(email, password);
		return createQuery().field("md5Password").equal(u.md5Password).get();
		}
    
	public User getByEmail( String email) {
		return createQuery().field("email").equal(email).get();
	} 
	
	public User getByUsername( String username) {
		return createQuery().field("username").equal(username).get();
		
	}
	
	
	public boolean isUsernameAvailable( String username ) {
		List<User> users= createQuery().field("username").equal(username).asList();
		
		return ( users.size() == 0 );
	}
	
	public boolean isEmailAvailable( String email ) {
        List<User> users= createQuery().field("email").equal(email).asList();
		return ( users.size() == 0 );
	}
}