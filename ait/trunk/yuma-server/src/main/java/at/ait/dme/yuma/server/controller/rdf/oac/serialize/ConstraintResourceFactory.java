package at.ait.dme.yuma.server.controller.rdf.oac.serialize;

import at.ait.dme.yuma.server.controller.rdf.RDFFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.MediaType;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;

/**
 * Used to obtain resources that constrain an annotation.
 * 
 * @author chef
 */
class ConstraintResourceFactory {

	private static final String NS_CNT = "http://www.w3.org/2008/content#";
	private static ConstraintResourceFactory instance;

	private Annotation annotation;
	private Model model;
	
	private ConstraintResourceFactory() {
		// constructor only called from within this class
	}
	
	static ConstraintResourceFactory getInstance() {
		if (instance == null) {
			instance = new ConstraintResourceFactory();
		}
		return instance;
	}
	
	Resource createResource(Annotation annotation, Model model) throws InvalidAnnotationException {
		MediaType mediaType = annotation.getType();
		
		this.annotation = annotation;
		this.model = model;
		
		switch (mediaType) {
		case IMAGE:
		case MAP:
			return createSvgConstraint();
		}
		throw new InvalidAnnotationException("don't know how to handle media type '" +mediaType.toString()+ "'");
	}
	
	private Resource createSvgConstraint() {
		Resource ret = model.createResource(
			RDFFormatHandler.createSvgConstraintUri(annotation.getAnnotationID()));
			
		ret.addProperty(
			RDF.type, 
			model.createProperty(OACFormatHandler.NS_OAC, "SvgConstraint"));
		ret.addProperty(DC.format, "image/svg+xml");
		
		ret.addProperty(
			RDF.type, 
			model.createProperty(NS_CNT, "ContentAsText"));

		Property chars = model.createProperty(NS_CNT, "chars");
		Property charEncoding = model.createProperty(NS_CNT, "characterEncoding");
		
		ret.addProperty(chars, annotation.getFragment());
		ret.addProperty(charEncoding, "utf-8");
		return ret;
	}
}
