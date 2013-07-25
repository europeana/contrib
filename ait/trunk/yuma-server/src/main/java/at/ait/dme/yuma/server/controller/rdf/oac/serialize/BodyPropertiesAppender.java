package at.ait.dme.yuma.server.controller.rdf.oac.serialize;

import java.util.List;

import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.tag.SemanticRelation;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Used to add properties to the body of an annotation.
 * 
 * @author Christian Mader
 */
public class BodyPropertiesAppender extends PropertiesAppender {

	private Model model;
	
	public BodyPropertiesAppender(Resource resource, Model model) {
		super(resource);
		this.model = model;
	}
	
	@Override
	void populatePropertiesMap(Annotation annotation) {
		addProperty(RDFS.label, annotation.getText());
		appendSemanticTags(annotation);
	}

	private void appendSemanticTags(Annotation annotation) {
		List<SemanticTag> semanticTags = annotation.getTags();
		
		if (semanticTags == null)
			return;
			
		for (SemanticTag semanticTag : semanticTags) {
			SemanticRelation r = semanticTag.getRelation();
			
			if (r == null) {
				addProperty(
					RDFS.seeAlso, 
					model.createResource(semanticTag.getURI().toString()));
			} 
			else {
				addProperty(model.createProperty(
					r.getNamespace(), 
					r.getProperty()), 
					semanticTag.getURI().toString());				
			}
		}
	}
	
}
