package at.ait.dme.yuma.server.db.mongodb;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.DBCollection;
import com.mongodb.BasicDBObject;
import com.mongodb.MongoException;

import at.ait.dme.yuma.server.config.Config;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.db.AbstractAnnotationDB;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.exception.AnnotationHasReplyException;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.exception.AnnotationNotFoundException;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.AnnotationTree;
import at.ait.dme.yuma.server.model.MapKeys;

/**
 * Annotation DB implementation based on the 
 * MongoDB key/value store.
 * 
 * TODO needs revision! Doesn't work with PlainLiteral!!!
 * 
 * @author Rainer Simon
 */
public class MongoAnnotationDB extends AbstractAnnotationDB {
	
	/**
	 * DB object ID key
	 */
	static final String OID = "_id";
	
	/**
	 * MongoDB database connection
	 */
	private static Mongo MONGO = null;
	
	/**
	 * Database/collection name
	 */
	private static String DB_NAME;
	
	/**
	 * The annotations collection
	 */
	private DBCollection collection = null;

	/**
	 * JSON format handler to perform translations JSON<->Annotation
	 */
	private JSONFormatHandler format = new JSONFormatHandler();
	
	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(MongoAnnotationDB.class);
	
	@Override
	public synchronized void init() throws AnnotationDatabaseException {
		final Config config = Config.getInstance();
		try {
			if (MONGO == null) {
				MONGO = new Mongo(config.getDbHost(), Integer.parseInt(config.getDbPort()));
				DB_NAME = config.getDbName();
			}
		} catch (NumberFormatException e) {
			throw new AnnotationDatabaseException(e);
		} catch (MongoException e) {
			throw new AnnotationDatabaseException(e);
		} catch (UnknownHostException e) {
			throw new AnnotationDatabaseException(e);
		}
	}

	@Override
	public void shutdown() {
		if (MONGO != null) MONGO.close();
	}

	@Override
	public void connect(HttpServletRequest request, HttpServletResponse response)
		throws AnnotationDatabaseException {
		
		if (MONGO == null) 
			throw new AnnotationDatabaseException("Database not initialized");
		
		try {
			DB db = MONGO.getDB(DB_NAME);
			collection = db.getCollection(DB_NAME);
		} catch (MongoException e) {
			throw new AnnotationDatabaseException(e.getMessage());
		}
	}

	@Override
	public void disconnect() {
		//
	}

	@Override
	public void commit() throws AnnotationDatabaseException {
		// TODO check http://www.mongodb.org/display/DOCS/Atomic+Operations
	}

	@Override
	public void rollback() throws AnnotationDatabaseException {
		// TODO check http://www.mongodb.org/display/DOCS/Atomic+Operations
	}

	@Override
	public String createAnnotation(Annotation annotation) throws InvalidAnnotationException  {
		checkIntegrity(annotation);
		DBObject dbo = toDBObject(annotation);
		collection.insert(dbo);
		return ((ObjectId) dbo.get(OID)).toString();
	}

	@Override
	public String updateAnnotation(String annotationId, Annotation annotation)
			throws AnnotationDatabaseException, AnnotationNotFoundException, AnnotationHasReplyException, InvalidAnnotationException {
		
		checkIntegrity(annotation);
		
		DBObject before = findDBObjectByAnnotationID(annotationId);

		if (countReplies(annotationId) > 0)
			throw new AnnotationHasReplyException();
		
		DBObject after = toDBObject(annotation);
		collection.update(before, after);

		// Note: MongoDB does not change the ID on updates
		return annotationId;
	}

