package at.researchstudio.dme.imageannotation.server.annotation.builder;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.openrdf.model.Resource;
import org.openrdf.model.Statement;
import org.openrdf.model.Value;
import org.openrdf.rio.RDFHandler;
import org.openrdf.rio.RDFHandlerException;

import at.researchstudio.dme.imageannotation.client.annotation.ImageAnnotation;
import at.researchstudio.dme.imageannotation.client.image.ImageFragment;
import at.researchstudio.dme.imageannotation.server.util.W3CDateTimeParser;

/**
 * rdf handler used to parse rdf/xml for image annotations
 * 
 * @author Christian Sadilek
 */
public class RdfXmlAnnotationHandler implements RDFHandler {
	private static Logger logger = Logger.getLogger(RdfXmlAnnotationHandler.class);
	
	// all root annotations
	private ArrayList<ImageAnnotation> rootAnnotations = new ArrayList<ImageAnnotation>();
	
	// maps all replies to an annotation
	private Map<String, ArrayList<ImageAnnotation>> allReplies = 
		new HashMap<String, ArrayList<ImageAnnotation>>();	

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
		for(String annotationId : allReplies.keySet()) {
			for(ImageAnnotation reply : allReplies.get(annotationId)) {
				rootAnnotations.add(reply);
			}
		}
	}
	
	/**
	 * add replies to the given annotation
	 */
	private void addAnnotationReplies(Collection<ImageAnnotation> annotations) {
		for(ImageAnnotation annotation : annotations) {
			ArrayList<ImageAnnotation> replies = allReplies.remove(annotation.getId());
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
		ImageAnnotation annotation = new ImageAnnotation();
		annotation.setId(currentResource.stringValue());
		
		Value objectId = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_ANNOTATES);
		if(objectId!=null) annotation.setObjectId(objectId.stringValue());
	
		Value extObjectId = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_LINKED_TO);
		if(extObjectId!=null) annotation.setExternalObjectId(extObjectId.stringValue());
	
		Value inReplyTo=currentStatements.get(RdfXmlAnnotationBuilder.THREAD_IN_REPLY_TO);
		if(inReplyTo!=null) {
			annotation.setParentId(inReplyTo.stringValue());
		
			ArrayList<ImageAnnotation> replies=allReplies.get(inReplyTo.stringValue());
			if(replies==null) replies = new ArrayList<ImageAnnotation>();
			replies.add(annotation);
			allReplies.put(annotation.getParentId(), replies);
		} else {
			rootAnnotations.add(annotation);
		}
		
		Value root=currentStatements.get(RdfXmlAnnotationBuilder.THREAD_ROOT);
		if(root!=null) annotation.setRootId(root.stringValue());

		Value title = currentStatements.get(RdfXmlAnnotationBuilder.DUBLIN_CORE_TITLE);
		if(title!=null) annotation.setTitle(StringEscapeUtils.unescapeXml(title.stringValue()));
		
		Value format = currentStatements.get(RdfXmlAnnotationBuilder.DUBLIN_CORE_FORMAT);
		if(format!=null) annotation.setMimeType(format.stringValue());

		Value scope = currentStatements.get(RdfXmlAnnotationBuilder.ANNOTATION_SCOPE);
		if(scope!=null) annotation.setScopeFromString(scope.stringValue());

		Value body = currentStatements.get(RdfXmlAnnotationBuilder.HTTP_BODY);
		if(body!=null) {
			try {	
				Document document = new SAXBuilder().build(new StringReader(body.stringValue()));
				Element htmlBody = document.getRootElement().getChild("body");
				if(htmlBody!=null) 
					annotation.setText(StringEscapeUtils.unescapeXml(htmlBody.getText()));
			} catch (Exception e) {
				logger.error("failed to parse annotation body", e);
			}
		}
		
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
			ImageFragment annotatedFragment=
				RdfXmlAnnotationBuilder.parseSvgFragment(fragment.stringValue());
			annotation.setAnnotatedFragment(annotatedFragment);
		}
		
		for(Value link : links) annotation.addLink(link.stringValue());		
	}
	
	/**
	 * return the list of parsed annotations.
	 * 
	 * @return collection of annotation
	 */
	public Collection<ImageAnnotation> getAnnotations() {
		return rootAnnotations;
	}
}
