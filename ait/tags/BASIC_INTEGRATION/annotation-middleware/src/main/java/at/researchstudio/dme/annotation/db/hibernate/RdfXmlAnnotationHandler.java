package at.researchstudio.dme.annotation.db.hibernate;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.jdom.JDOMException;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import at.researchstudio.dme.annotation.config.Config;


/**
 * rdf handler used to parse rdf/xml for annotations
 * 
 * @author Christian Sadilek
 */
public class RdfXmlAnnotationHandler implements RDFHandler {
	private static Logger logger = Logger.getLogger(RdfXmlAnnotationHandler.class);
	
	// all root annotations
	private List<Annotation> rootAnnotations = new ArrayList<Annotation>();
	
	// maps all replies to an annotation
	private Map<Long, ArrayList<Annotation>> allReplies = 
		new HashMap<Long, ArrayList<Annotation>>();	

	// the current resource and statements to be processed
	private Resource currentResource = null;	
	private Map<String, Value> currentStatements = null;
	private ArrayList<Value> links = null;
		
	@Override
	public void startRDF() throws RDFHandlerException {}
	
	@Override
	public void endRDF() throws RDFHandlerException {
		try {
			createAnnotationTree();
		} catch (Exception e) {
			throw new RDFHandlerException(e);
		}
	}

	@Override
	public void handleComment(String comment) throws RDFHandlerException {}

	@Override
	public void handleNamespace(String prefix, String uri) throws RDFHandlerException {}

	@Override
	public void handleStatement(Statement statement) throws RDFHandlerException {
		try {
			// we found a new annotation when we find a new resource that does not describe
			// the body of an annotation (the body could be an embedded resource and is described
			// using predicates from http://www.w3.org/2000/10/annotation-ns#, 
			// see also http://www.w3.org/2001/Annotea/User/Protocol.html#PostABody)
			if(currentResource==null ||
				(currentResource!=null && !currentResource.equals(statement.getSubject()) &&
				!statement.getPredicate().getNamespace().equals(RdfXmlAnnotationBuilder.HTTP_NS))) { 
				
				if(currentResource!=null) createAnnotation();
				
				currentResource = statement.getSubject();
				currentStatements = new HashMap<String, Value>();
				links = new ArrayList<Value>();
			}
			
			if(statement.getPredicate().toString().equals(RdfXmlAnnotationBuilder.RDFS_SEEALSO)) {
				if(links==null) links = new ArrayList<Value>();	
				links.add(statement.getObject());			
			}
			
			currentStatements.put(statement.getPredicate().toString(), statement.getObject());			
		} catch (Exception e) {
			throw new RDFHandlerException(e);
		}
	}
	
	/**
	 * create an annotation tree based on the parsed annotations.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createAnnotationTree() throws JDOMException, IOException, ParseException {
		if(currentResource!=null)
			createAnnotation();

		if(rootAnnotations!=null)
			addAnnotationReplies(rootAnnotations);
		
		// we add all remaining replies to the root level because we did not
		// find the annotation they reply to.
		for(Long annotationId : allReplies.keySet()) {
			for(Annotation reply : allReplies.get(annotationId)) {
				rootAnnotations.add(reply);
			}
		}
	}
	
	/**
	 * add replies to the given annotation
	 */
	private void addAnnotationReplies(Collection<Annotation> annotations) {
		for(Annotation annotation : annotations) {
			ArrayList<Annotation> replies = allReplies.remove(annotation.getId());
			if(replies!=null) {
				annotation.setReplies(replies);
				addAnnotationReplies(replies);
			}
		}			
	}
	
	/**
	 * create an annotation.
	 * 
	 * @throws JDOMException
	 * @throws IOException
	 * @throws ParseException
	 */
	private void createAnnotation() throws JDOMException, IOException, ParseException {
		Annotation annotation = new Annotation();
		if(!currentResource.stringValue().contains("node"))
			annotation.setId(readAnnotationId(currentResource.stringValue()));
		
		Value objectId = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_ANNOTATES);
		if(objectId!=null) annotation.setObjectUrl(objectId.stringValue());
	
		Value extObjectId = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_LINKED_TO);
		if(extObjectId!=null) annotation.setExternalObjectId(extObjectId.stringValue());
	
		Value inReplyTo=currentStatements.get(RdfXmlAnnotationBuilder.THREAD_IN_REPLY_TO);
		if(inReplyTo!=null) {
			annotation.setParentId(readAnnotationId(inReplyTo.stringValue()));
		
			ArrayList<Annotation> replies=allReplies.get(inReplyTo.stringValue());
			if(replies==null) replies = new ArrayList<Annotation>();
			replies.add(annotation);
			allReplies.put(annotation.getParentId(), replies);
		} else {
			rootAnnotations.add(annotation);
		}
		
		Value root=currentStatements.get(RdfXmlAnnotationBuilder.THREAD_ROOT);
		if(root!=null) annotation.setRootId(readAnnotationId(root.stringValue()));

		Value title = currentStatements.get(RdfXmlAnnotationBuilder.DUBLIN_CORE_TITLE);
		if(title!=null) annotation.setTitle(title.stringValue());
		
		Value format = currentStatements.get(RdfXmlAnnotationBuilder.DUBLIN_CORE_FORMAT);
		if(format!=null) annotation.setMimeType(format.stringValue());

		Value scope = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_SCOPE);
		if(scope!=null) annotation.setScope(
				Annotation.Scope.valueOf(scope.stringValue().toUpperCase()));

		Value label = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_LABEL);
		if(label!=null) annotation.setText(label.stringValue());
		
		Value creator = currentStatements.get(RdfXmlAnnotationBuilder.DUBLIN_CORE_CREATOR);
		if(creator!=null) annotation.setCreatedBy(creator.stringValue());

		Value created = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_CREATED);
		if(created!=null) {
			annotation.setCreated(W3CDateTimeParser.parseW3CDateTime(created.stringValue()));
		}
		
		Value modified = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_MODIFIED);
		if(modified!=null) {
			annotation.setModified(W3CDateTimeParser.parseW3CDateTime(modified.stringValue()));
		}
		
		Value fragment = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_IMAGE_FRAGMENT);
		if(fragment!=null) {
			annotation.setAnnotatedFragment(fragment.stringValue());
		}
		
		for(Value link : links) annotation.addLink(link.stringValue());		
	}
	
	/**
	 * return the list of parsed annotations.
	 * 
	 * @return collection of annotation
	 */
	public List<Annotation> getAnnotations() {
		return rootAnnotations;
	}
	
	/**
	 * read annotation id from annotation url
	 * 
	 * @param url
	 * @return annotation id
	 */
	private static Long readAnnotationId(String url) {
		return Long.parseLong(url.replace(Config.getInstance().getAnnotationBaseUrl(), ""));
	}

}
