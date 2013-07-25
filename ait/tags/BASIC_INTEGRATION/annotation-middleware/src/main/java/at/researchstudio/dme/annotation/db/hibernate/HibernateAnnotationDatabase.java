package at.researchstudio.dme.annotation.db.hibernate;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.LockModeType;
import javax.persistence.OptimisticLockException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.openrdf.OpenRDFException;

import at.researchstudio.dme.annotation.config.Config;
import at.researchstudio.dme.annotation.db.AnnotationDatabase;
import at.researchstudio.dme.annotation.exception.AnnotationAlreadyReferencedException;
import at.researchstudio.dme.annotation.exception.AnnotationDatabaseException;
import at.researchstudio.dme.annotation.exception.AnnotationException;
import at.researchstudio.dme.annotation.exception.AnnotationModifiedException;
import at.researchstudio.dme.annotation.exception.AnnotationNotFoundException;

/**
 * implementation of the annotation database using Hibernate/JPA.
 * 
 * @author Christian Sadilek
 */
public class HibernateAnnotationDatabase extends AnnotationDatabase {
	private static Logger logger = Logger.getLogger(HibernateAnnotationDatabase.class);
	
	private static EntityManagerFactory emf;		
	private EntityManager em = null;
	 		
	@Override
	public synchronized void init() throws AnnotationDatabaseException {
		final Config config = Config.getInstance();
		Map<String, String> emfProperties = new HashMap<String, String>() {{
			put("javax.persistence.provider", "org.hibernate.ejb.HibernatePersistence");
			put("hibernate.hbm2ddl.auto", "update");	
			put("hibernate.show_sql","false");
			put("hibernate.connection.driver_class", config.getDbDriver());
			put("hibernate.connection.username", config.getDbUser());
			put("hibernate.connection.password", config.getDbPass());
			put("hibernate.connection.url", config.getDbDriverProtocol() + "://"
				+ config.getDbHost() + ":" + config.getDbPort() + "/" + config.getDbName());
		}};
		
		emf = Persistence.createEntityManagerFactory("annotation", emfProperties);
	}

	@Override
	public void shutdown() {
		if(emf!=null) emf.close();
	}
	
	@Override
	public void connect(HttpServletRequest request) throws AnnotationDatabaseException {
		if(emf==null) 
			throw new AnnotationDatabaseException("entity manager factory not initialized");
		
		em=emf.createEntityManager();		
	}
	
	@Override
	public void disconnect() {
		if(em!=null) em.close();		
	}
	
	@Override
	public void commit() throws AnnotationDatabaseException {
		if(em==null||em.getTransaction()==null) 
			throw new AnnotationDatabaseException("no transaction to commit");
		
		em.getTransaction().commit();	
	}

	@Override
	public void rollback() throws AnnotationDatabaseException {
		if(em==null||em.getTransaction()==null) 
			throw new AnnotationDatabaseException("no transaction to rollback");
		
		em.getTransaction().rollback();			
	}

