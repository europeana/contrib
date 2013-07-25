package at.ait.dme.yuma.server.controller.rdf.oac.parse;

import java.io.StringReader;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.MapKeys;
import at.ait.dme.yuma.server.model.MediaType;
import at.ait.dme.yuma.server.model.Scope;
import at.ait.dme.yuma.server.model.User;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

/**
 * Used to convert a serialized annotation in OAC RDF format to an annotation 
 * object compliant to the server's data model. 
 * 
 * @author Christian Mader
 */
public class OACParser {

	private HashMap<String, Object> properties = new HashMap<String, Object>();
	private Model model;
	
	private Resource mainAnnotation;
	
	public OACParser(String serialized) {
		model = ModelFactory.createDefaultModel();
		model.read(new StringReader(serialized), null);		
	}
	
	public Annotation parse() throws InvalidAnnotationException, ParseException {
		getMainAnnotationResource();
		parseBasicInfo();
		parseObjectUri();
		parseMediaType();
		parseScope();
		parseParentId();
		parseText();
		parseSemanticTags();
		
		return new Annotation(properties);
	}
	
	private void getMainAnnotationResource() {
		ResIterator it = model.listResourcesWithProperty(
			RDF.type,
			model.createProperty(OACFormatHandler.NS_OAC, "Reply"));
		
		if (!it.hasNext()) {
			it = model.listResourcesWithProperty(
				RDF.type,
				model.createProperty(OACFormatHandler.NS_OAC, "Annotation"));
		}
		
		mainAnnotation = it.next();
		assert it.hasNext() == false : "Resource must be typed either as reply or annotation, not both";
	}
		
	private void parseBasicInfo() throws ParseException {
		properties.put(MapKeys.ANNOTATION_TITLE,
			mainAnnotation.getProperty(DC.title).getString());
			
		properties.put(MapKeys.ANNOTATION_CREATED_BY,
			new User(mainAnnotation.getProperty(DC.creator).getString()));
		
		properties.put(MapKeys.ANNOTATION_CREATED,
			createDateFromString(mainAnnotation.getProperty(DCTerms.created).getString()));
		
		properties.put(MapKeys.ANNOTATION_LAST_MODIFIED,
			createDateFromString(mainAnnotation.getProperty(DCTerms.modified).getString()));
		
		properties.put(MapKeys.ANNOTATION_ROOT_ID, "");
	}
	
	private Date createDateFromString(String date) throws ParseException {
		return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy").parse(date);
	}	
	
	private void parseObjectUri() {
		properties.put(MapKeys.ANNOTATION_OBJECT_URI, findObjectUri());
	}
	
	private String findObjectUri() {
		String ret = null;
		
		NodeIterator targetIt = getTargetResources();
		while (targetIt.hasNext()) {
			RDFNode target = targetIt.next();
			if (target.isURIResource()) {
				ret = getObjectUriFromSimpleTarget(target);
			}
			else {
				ret = getObjectUriFromConstrainedTarget(target);
			}
		}
		
		return ret;
	}
	
	private NodeIterator getTargetResources() {
		return model.listObjectsOfProperty(
			model.createProperty(OACFormatHandler.NS_OAC, "hasTarget"));
	}
		
	private String getObjectUriFromConstrainedTarget(RDFNode target) {
		Statement constrainsStatement = target.asResource().getProperty(
			model.createProperty(OACFormatHandler.NS_OAC, "constrains"));
		return constrainsStatement.getObject().asLiteral().toString();
	}
	
	private String getObjectUriFromSimpleTarget(RDFNode target) {
		if (referencesAnnotation(target.toString())) {
			return null;
		}
		return target.toString();
	}
	
	private boolean referencesAnnotation(String targetUri) {
		try {
			URIBuilder.toID(targetUri);
			return true;
		}
		catch (URISyntaxException e) {
			return false;
		}
	}
	
	private void parseMediaType() {
		properties.put(MapKeys.ANNOTATION_TYPE, MediaType.IMAGE);
	}
	
	private void parseScope() {
		properties.put(MapKeys.ANNOTATION_SCOPE, Scope.PUBLIC);
	}
	
	private void parseParentId() {
		properties.put(MapKeys.ANNOTATION_PARENT_ID, findParentId());
	}
	
	private String findParentId() {
		NodeIterator targetIt = getTargetResources();
		while (targetIt.hasNext()) {
			RDFNode target = targetIt.next();
			if (target.isLiteral()) {
				if (referencesAnnotation(target.asLiteral().toString())) {
					return target.asLiteral().toString();
				}
			}
		}
		return "";
	}

	private void parseText() {
		properties.put(MapKeys.ANNOTATION_TEXT, findText());
	}
	
	private String findText() {
		Resource bodyResource = getBodyResource();
		Statement labelStatement = bodyResource.getProperty(RDFS.label);
		return labelStatement.getObject().asLiteral().toString();
	}
	
	private Resource getBodyResource() {
		NodeIterator bodyIt = model.listObjectsOfProperty(
			model.createProperty(OACFormatHandler.NS_OAC, "hasBody"));
		return bodyIt.next().asResource();
	}
	
	private void parseSemanticTags() throws InvalidAnnotationException {
		properties.put(
			MapKeys.ANNOTATION_SEMANTIC_TAGS, 
			new SemanticTagParser(getBodyResource()).getSemanticTags());
	}
}
