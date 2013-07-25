package at.ait.dme.yuma.suite.apps.core.server.ner;

import java.util.ArrayList;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import at.ait.dme.yuma.suite.apps.core.shared.model.PlainLiteral;
import at.ait.dme.yuma.suite.apps.core.shared.model.SemanticTag;

/**
 * A simple parser for DBpedia entities. (Currently supports JSON
 * format only.)
 * 
 * @author Rainer Simon
 */
public class DBpediaEntityParser {
	
	/**
	 * String constants
	 */
	private static final String PRIMARY_LANG = "en";
	
	private static final String URI_LABELS = "http://www.w3.org/2000/01/rdf-schema#label";
	private static final String URI_ABSTRACTS = "http://dbpedia.org/ontology/abstract";
	
	private static final String KEY_TYPE = "type";
	private static final String KEY_VALUE = "value";	
	private static final String KEY_LANG = "lang";
	
	private static final String LITERAL = "literal";
	
	public SemanticTag parseJsonEntity(JSONObject json) {
		SemanticTag t = new SemanticTag();
		t.setPrimaryLanguage(PRIMARY_LANG);
		
		// Labels
		for (PlainLiteral label : parseLiterals(json, URI_LABELS)) {
			if (label.getLanguage().equalsIgnoreCase(PRIMARY_LANG)) {
				t.setPrimaryLabel(label.getValue());
			} else {
				t.addAlternativeLabel(label);
			}
		}
		
		// Abstracts (only include primary language abstract for now!)
		for (PlainLiteral anAbstract : parseLiterals(json, URI_ABSTRACTS)) {
			if (anAbstract.getLanguage().equalsIgnoreCase(PRIMARY_LANG)) {
				t.setPrimaryDescription(anAbstract.getValue());
			} 
		}
		
		return t;
	}

	private List<PlainLiteral> parseLiterals(JSONObject jsonObj, String literalUri) {
		JSONArray literals = (JSONArray) jsonObj.get(literalUri);
	
		List<PlainLiteral> pLiterals = new ArrayList<PlainLiteral>();
		JSONObject literalObj;
		for (Object obj : literals) {
			literalObj = (JSONObject) obj;
			if (literalObj.get(KEY_TYPE).toString().equalsIgnoreCase(LITERAL)) {
				PlainLiteral pLiteral = new PlainLiteral(
						literalObj.get(KEY_VALUE).toString(),
						literalObj.get(KEY_LANG).toString()
				);
				pLiterals.add(pLiteral);
			}
		}
		
		return pLiterals;
	}
	
}
