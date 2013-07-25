package at.ait.dme.yuma.server.controller.rdf.oac.parse;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import at.ait.dme.yuma.server.exception.InvalidAnnotationException;
import at.ait.dme.yuma.server.model.tag.SemanticRelation;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

class SemanticTagParser {

	private final Property[] NON_SEMANTIC_TAG_PROPERTIES = {RDFS.label, RDF.type};
	private final String SEM_TAG_PRIM_LABEL = "sem_tag_from_oac";
	private final String SEM_TAG_PRIM_DESC = "semantic tag deserialized from open annotation format";
	
	private Resource bodyResource;
	
	SemanticTagParser(Resource bodyResource) {
		this.bodyResource = bodyResource;
	}
	
	List<SemanticTag> getSemanticTags() throws InvalidAnnotationException 
	{
		List<Statement> semanticTagStatements = findSemanticTagStatements();
		return createSemanticTags(semanticTagStatements);
	}
	
	private List<Statement> findSemanticTagStatements() {
		List<Statement> ret = new ArrayList<Statement>();
		
		StmtIterator statementIt = bodyResource.listProperties();
		while (statementIt.hasNext()) {
			Statement statement = statementIt.next();
			if (statementIsSemanticTag(statement)) {
				ret.add(statement);
			}
		}
		return ret;
	}
	
	private boolean statementIsSemanticTag(Statement statement) {
		Property property = statement.getPredicate();
		List<Property> nonSemanticTagPropertiesList = Arrays.asList(NON_SEMANTIC_TAG_PROPERTIES);
		
		return !nonSemanticTagPropertiesList.contains(property);
	}
	
	private List<SemanticTag> createSemanticTags(List<Statement> statements) throws 
		InvalidAnnotationException 
	{
		List<SemanticTag> ret = new ArrayList<SemanticTag>();
		for (Statement statement : statements) {
			try {
				ret.add(createSemanticTag(statement));
			}
			catch (URISyntaxException e) {
				throw new RuntimeException("Invalid URI for semantic tag from OAC annotation", e);
			}
		}
		return ret;
	}
	
	private SemanticTag createSemanticTag(Statement statement) throws 
		InvalidAnnotationException, URISyntaxException 
	{
		String uri = statement.getObject().toString();
		String nameSpace = statement.getPredicate().getNameSpace();
		String property = statement.getPredicate().toString();
		
		SemanticRelation semRelation = new SemanticRelation(nameSpace, property);
		SemanticTag ret = new SemanticTag(
			new URI(uri), SEM_TAG_PRIM_LABEL, SEM_TAG_PRIM_DESC); 
		ret.setSemanticRelation(semRelation);
		
		return ret;
	}
	
}
