package gr.ntua.ivml.mint.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import net.minidev.json.JSONArray;
import net.minidev.json.JSONObject;
import net.minidev.json.JSONValue;
import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Serializer;

import org.apache.log4j.Logger;

public class JsonToXml {

	public static final Logger log = Logger.getLogger( JsonToXml.class);
	
	public Document convert( Object input ) {
		// we only convert JSONObjects		
		Element root = new Element( "json" );
		if( input instanceof JSONObject )
			addObject( root, (JSONObject) input );
		else if( input instanceof JSONArray ) {
			addArray(root, "item", 0, (JSONArray) input );
		}
		return new Document(root);
	}
	
	private void addObject( Element parent, JSONObject obj ) {
		// iterate through keys in obj
		for( String key: obj.keySet()) {
			Object unknownObj = obj.get(key);
			if( unknownObj instanceof JSONObject ) {
				// recurse
				Element newElem = new Element( key );
				parent.appendChild(newElem );
				addObject( newElem, (JSONObject) unknownObj);
			} else if(unknownObj instanceof JSONArray ) { 
				addArray( parent, key, 0, (JSONArray) unknownObj );
			} else {
				Element txt = new Element( key );
				txt.appendChild(unknownObj.toString());
				parent.appendChild( txt );
			}
		}
	}
	
	
	// create for each element of the json array an elemName xml-Element and an indexLevel attribute counter
	private void addArray( Element parent, String elemName, int indexLevel, JSONArray arr ) {
		for( int i=0; i<arr.size(); i++ ) {
			Element newElem = new Element( elemName );
			newElem.addAttribute( new Attribute("IndexLevel_"+indexLevel, Integer.toString(i) ));
			parent.appendChild(newElem );
			Object arrElement = arr.get( i );
			// if its an array we need to recurse a little
			if( arrElement instanceof JSONArray ) {
				JSONArray newArray = (JSONArray) arrElement;
				addArray( newElem, elemName, indexLevel+1, newArray );
			} else if( arrElement instanceof JSONObject )  {
				addObject( newElem, (JSONObject) arrElement );
			} else {
				newElem.appendChild( arrElement.toString());
			}
		}
	}
	
	public static void main( String[] args ) {
		// read in some json 
		try {
		File input = new File( args[0]);
		Reader fr = new InputStreamReader( new FileInputStream(input), "UTF8" );
		Object res = JSONValue.parse(fr);
		JsonToXml converter = new JsonToXml();
		Document doc = converter.convert( res );
		Serializer serializer = new Serializer(System.out, "UTF8");
	      serializer.setIndent(2);
	      serializer.setMaxLength(80);
	      serializer.write(doc);  
		// System.out.println( JSONValue.toJSONString(res));
		} catch(Exception e) {
			log.error( "Exception during test ", e );
		}
	}
}
