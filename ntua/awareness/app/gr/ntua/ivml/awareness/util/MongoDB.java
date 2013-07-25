package gr.ntua.ivml.awareness.util;


import gr.ntua.ivml.awareness.db.DigitalStoryDAO;
import gr.ntua.ivml.awareness.db.EuropeanaImageDAO;
import gr.ntua.ivml.awareness.db.GridFSDAO;
import gr.ntua.ivml.awareness.db.StoryCommentsDAO;
import gr.ntua.ivml.awareness.db.StoryImageDAO;
import gr.ntua.ivml.awareness.db.StoryObjectDAO;
import gr.ntua.ivml.awareness.db.UserDAO;
import gr.ntua.ivml.awareness.db.ThemeDAO;
import gr.ntua.ivml.awareness.persistent.User;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.dao.BasicDAO;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.gridfs.GridFS;


public class MongoDB {
	private static Mongo mongo = null;
	private static DB db = null;
	private static GridFS gridFS = null;
	private static Datastore ds=null;
	private static UserDAO userDAO=null;
	private static DigitalStoryDAO dsDAO = null;
	private static ThemeDAO themeDAO=null;
	private static StoryObjectDAO soDAO = null;
	private static StoryImageDAO siDAO = null;
	private static EuropeanaImageDAO euDAO = null;
	private static StoryCommentsDAO scDAO = null;
	private static GridFSDAO gridDAO = null;
	private static Morphia morph = null;
	
     static{
		String host = Config.get("mongo.host");
		int port = Config.getInt("mongo.port");
		String database = Config.get("mongo.db");
		
		String username = Config.get("mongo.username");
		String password = Config.get("mongo.password");
		boolean authenticate = !Config.getBoolean("mongo.noauth");
		
		try {
			mongo = new Mongo(host, port);
			//db=mongo.getDB(database);
			ds = new Morphia().map(User.class).createDatastore(mongo,database);
			ds.ensureIndexes();
			db=ds.getDB();
			gridFS = new GridFS( db );
			morph = new Morphia();
			morph.mapPackage("gr.ntua.ivml.awareness.persistent");
//			gridFS = new GridFS( db );
			//instantiate the DAOS
			userDAO=(UserDAO) instantiateDAO( UserDAO.class );
			dsDAO = (DigitalStoryDAO) instantiateDAO( DigitalStoryDAO.class );
			soDAO = (StoryObjectDAO) instantiateDAO(StoryObjectDAO.class);
			themeDAO = (ThemeDAO) instantiateDAO(ThemeDAO.class);
			siDAO = (StoryImageDAO) instantiateDAO(StoryImageDAO.class);
			euDAO = (EuropeanaImageDAO) instantiateDAO(EuropeanaImageDAO.class);
			scDAO = (StoryCommentsDAO) instantiateDAO(StoryCommentsDAO.class);
			gridDAO = new GridFSDAO();
			if(authenticate) {
				boolean auth = db.authenticate(username, password.toCharArray());
				if(!auth) {
					System.err.println("MongoDB authentication failed");					
				}
			}

		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (MongoException e) {
			e.printStackTrace();
		}
		
		System.out.println("MongoDB connection started");
	}
	
	public static DB getDB(){
		return db;
	}
	
	public static Datastore getDS(){
		return ds;
	}
	
	public static Mongo getMongo(){
		return mongo;
	}
	
	public static GridFS getGridFS(){
		return gridFS;
	}
	
	public static UserDAO getUserDAO() {
		return userDAO;
	}
	
	public static StoryImageDAO getStoryImageDAO() {
		return siDAO;
	}
	
	
	public static DigitalStoryDAO getDigitalStoryDAO(){
		return dsDAO;
	}
	
	public static StoryObjectDAO getStoryObjectDAO(){
		return soDAO;
	}
	
	public static EuropeanaImageDAO getEuropenaImageDAO(){
		return euDAO;
	}
	
	
	public static ThemeDAO getThemeDAO(){
		return themeDAO;
	}
	
	public static GridFSDAO getGridFSDAO(){
		return gridDAO;
	}
	
	public static Morphia getMorphia(){
		return morph;
	}

	private static BasicDAO instantiateDAO(Class<? extends BasicDAO> daoClass) {
        try {
            BasicDAO dao = (BasicDAO)daoClass.newInstance();
            return dao;
        } catch (Exception ex) {
            throw new RuntimeException("Can not instantiate DAO: " + daoClass, ex);
        }
    }

	public static StoryCommentsDAO getScDAO() {
		return scDAO;
	}

}