	@Override
	public void deleteAnnotation(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException, AnnotationHasReplyException {
		
		DBObject dbo = findDBObjectByAnnotationID(annotationId);

		if (countReplies(annotationId) > 0)
			throw new AnnotationHasReplyException();
		
		collection.remove(dbo);
	}

	@Override
	public AnnotationTree findAnnotationsForObject(String objectUri)
			throws AnnotationDatabaseException {
		
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		BasicDBObject query = new BasicDBObject();
		query.put(MapKeys.ANNOTATION_OBJECT_URI, objectUri);
		DBCursor cursor = collection.find(query);
		
		while (cursor.hasNext()) {
			try {
				annotations.add(toAnnotation(cursor.next()));
			} catch (InvalidAnnotationException e) {
				// Should never happen
				throw new AnnotationDatabaseException(e);
			}
		}
		
		return new AnnotationTree(annotations);
	}
	
	@Override
	public long countAnnotationsForObject(String objectUri)
			throws AnnotationDatabaseException {

		BasicDBObject query = new BasicDBObject();
		query.put(MapKeys.ANNOTATION_OBJECT_URI, objectUri);
		return collection.count(query);
	}
	
	@Override
	public List<Annotation> findAnnotationsForUser(String username)
			throws AnnotationDatabaseException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Annotation findAnnotationById(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException {

		try {
			return toAnnotation((findDBObjectByAnnotationID(annotationId)));
		} catch (InvalidAnnotationException e) {
			// Should never happen
			throw new AnnotationDatabaseException(e);
		}
	}
	
	@Override
	public long countReplies(String annotationId) {
		BasicDBObject query = new BasicDBObject();
		query.put(MapKeys.ANNOTATION_PARENT_ID, annotationId);
		return collection.count(query);
	}
	
	@Override
	public AnnotationTree getReplies(String annotationId)
			throws AnnotationDatabaseException, AnnotationNotFoundException {
		
		String rootId = findAnnotationById(annotationId).getRootId();		
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		BasicDBObject query = new BasicDBObject();
		query.put(MapKeys.ANNOTATION_ROOT_ID, rootId);
		DBCursor cursor = collection.find(query);
		
		while (cursor.hasNext()) {
			try {
				annotations.add(toAnnotation(cursor.next()));
			} catch (InvalidAnnotationException e) {
				// Should never happen
				throw new AnnotationDatabaseException(e);
			}
		}
		
		return new AnnotationTree(annotations);
	}
	
	@Override
	public List<Annotation> getMostRecent(int n, boolean publicOnly)
			throws AnnotationDatabaseException {

		// TODO implement publicOnly behavior!
		ArrayList<Annotation> annotations = new ArrayList<Annotation>();
		
		DBCursor cursor = collection.find()
			.sort(new BasicDBObject(MapKeys.ANNOTATION_CREATED, 1));
		
		int ctr = 0;
		while (cursor.hasNext() && ctr < n) {
			try {
				annotations.add(toAnnotation(cursor.next()));
			} catch (InvalidAnnotationException e) {
				logger.error("Got invalid annotation from MongoDB: " + e.getMessage());
			}
		}
		
		return annotations;
	}

	@Override
	public List<Annotation> findAnnotations(String query)
			throws AnnotationDatabaseException {

		// TODO implement correctly!
		BasicDBObject dbo = new BasicDBObject();
		dbo.put(MapKeys.ANNOTATION_TITLE, query);
		dbo.put(MapKeys.ANNOTATION_TEXT, query);
		
		DBCursor cursor = collection.find(dbo);
		ArrayList<Annotation> results = new ArrayList<Annotation>();
		while(cursor.hasNext()) {
			try {
				results.add(toAnnotation(cursor.next()));
			} catch (InvalidAnnotationException e) {
				throw new AnnotationDatabaseException(e);
			}
		}
		return results;
	}
	
	private DBObject findDBObjectByAnnotationID(String annotationId)
		throws AnnotationDatabaseException, AnnotationNotFoundException {
		
		BasicDBObject query = new BasicDBObject();
		query.put(OID, new ObjectId(annotationId));
		DBCursor cursor = collection.find(query);
		
		if (cursor.count() > 1)
			// Should never happen
			throw new AnnotationDatabaseException("More than one object for this ID");
		
		if (cursor.count() > 0)
			return cursor.next();
		
		throw new AnnotationNotFoundException();
	}
	
	/**
	 * Performs a number of consistency checks on the annotation
	 * @param annotation the annotation
	 */
	private void checkIntegrity(Annotation annotation) throws InvalidAnnotationException {
		// TODO check for consistency - if it's a reply it needs to have the same objectID etc.
	}
	
	/**
	 * Turns an annotation into a map suitable for storage inside MongoDB
	 * @param annotation the annotation
	 * @return the map
	 */
	private DBObject toDBObject(Annotation annotation) {
		return new BasicDBObject(format.annotationToJSONFormat(annotation));
	}
	
	/**
	 * Creates an annotation from a map retrieved from MongoDB.
	 * @param map the map
	 * @return the annotation
	 * @throws InvalidAnnotationException if something is wrong with the annotation
	 */
	private Annotation toAnnotation(DBObject dbo) throws InvalidAnnotationException {
		try {
			@SuppressWarnings("unchecked")
			Map<String, Object> map = (Map<String, Object>) dbo.toMap();
			map.put(MapKeys.ANNOTATION_ID, dbo.get(MongoAnnotationDB.OID).toString());
			map.remove(MongoAnnotationDB.OID);
			return format.toAnnotation(map);	
		} catch (Throwable t) {
			throw new InvalidAnnotationException(t);
		}
	}

}
