package at.ait.dme.yuma.server.controller.rdf.oac;

import java.text.ParseException;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.controller.rdf.RDFFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.controller.rdf.oac.parse.OACParser;
import at.ait.dme.yuma.server.controller.rdf.oac.serialize.AnnotationPropertiesAppender;
import at.ait.dme.yuma.server.controller.rdf.oac.serialize.BodyPropertiesAppender;
import at.ait.dme.yuma.server.controller.rdf.oac.serialize.ConstrainedTargetPropertiesAppender;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Format handler for OAC RDF(in different serialization
 * languages).
 * 
 * @author Christian Mader
 */
public class OACFormatHandler extends RDFFormatHandler {
	
	public static final String NS_OAC = "http://www.openannotation.org/ns/";
	
	protected Annotation annotation;
	protected Model model;
	
	public OACFormatHandler() {
		super(SerializationLanguage.RDF_XML);
	}
	
	public OACFormatHandler(SerializationLanguage format) {
		super(format);
	}

	@Override
	public Annotation parse(String serialized) throws InvalidAnnotationException 
	{
		try {
			return new OACParser(serialized).parse();
		}
		catch (ParseException e) {
			throw new InvalidAnnotationException("Error parsing elements", e);
		}	
	}

	@Override
	protected void addRDFResource(Annotation annotation, Model model) {
		initResourceCreation(annotation, model);
		
		Resource body = createBodyResource();
		createAnnotationResource(body);
	}
	
	protected void addBodyNode(Annotation annotation, Model model) {
		initResourceCreation(annotation, model);
		createBodyResource();
	}
	
	private void initResourceCreation(Annotation annotation, Model model) {
		this.annotation = annotation;
		this.model = model;
		
		model.setNsPrefix("oac", NS_OAC);		
	}
		
	protected Resource createBodyResource() {
		Resource body = model.createResource(createBodyUri(annotation.getAnnotationID()));
		
		body.addProperty(RDF.type, model.createProperty(NS_OAC, "Body"));
		new BodyPropertiesAppender(body, model).appendProperties(annotation);
		
		return body;
	}
	
	private Resource createAnnotationResource(Resource body) {
		Resource ret = model.createResource(
			URIBuilder.toURI(annotation.getAnnotationID()).toString());
		
		addBasicProperties(ret, body);
		new AnnotationPropertiesAppender(ret).appendProperties(annotation);
		return ret;
	}
	
	private void addBasicProperties(Resource annotResource, Resource body)
	{
		if (isReplyAnnotation()) {
			annotResource.addProperty(RDF.type, model.createProperty(NS_OAC, "Reply"));
			addReplyTargets(annotResource);
		}
		else {
			annotResource.addProperty(RDF.type, model.createProperty(NS_OAC, "Annotation"));
			addSingleTarget(annotResource);
		}
		
		annotResource.addProperty(
			model.createProperty(NS_OAC, "hasBody"), 
			body);
	}
	
	private boolean isReplyAnnotation() {
		return annotation.getParentId() != null && !annotation.getParentId().isEmpty();
	}
	
	private void addReplyTargets(Resource annotationResource) {
		annotationResource.addProperty(
			model.createProperty(NS_OAC, "hasTarget"),
			URIBuilder.toURI(annotation.getParentId()).toString());
		
		if (annotation.getFragment() != null) {
			annotationResource.addProperty(
				model.createProperty(NS_OAC, "hasTarget"),
				createConstrainedTarget());	
		}		
	}
	
	private void addSingleTarget(Resource annotationResource) {
		annotationResource.addProperty(
			model.createProperty(NS_OAC, "hasTarget"),
			createTarget());		
	}
	
	private Resource createTarget() {
		if (annotation.getFragment() == null) {
			return createNonConstrainedTarget();
		}
		else {
			return createConstrainedTarget();
		}
	}
	
	private Resource createNonConstrainedTarget() {
		Resource target = model.createResource(annotation.getObjectUri().toString());
		target.addProperty(RDF.type, model.createProperty(NS_OAC, "Target"));
		return target;
	}
	
	private Resource createConstrainedTarget() {
		Resource target = model.createResource(createConstrainedTargetUri(annotation.getAnnotationID()));
		
		target.addProperty(RDF.type, model.createProperty(NS_OAC, "ConstrainedTarget"));
		new ConstrainedTargetPropertiesAppender(target, model).appendProperties(annotation);
		
		return target;
	}
}