	@Override
	public URI createAnnotation(String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationModifiedException {
		try {
			Annotation annEntity = RdfXmlAnnotationBuilder.fromRdfXml(annotation);
			if(!em.getTransaction().isActive()) em.getTransaction().begin();
			
			// in case of a reply we have to ensure that the parent is unchanged
			if(annEntity.getParentId()!=null) {
				// an annotation gets a new id on every update. therefore, checking for
				// existence is sufficient here.			
				Annotation parent=em.find(Annotation.class, annEntity.getParentId());				
				if(parent==null) throw new AnnotationModifiedException(annEntity.getParentId());

				// parent is unchanged, lock it and make sure it wasn't modified concurrently
				try {
					em.lock(parent, LockModeType.WRITE);
				} catch(OptimisticLockException ole) {
					throw new AnnotationModifiedException(annEntity.getParentId());
				}				
				em.refresh(parent);				
			}
			
			em.persist(annEntity);														
			if(isAutoCommit()) commit();
			
			return new URI(Config.getInstance().getAnnotationBaseUrl()+annEntity.getId());
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		} catch (IOException ioe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, ioe);
			throw new AnnotationDatabaseException(ioe);	
		} catch(AnnotationModifiedException ame) {
			throw ame;
		} catch(Throwable t) {
			rollback();
			logger.error(FAILED_TO_SAVE_ANNOTATION + ":" + t.getMessage(), t);
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public URI updateAnnotation(String id, String annotation) throws AnnotationDatabaseException,
			AnnotationException, AnnotationAlreadyReferencedException {
		boolean autoCommit = isAutoCommit();
		setAutoCommit(false);
		try {
			Annotation annEntity = RdfXmlAnnotationBuilder.fromRdfXml(annotation);
			annEntity.setId(null);
			
			if(!em.getTransaction().isActive()) em.getTransaction().begin();
			deleteAnnotation(id);
			em.persist(annEntity);
			
			if(autoCommit) commit();
			return new URI(Config.getInstance().getAnnotationBaseUrl()+annEntity.getId());
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		} catch (IOException ioe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, ioe);
			throw new AnnotationDatabaseException(ioe);	
		} catch(AnnotationAlreadyReferencedException aare) {
			throw aare;
		} catch(Throwable t) {
			rollback();
			logger.error(FAILED_TO_SAVE_ANNOTATION + ":" + t.getMessage(), t);
			throw new AnnotationDatabaseException(t);
		} finally {
			setAutoCommit(autoCommit);
		}
	}
	
	@Override
	public void deleteAnnotation(String id) throws AnnotationDatabaseException,
			AnnotationException, AnnotationAlreadyReferencedException {
		try {
			Long aId = Long.parseLong(id.replace(Config.getInstance().getAnnotationBaseUrl(), ""));		
			if(!em.getTransaction().isActive()) em.getTransaction().begin();					
			
			Annotation annEntity=em.find(Annotation.class, aId);						
			em.lock(annEntity, LockModeType.WRITE);			
			em.refresh(annEntity);
			if(!annEntity.getReplies().isEmpty())
				throw new AnnotationAlreadyReferencedException(id);
			
			em.remove(annEntity);			
			if(isAutoCommit()) commit();
		} catch(AnnotationAlreadyReferencedException aare) {
			rollback();
			throw aare;
		} catch (Throwable t) {
			rollback();
			logger.error(FAILED_TO_DELETE_ANNOTATION + ":" + t.getMessage(), t);
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String findAnnotationBodyById(String id) throws AnnotationDatabaseException,
			AnnotationNotFoundException, AnnotationException {
		try {
			Long aId = Long.parseLong(id.replace(Config.getInstance().getAnnotationBaseUrl(), ""));
			Annotation annEntity=em.find(Annotation.class, aId);
			if(annEntity==null) throw new AnnotationNotFoundException(id);
		
			return annEntity.toHtml();
		} catch(AnnotationNotFoundException anfe) {
			throw anfe;
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String findAnnotationById(String id) throws AnnotationDatabaseException,
			AnnotationNotFoundException, AnnotationException {
		
		try {
			Long aId = Long.parseLong(id.replace(Config.getInstance().getAnnotationBaseUrl(), ""));
			Annotation annEntity=em.find(Annotation.class, aId);
			if(annEntity==null) throw new AnnotationNotFoundException(id);
			
			return RdfXmlAnnotationBuilder.toRdfXml(annEntity);					
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		}  catch (IOException ioe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, ioe);
			throw new AnnotationDatabaseException(ioe);
		} catch(AnnotationNotFoundException anfe) {
			throw anfe;
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String findAnnotations(String term) throws AnnotationDatabaseException, 
			AnnotationException {
		try {
			Query query = em.createNamedQuery("annotation.search");
			query.setParameter("term", term.toLowerCase());
			
			List<Annotation> allReplies=query.getResultList();
			return RdfXmlAnnotationBuilder.toRdfXml(allReplies);	
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		}  catch (IOException ioe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, ioe);
			throw new AnnotationDatabaseException(ioe);
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String listAnnotationReplies(String id) throws AnnotationDatabaseException,
			AnnotationException {
		try {
			Long annId = Long.parseLong(id.replace(Config.getInstance().getAnnotationBaseUrl(), ""));
			Query query = em.createNamedQuery("annotation.subtree");
			query.setParameter("rootId", annId);
			
			List<Annotation> allReplies=query.getResultList();
			return RdfXmlAnnotationBuilder.toRdfXml(allReplies);	
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		}  catch (IOException ioe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, ioe);
			throw new AnnotationDatabaseException(ioe);
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String listAnnotations(String objectUrl) throws AnnotationDatabaseException,
			AnnotationException {
		try {
			Query query = em.createNamedQuery("annotation.list");
			query.setParameter("objectUrl", objectUrl);
		
			List<Annotation> allAnnotations = query.getResultList();
			return RdfXmlAnnotationBuilder.toRdfXml(allAnnotations);
		} catch (IOException e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}

	@Override
	public String listLinkedAnnotations(String linkedObjectId) throws AnnotationDatabaseException,
			AnnotationException {
		try {
			Query query = em.createNamedQuery("annotation.list.linked");
			query.setParameter("id", linkedObjectId);
		
			List<Annotation> allAnnotations = query.getResultList();
			return RdfXmlAnnotationBuilder.toRdfXml(allAnnotations);
		} catch (IOException e) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, e);
			throw new AnnotationDatabaseException(e);
		} catch (OpenRDFException rdfe) {
			logger.error(FAILED_TO_PARSE_ANNOTATION, rdfe);
			throw new AnnotationException(rdfe);
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
	}
	
	@Override
	public int countAnnotations(String objectUrl) throws AnnotationDatabaseException,
			AnnotationException {
		
		int count = 0;
		try {
			Query query = em.createNamedQuery("annotation.count");
			query.setParameter("objectUrl", objectUrl);
			count = ((Long) query.getSingleResult()).intValue();
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
		return count;
	}

	@Override
	public int countLinkedAnnotations(String linkedObjectId) throws AnnotationDatabaseException,
			AnnotationException {
		int count = 0;
		try {
			Query query = em.createNamedQuery("annotation.count.linked");
			query.setParameter("id", linkedObjectId);
			count = ((Long) query.getSingleResult()).intValue();
		} catch(Throwable t) {
			throw new AnnotationDatabaseException(t);
		}
		return count;
	}
}
