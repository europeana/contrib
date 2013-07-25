package at.researchstudio.dme.annotation.db.sesame;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ArrayList;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.URI;
import org.openrdf.model.Value;
import org.openrdf.model.ValueFactory;
import org.openrdf.model.impl.StatementImpl;
import org.openrdf.query.GraphQueryResult;
import org.openrdf.query.QueryLanguage;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.RepositoryResult;
import org.openrdf.repository.sail.SailRepository;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.rdfxml.RDFXMLParser;
import org.openrdf.rio.rdfxml.RDFXMLWriter;
import org.openrdf.sail.memory.MemoryStore;
import org.openrdf.sail.nativerdf.NativeStore;
import org.openrdf.sail.rdbms.postgresql.PgSqlStore;

import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.AnnotationDatabase;
import at.researchstudio.dme.annotation.db.AnnotationDatabaseLockManager;
import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationLockedException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * implementation of the annotation database using sesame (http://www.openrdf.org)
 * 
 * @author Christian Sadilek
 */
public class SesameAnnotationDatabase extends AnnotationDatabase {
	private static Logger logger = Logger.getLogger(SesameAnnotationDatabase.class);
	private static final String NATIVE = "native";
	private static final String INDEXER = "spoc,posc,cosp";

	private static final String ANNOTATION_NS = 
		"http://lemo.mminf.univie.ac.at/annotation-core#";
	private static final String ANNOTATION_ANNOTATES = ANNOTATION_NS + "annotates";

	private static final String ANNOTEA_ANNOTATION_NS = 
		"http://www.w3.org/2000/10/annotation-ns#";
	private static final String ANNOTATION_BODY = ANNOTEA_ANNOTATION_NS + "body";

	private static final String ANNOTATION_REL_NS = 
		"http://lemo.mminf.univie.ac.at/ann-relationship#";
	private static final String ANNOTATION_LINKED_TO = ANNOTATION_REL_NS + "isLinkedTo";

	private static final String ANNOTATION_IMAGE_NS = 
		"http://lemo.mminf.univie.ac.at/annotation-image#";

	private static final String ANNOTATION_VIDEO_NS = 
		"http://lemo.mminf.univie.ac.at/annotation-video#";

	private static final String ANNOTATION_SCOPE_NS = 
		"http://lemo.mminf.univie.ac.at/ann-tel#";

	private static final String THREAD_NS = "http://www.w3.org/2001/03/thread#";
	private static final String THREAD_ROOT = THREAD_NS + "root";
	private static final String THREAD_IN_REPLY_TO = THREAD_NS + "inReplyTo";

	private static final String HTTP_NS = "http://www.w3.org/1999/xx/http#";
	private static final String HTTP_BODY = HTTP_NS + "Body";

	private static final String SVG_NS = "http://www.w3.org/2000/svg";
	private static final String DUBLIN_CORE_NS = "http://purl.org/dc/elements/1.1/";

	private static final long LOCK_TIMEOUT = 10000l;

	private static Repository repository = null;
	private static ValueFactory valueFactory = null;

	private RepositoryConnection repositoryConnection = null;
	// an in-memory repository is used to export the statements in a pretty rdf/xml.
	private Repository inMemoryRepository = null;
	private RepositoryConnection inMemoryRepositoryConnection = null;

	// the lock manager for this db
	private AnnotationDatabaseLockManager lockManager = null;

	// handler used to parse rdf xml and to store the statements in a repository
	private class RdfXmlAnnotationHandler implements RDFHandler {
		private Resource annotation = null;
		private Resource body = null;
		private String annotationBaseUrl = null;
		private String annotationBodyBaseUrl = null;
		private String replyToId = null;
		private boolean reply = false;
		private Collection<Statement> statements = new ArrayList<Statement>();

		public RdfXmlAnnotationHandler(String annotationBaseUrl, String annotationBodyBaseUrl) {
			this.annotationBaseUrl = annotationBaseUrl;
			this.annotationBodyBaseUrl = annotationBodyBaseUrl;
		}

		public Resource getAnnotation() {
			return annotation;
		}

		public String getReplyToId() {
			return replyToId;
		}

		public boolean isReply() {
			return reply;
		}

		public Collection<Statement> getStatements() {
			return statements;
		}

		public void handleStatement(Statement statement) throws RDFHandlerException {
			// we change the id of the resource e.g.
			// <r:Description r:about="http://annotea.example.org/Annotation/3ACF6D754"> and
			// <r:Description r:about="http://annotea.example.org/Annotation/body/3ACF6D754">
			if (annotation == null) {
				String id = valueFactory.createBNode().stringValue();
				annotation = valueFactory.createBNode(id.replace("node", annotationBaseUrl));
				body = valueFactory.createBNode(id.replace("node", annotationBodyBaseUrl));
			}

			if (statement.getPredicate().equals(valueFactory.createURI(THREAD_IN_REPLY_TO))) {
				reply = true;
				replyToId = statement.getObject().stringValue();
			}

			if (statement.getPredicate().getNamespace().equals(HTTP_NS)) {
				// body statement
				statements.add(new StatementImpl(body, statement.getPredicate(), 
						statement.getObject()));
			} else {
				// annotation statement 
				// in case of the annotation body predicate we have to add the
				// created body to the annotation.
				if (statement.getPredicate().equals(valueFactory.createURI(ANNOTATION_BODY))) {
					// use the provided URI
					if (statement.getObject() instanceof URI) body=(Resource)statement.getObject();
					statements.add(new StatementImpl(annotation, statement.getPredicate(), body));
				} else {
					statements.add(new StatementImpl(annotation, statement.getPredicate(),
							statement.getObject()));
				}
			}
		}

		public void endRDF() throws RDFHandlerException {}
		public void handleComment(String comment) throws RDFHandlerException {}
		public void handleNamespace(String prefix, String uri) throws RDFHandlerException {}
		public void startRDF() throws RDFHandlerException {}
	}

	@Override
	public synchronized void init() throws AnnotationDatabaseException {
		try {
			Config config = Config.getInstance();

			if(config.getDbFlags() != null && config.getDbFlags().contains(NATIVE)) {
				NativeStore ns = new NativeStore(new File(config.getDbDir()));
				ns.setTripleIndexes(INDEXER);
				repository = new SailRepository(ns);
			} else {
				PgSqlStore db = new PgSqlStore();
				db.setDatabaseName(config.getDbName());
				db.setUser(config.getDbUser());
				db.setPassword(config.getDbPass());
				db.setPortNumber(new Integer(config.getDbPort()));
				db.setServerName(config.getDbHost());
				repository = new SailRepository(db);
			}

			repository.initialize();
			valueFactory = repository.getValueFactory();
		} catch (Throwable t) {
			logger.error("failed to initialize database", t);
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public synchronized void shutdown() {
		try {
			if(repository != null)
				repository.shutDown();
		} catch (Exception e) {
			logger.error("failed to shutdown database", e);
		}
	}

	@Override
	public void connect(HttpServletRequest request) throws AnnotationDatabaseException {
		try {
			if(repository == null)
				throw new AnnotationDatabaseException("Repository not initialized");

			// we set auto commit to false here because otherwise Sesame commits
			// every statement separately
			repositoryConnection = repository.getConnection();
			repositoryConnection.setAutoCommit(false);

			lockManager = Config.getInstance().getLockManager();
			if(lockManager == null)
				throw new AnnotationDatabaseException("no lockmanager specified");
			lockManager.setConnection(getNativeConnection());
		} catch (Throwable t) {
			logger.error("failed to connect to database", t);
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public void disconnect() {
		try {
			if(repositoryConnection != null) 
				repositoryConnection.close();
			if(lockManager != null)
				lockManager.closeConnection();
		} catch (Exception e) {
			logger.error("failed to disconnect from database", e);
		}
	}

	@Override
	public void commit() throws AnnotationDatabaseException {
		try {
			if(repositoryConnection != null)
				repositoryConnection.commit();
		} catch (RepositoryException e) {
			logger.error("commit failed", e);
			throw new AnnotationDatabaseException(e);
		}
	}

	@Override
	public void rollback() throws AnnotationDatabaseException {
		try {
			if(repositoryConnection != null)
				repositoryConnection.rollback();
		} catch (RepositoryException e) {
			logger.error("rollback failed", e);
			throw new AnnotationDatabaseException(e);
		}
	}

	@Override
	public java.net.URI createAnnotation(String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationModifiedException {
		return createAnnotation(annotation, false);
	}

	private java.net.URI createAnnotation(String annotation, boolean update)
			throws AnnotationDatabaseException, AnnotationException, AnnotationModifiedException {

		java.net.URI uri = null;
		String lockedAnnId = null;
		try {
			Config config = Config.getInstance();
			RdfXmlAnnotationHandler rdfHandler = new RdfXmlAnnotationHandler(config
					.getAnnotationBaseUrl(), config.getAnnotationBodyBaseUrl());

			RDFParser parser = new RDFXMLParser();
			parser.setVerifyData(false);
			parser.setRDFHandler(rdfHandler);
			parser.parse(new StringReader(annotation), config.getAnnotationBaseUrl());

			// this check needs to be done only for the creation of replies. as soon as an 
			// annotation is referenced it cannot be modified.
			if(!update && rdfHandler.isReply()) {
				lockedAnnId = lockManager.acquireLock(rdfHandler.getReplyToId(), LOCK_TIMEOUT);
				// check if the referenced annotation has been modified. since update creates 
				// a new id we just have to check if the annotation still exists
				if (!existsAnnotation(lockedAnnId)) {
					throw new AnnotationModifiedException(rdfHandler.getReplyToId());
				}
			}
			repositoryConnection.add(rdfHandler.getStatements());

			uri = new java.net.URI(rdfHandler.getAnnotation().stringValue());
			if(isAutoCommit())
				repositoryConnection.commit();
			
		} catch (RepositoryException e) {
			logger.error(FAILED_TO_SAVE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (OpenRDFException e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationException(e);
		} catch (IOException e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationException(e);
		} catch (AnnotationLockedException ale) {
			logger.error(ale.getMessage(), ale);
			throw new AnnotationDatabaseException(ale);
		} catch(AnnotationModifiedException ame) {
			throw ame;
		} catch (Throwable t) {
			logger.error(FAILED_TO_SAVE_ANNOTATION + ":" + t.getMessage(), t);
			throw new AnnotationDatabaseException(t);
		} finally {
			if (lockedAnnId != null)
				lockManager.releaseLock(lockedAnnId);
		}

		return uri;
	}

	@Override
	public java.net.URI updateAnnotation(String id, String annotation)
			throws AnnotationDatabaseException, AnnotationException,
			AnnotationAlreadyReferencedException {

		String lockedAnnotationId = null;
		try {
			lockedAnnotationId = lockManager.acquireLock(id, LOCK_TIMEOUT);
			if(hasReplies(id))
				throw new AnnotationAlreadyReferencedException(id);
			removeAnnotation(id);
			
			return createAnnotation(annotation, true);
		} catch (RepositoryException e) {
			logger.error(FAILED_TO_DELETE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (AnnotationLockedException ale) {
			logger.error(ale.getMessage(), ale);
			throw new AnnotationDatabaseException(ale);
		} catch (AnnotationModifiedException ame) {
			// As long as our locks are working this can never happen
			logger.fatal(ame.getMessage(), ame);
			throw new AnnotationDatabaseException(ame);
		} finally {
			if (lockedAnnotationId != null)
				lockManager.releaseLock(lockedAnnotationId);
		}
	}

	@Override
	public void deleteAnnotation(String id) throws AnnotationDatabaseException,
			AnnotationAlreadyReferencedException {

		String lockedAnnotationId = null;
		try {
			lockedAnnotationId = lockManager.acquireLock(id, LOCK_TIMEOUT);
			if(hasReplies(id)) throw new AnnotationAlreadyReferencedException(id);
			removeAnnotation(id);

			if(isAutoCommit()) repositoryConnection.commit();
		} catch (RepositoryException e) {
			logger.error(FAILED_TO_DELETE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (AnnotationLockedException ale) {
			logger.error(ale.getMessage(), ale);
			throw new AnnotationDatabaseException(ale);
		} finally {
			if(lockedAnnotationId != null)
				lockManager.releaseLock(lockedAnnotationId);
		}
	}

	@Override
	public String findAnnotationById(String id) throws AnnotationDatabaseException,
			AnnotationNotFoundException {
		StringWriter sw = new StringWriter();

		try {
			createInMemoryRepositoryConnection();

			// find all statements of the annotation (subject) in question
			Resource annotationSubject = valueFactory.createBNode(makeValidAnnotationId(id));
			RepositoryResult<Statement> annotationStatements = repositoryConnection.getStatements(
					annotationSubject, null, null, false);

			if(!annotationStatements.hasNext())
				throw new AnnotationNotFoundException(id);

			// add all statements to the in memory repository
			addAnnotationStatements(inMemoryRepositoryConnection, annotationStatements,
					valueFactory.createURI(annotationSubject.stringValue()));

			// export the statements
			inMemoryRepositoryConnection.export(createRdfXmlWriter(sw));
		} catch (RepositoryException e) {
			logger.error(FAILED_TO_READ_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (RDFHandlerException e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (AnnotationNotFoundException anfe) {
			throw anfe;
		} finally {
			shutDownInMemoryRepositoryConnection();
		}

		return sw.toString();
	}

	@Override
	public String findAnnotationBodyById(String id) throws AnnotationDatabaseException,
			AnnotationException {

		String body = "";
		try {
			// find all statements of the annotation body in question
			Resource bodySubject = valueFactory.createBNode(makeValidAnnotationBodyId(id));
			RepositoryResult<Statement> httpBodyStatements = repositoryConnection.getStatements(
					bodySubject, valueFactory.createURI(HTTP_BODY), null, true);

			// should be only one
			for(Statement httpBodyStatement : httpBodyStatements.asList()) {
				body += httpBodyStatement.getObject().stringValue();
			}
		} catch (RepositoryException e) {
			logger.error(FAILED_TO_READ_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} finally {
			shutDownInMemoryRepositoryConnection();
		}

		return body;
	}

	@Override
	public String findAnnotations(String term) throws AnnotationDatabaseException {
		String query = "CONSTRUCT {?x <" + HTTP_BODY + "> ?body} " + 
						"WHERE {?x <" + HTTP_BODY + "> ?body " +
							"FILTER regex(str(?body), \"" + term + "\", \"i\")" +
						"}";
		
		String result = "";
		Collection<Value> bodies = new ArrayList<Value>();
		try {
			GraphQueryResult graphResult = 
				repositoryConnection.prepareGraphQuery(QueryLanguage.SPARQL, query).evaluate();
			while (graphResult.hasNext()) {
				Statement statement = graphResult.next();
				bodies.add(statement.getSubject());
			}
			result = listAnnotationsByPredicate(valueFactory.createURI(ANNOTATION_BODY), bodies);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AnnotationDatabaseException(e);
		}

		return result;
	}

	@Override
	public String listAnnotations(String objectId) throws AnnotationDatabaseException {
		// find all annotations (resources that have a ANNOTATION_ANNOTATES predicate for
		// the corresponding objectId)
		return listAnnotationsByPredicate(valueFactory.createURI(ANNOTATION_ANNOTATES),
				valueFactory.createURI(objectId));
	}

	@Override
	public int countAnnotations(String objectId) throws AnnotationDatabaseException {
		// count all annotations (resources that have a ANNOTATION_ANNOTATES predicate for
		// the corresponding objectId)
		return countAnnotationsByPredicate(valueFactory.createURI(ANNOTATION_ANNOTATES),
				valueFactory.createURI(objectId));
	}

	@Override
	public String listAnnotationReplies(String id) throws AnnotationDatabaseException {
		// find all annotations (resources that have a THREAD_ROOT predicate for
		// the corresponding annotation id)
		return listAnnotationsByPredicate(valueFactory.createURI(THREAD_ROOT), valueFactory
				.createURI(makeValidAnnotationId(id)));
	}

	@Override
	public String listLinkedAnnotations(String linkedObjectId) throws AnnotationDatabaseException {
		// find all annotations (resources that have a ANNOTATION_LINKED_TO predicate for
		// the corresponding linked object id)
		return listAnnotationsByPredicate(valueFactory.createURI(ANNOTATION_LINKED_TO),
				valueFactory.createLiteral(linkedObjectId));
	}

	@Override
	public int countLinkedAnnotations(String linkedObjectId) throws AnnotationDatabaseException,
			AnnotationException {
		// count all annotations (resources that have a ANNOTATION_LINKED_TO predicate for
		// the corresponding linked object id)
		return countAnnotationsByPredicate(valueFactory.createURI(ANNOTATION_LINKED_TO),
				valueFactory.createLiteral(linkedObjectId));
	}

	private void removeAnnotation(String id) throws RepositoryException {
		// remove body
		repositoryConnection.remove(
				valueFactory.createBNode(makeValidAnnotationBodyId(id)), null, null);

		// remove annotation
		repositoryConnection.remove(
				valueFactory.createBNode(makeValidAnnotationId(id)), null, null);
	}

	private String listAnnotationsByPredicate(URI predicate, Value value) 
		throws AnnotationDatabaseException {

		Collection<Value> values = new ArrayList<Value>();
		values.add(value);
		return listAnnotationsByPredicate(predicate, values);
	}

	private String listAnnotationsByPredicate(URI predicate, Collection<Value> values) 
		throws AnnotationDatabaseException {

		StringWriter sw = new StringWriter();
		try {
			createInMemoryRepositoryConnection();
			for (Value value : values) {
				// find all annotations that have the predicate
				RepositoryResult<Statement> annotations = repositoryConnection.getStatements(null,
						predicate, value, false);

				for (Statement annotation : annotations.asList()) {
					// find all statements of the annotation resource and add them to the
					// in-memory repository
					RepositoryResult<Statement> annotationStatements = repositoryConnection
							.getStatements(annotation.getSubject(), null, null, true);

					// add all statements to the connection
					addAnnotationStatements(inMemoryRepositoryConnection, annotationStatements,
							valueFactory.createURI(annotation.getSubject().stringValue()));
				}
			}
			inMemoryRepositoryConnection.export(createRdfXmlWriter(sw));
		} catch (Throwable t) {
			logger.error(t.getMessage(), t);
			throw new AnnotationDatabaseException(t);
		} finally {
			shutDownInMemoryRepositoryConnection();
		}
		return sw.toString();
	}

	private int countAnnotationsByPredicate(URI predicate, Value value) 
		throws AnnotationDatabaseException {

		try {
			// find all annotations that have the predicate
			RepositoryResult<Statement> annotations = 
				repositoryConnection.getStatements(null, predicate, value, false);
			return annotations.asList().size();
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			throw new AnnotationDatabaseException(e);
		}
	}

	private void createInMemoryRepositoryConnection() throws RepositoryException {
		// create an in-memory repository for the relevant statements
		inMemoryRepository = new SailRepository(new MemoryStore());
		inMemoryRepository.initialize();
		inMemoryRepositoryConnection = inMemoryRepository.getConnection();
	}

	private void shutDownInMemoryRepositoryConnection() throws AnnotationDatabaseException {
		if (inMemoryRepositoryConnection != null) {
			try {
				inMemoryRepositoryConnection.close();
			} catch (RepositoryException e) {
				logger.error("failed to close in memory repository connection", e);
				throw new AnnotationDatabaseException(e);
			}
		}
		if (inMemoryRepository != null) {
			try {
				inMemoryRepository.shutDown();
			} catch (RepositoryException e) {
				logger.error("failed to shutdown in memory repository", e);
				throw new AnnotationDatabaseException(e);
			}
		}
	}

	private RDFXMLWriter createRdfXmlWriter(StringWriter sw) {
		RDFXMLWriter rdfXmlWriter = new RDFXMLPrettyWriter(sw);

		rdfXmlWriter.handleNamespace("a", ANNOTATION_NS);
		rdfXmlWriter.handleNamespace("ann", ANNOTEA_ANNOTATION_NS);
		rdfXmlWriter.handleNamespace("scope", ANNOTATION_SCOPE_NS);
		rdfXmlWriter.handleNamespace("image", ANNOTATION_IMAGE_NS);
		rdfXmlWriter.handleNamespace("video", ANNOTATION_VIDEO_NS);
		rdfXmlWriter.handleNamespace("rel", ANNOTATION_REL_NS);
		rdfXmlWriter.handleNamespace("dc", DUBLIN_CORE_NS);
		rdfXmlWriter.handleNamespace("h", HTTP_NS);
		rdfXmlWriter.handleNamespace("tr", THREAD_NS);
		rdfXmlWriter.handleNamespace("svg", SVG_NS);
		return rdfXmlWriter;
	}

	private void addAnnotationStatements(RepositoryConnection connection, 			
			RepositoryResult<Statement> annotationStatements, URI annotationSubject) 
		throws RepositoryException {

		for (Statement statement : annotationStatements.asList()) {
			// in case we found the body we additionally add all body statements
			if (statement.getPredicate().equals(valueFactory.createURI(ANNOTATION_BODY))) {
				Resource body = null;
				try {
					body = valueFactory.createURI(statement.getObject().stringValue());
				} catch (IllegalArgumentException e) {
					// in case the body has no absolute URI
					body = valueFactory.createBNode(statement.getObject().stringValue());
				}
				connection.add(annotationSubject, statement.getPredicate(), body);

				// find all body statements
				RepositoryResult<Statement> bodyStatements = repositoryConnection.getStatements(
						valueFactory.createBNode(statement.getObject().stringValue()), 
						null, null, true);
				if (!bodyStatements.hasNext())
					bodyStatements = repositoryConnection.getStatements(
							valueFactory.createURI(statement.getObject().stringValue()), 
							null, null, true);

				for (Statement bodyStatement : bodyStatements.asList()) {
					connection.add(body, bodyStatement.getPredicate(), bodyStatement.getObject());
				}
			} else {
				connection.add(annotationSubject, statement.getPredicate(), statement.getObject());
			}
		}
	}

	private String makeValidAnnotationId(String id) {
		if (!id.startsWith(Config.getInstance().getAnnotationBaseUrl())) {
			id = Config.getInstance().getAnnotationBaseUrl() + id;
		}

		return id;
	}

	private String makeValidAnnotationBodyId(String id) {
		if (id.startsWith(Config.getInstance().getAnnotationBaseUrl())) {
			id = id.replace(Config.getInstance().getAnnotationBaseUrl(), Config.getInstance()
					.getAnnotationBodyBaseUrl());
		}

		if (!id.startsWith(Config.getInstance().getAnnotationBodyBaseUrl())) {
			id = Config.getInstance().getAnnotationBodyBaseUrl() + id;
		}

		return id;
	}

	private boolean existsAnnotation(String id) throws RepositoryException {
		if(id == null) return false;

		Resource annotationSubject = valueFactory.createBNode(makeValidAnnotationId(id));
		RepositoryResult<Statement> annotationStatements = 
			repositoryConnection.getStatements(annotationSubject, null, null, true);
		return annotationStatements.asList().size() > 0;
	}

	private boolean hasReplies(String id) throws RepositoryException, AnnotationDatabaseException {
		if (id == null) return false;

		return countAnnotationsByPredicate(valueFactory.createURI(THREAD_IN_REPLY_TO), 
				valueFactory.createURI(makeValidAnnotationId(id))) > 0;
	}

	private Connection getNativeConnection() throws Exception {
		Config config = Config.getInstance();
		Class.forName(config.getDbDriver());
		return DriverManager.getConnection(config.getDbDriverProtocol() + ":" + 
				config.getDbName(), 
				config.getDbUser(), 
				config.getDbPass());
	}
}
