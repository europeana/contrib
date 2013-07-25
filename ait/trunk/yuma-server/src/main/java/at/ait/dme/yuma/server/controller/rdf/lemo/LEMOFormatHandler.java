package at.ait.dme.yuma.server.controller.rdf.lemo;

import java.io.StringReader;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.DC;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import at.ait.dme.yuma.server.URIBuilder;
import at.ait.dme.yuma.server.controller.rdf.RDFFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.MapKeys;
import at.ait.dme.yuma.server.model.MediaType;
import at.ait.dme.yuma.server.model.Scope;
import at.ait.dme.yuma.server.model.User;
import at.ait.dme.yuma.server.model.tag.SemanticRelation;
import at.ait.dme.yuma.server.model.tag.SemanticTag;


/**
 * Format handler for LEMO RDF format (in different serialization
 * languages).
 * 
 * TODO Finish this!
 * 
 * @author Rainer Simon
 */
public class LEMOFormatHandler extends RDFFormatHandler {
	
	private static final String NS_LEMO_CORE = "http://lemo.mminf.univie.ac.at/annotation-core#";
	private static final String NS_LEMO_IMAGE = "http://lemo.mminf.univie.ac.at/annotation-image#";
	private static final String NS_SCOPE = "http://lemo.mminf.univie.ac.at/ann-tel#";

	/**
	 * Creates a LEMO RDF format handler with the default
	 * serialization (RDF/XML)
	 */
	public LEMOFormatHandler() {
		this(SerializationLanguage.RDF_XML);
	}
	
	/**
	 * Creates a LEMO RDF format handler with the specified
	 * RDF serialization language
	 * @param language the language
	 */
	public LEMOFormatHandler(SerializationLanguage language) {
		super(language);
	}
	
	@Override
	public Annotation parse(String serialized)
		throws InvalidAnnotationException {
		
		Model m = ModelFactory.createDefaultModel();
		m.read(new StringReader(serialized), null);
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		
		ResIterator it =
			m.listResourcesWithProperty(m.createProperty(NS_LEMO_CORE, "annotates"));
		
		if (it.hasNext()) {
			Resource a = it.next();
			
			try {
				properties.put(MapKeys.ANNOTATION_ID,
					URIBuilder.toID(a.getURI()));
			}
			catch (URISyntaxException e) {
				throw new InvalidAnnotationException(e);
			}
			
			properties.put(MapKeys.ANNOTATION_OBJECT_URI, 
					a.getProperty(m.createProperty(NS_LEMO_CORE, "annotates")).getString());
			
			properties.put(MapKeys.ANNOTATION_CREATED_BY,
					new User(a.getProperty(DC.creator).getString()));
			
			properties.put(MapKeys.ANNOTATION_CREATED,
					new Date());
			
			properties.put(MapKeys.ANNOTATION_LAST_MODIFIED,
					new Date());
			
			properties.put(MapKeys.ANNOTATION_TYPE,
					MediaType.IMAGE);
			
			properties.put(MapKeys.ANNOTATION_SCOPE,
					Scope.valueOf(a.getProperty(m.createProperty(NS_SCOPE, "scope")).getString()));
			
			properties.put(MapKeys.ANNOTATION_TITLE,
					a.getProperty(DC.title).getString());
			
			properties.put(MapKeys.ANNOTATION_TEXT,
					a.getProperty(m.createProperty(NS_LEMO_CORE, "label")).getString());	
		}
				
		return new Annotation(properties);
	}

	@Override
	public String serialize(Annotation a) {
		Model m = ModelFactory.createDefaultModel();

		addRDFResource(a, m);
		
		return toString(m);
	}

	@Override
	public String serialize(List<Annotation> annotations) {
		Model m = ModelFactory.createDefaultModel();
		
		for (Annotation a : annotations) {
			addRDFResource(a, m);
		}

		return toString(m);
	}
	
	protected void addRDFResource(Annotation a, Model m) {
		m.setNsPrefix("a", NS_LEMO_CORE);
		m.setNsPrefix("image", NS_LEMO_IMAGE);
		m.setNsPrefix("scope", NS_SCOPE);
		
		Resource annotation = m.createResource(URIBuilder.toURI(a.getAnnotationID()).toString());		
		
		annotation.addProperty(m.createProperty(NS_LEMO_CORE, "annotates"), a.getObjectUri());
		annotation.addProperty(RDF.type, m.createProperty(NS_LEMO_CORE, "Annotation"));
		
		if (a.getTitle() != null)
			annotation.addProperty(DC.title, a.getTitle());
		
		annotation.addProperty(DC.creator, a.getCreatedBy().getUsername());
		annotation.addProperty(m.createProperty(NS_LEMO_CORE, "created"), a.getCreated().toString());
		annotation.addProperty(m.createProperty(NS_LEMO_CORE, "modified"), a.getLastModified().toString());
		
		if (a.getText() != null)
			annotation.addProperty(m.createProperty(NS_LEMO_CORE, "label"), a.getText());
		
		annotation.addProperty(m.createProperty(NS_SCOPE, "scope"), a.getScope().name());
		
		if (a.getFragment() != null)
			annotation.addProperty(m.createProperty(NS_LEMO_IMAGE, "svg-fragment"), m.createLiteral(a.getFragment(), true));
		
		if (a.getTags() != null) {
			for (SemanticTag t : a.getTags()) {
				SemanticRelation r = t.getRelation();
				if (r == null) {
					// Default to rdfs:seeAlso
					annotation.addProperty(RDFS.seeAlso, t.getURI().toString());
				} else {
					annotation.addProperty(m.createProperty(r.getNamespace(), r.getProperty()), t.getURI().toString());
				}
			}
		}
	}
}
