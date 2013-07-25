package at.ait.dme.yuma.server.controller.rdf.oac.serialize;

import org.apache.log4j.Logger;

import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Used to add constrains-related properties to the target of an annotation.
 * 
 * @author Christian Mader
 */
public class ConstrainedTargetPropertiesAppender extends PropertiesAppender {

	private Logger logger = Logger.getLogger(ConstrainedTargetPropertiesAppender.class);
	private Model model;
	
	public ConstrainedTargetPropertiesAppender(Resource resource, Model model) {
		super(resource);
		this.model = model;
	}
	
	@Override
	void populatePropertiesMap(Annotation annotation) {
		addProperty(createConstrainsProperty(),	createConstrainedResource(annotation));
		
		try {
			addProperty(createConstrainedByProperty(), 
				ConstraintResourceFactory.getInstance().createResource(annotation, model));
		}
		catch (InvalidAnnotationException e) {
			logger.error("Could not create constrained annotation resource", e);
		}
	}
	
	private Property createConstrainsProperty() {
		return model.createProperty(OACFormatHandler.NS_OAC, "constrains");
	}
	
	private Resource createConstrainedResource(Annotation annotation) {
		Resource constrainedResource = model.createResource(annotation.getObjectUri().toString());
		constrainedResource.addProperty(RDF.type, model.createProperty(OACFormatHandler.NS_OAC, "Target"));

		return constrainedResource;
	}
	
	private Property createConstrainedByProperty() {
		return model.createProperty(OACFormatHandler.NS_OAC, "constrainedBy");
	}
}
