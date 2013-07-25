package at.ait.dme.yuma.server.controller.json;

import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.model.Annotation;
import at.ait.dme.yuma.server.model.MediaType;
import at.ait.dme.yuma.server.model.MapKeys;
import at.ait.dme.yuma.server.model.Scope;
import at.ait.dme.yuma.server.model.tag.SemanticRelation;
import at.ait.dme.yuma.server.model.tag.SemanticTag;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

public class JSONFormatHandlerTest {
	
	private static SimpleDateFormat dateFormat;
	
	@BeforeClass
	public static void setUp() throws Exception {		
		dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss'Z'");
		dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
	}
	
	@Test
	public void testJSONParsing() throws Exception {
		FormatHandler jsonFormat = new JSONFormatHandler();
		Annotation a = jsonFormat.parse(Data.ANNOTATION_JSON_UPDATE);
		
		assertEquals("", a.getParentId());
		assertEquals("", a.getRootId());
		assertEquals("Ponte 25 de Abril", a.getTitle());
		assertEquals("The 25 de Abril Bridge is a suspension bridge connecting the city of " +
				"Lisbon, capital of Portugal, to the municipality of Almada on the left " +
				"bank of the Tagus river. It was inaugurated on August 6, 1966 and a train " +
				"platform was added in 1999. It is often compared to the Golden Gate Bridge " +
				"in San Francisco, USA, due to their similarities and same construction " +
				"company. With a total length of 2.277 m, it is the 19th largest " +
				"suspension bridge in the world. The upper platform carries six car lanes, " +
				"the lower platform two train tracks. Until 1974 the bridge was named Salazar " +
				"Bridge.", a.getText());
		assertEquals(Scope.PUBLIC, a.getScope());
		assertEquals(dateFormat.parse("2008-10-15T04:00:00Z"), a.getCreated());
		assertEquals(dateFormat.parse("2008-10-15T04:00:00Z"), a.getLastModified());		
		assertEquals("rsimon", a.getCreatedBy().getUsername());
		assertEquals("fragment", "<svg:svg xmlns:svg=\"http://www.w3.org/2000/svg\" width=\"640px\" height=\"480px\" viewbox=\"0px 0px 640px 480px\"> " +
				"<svg:defs xmlns:svg=\"http://www.w3.org/2000/svg\"> " +
				"<svg:symbol xmlns:svg=\"http://www.w3.org/2000/svg\" id=\"Polygon\"> " +
				"<svg:polygon xmlns:svg=\"http://www.w3.org/2000/svg\" " +
					"points=\"0,24 45,22 45,32 49,32 49,23 190,20 285,19 193,0 119,17 48,5\" stroke=\"rgb(229,0,0)\" " +
					"stroke-width=\"2\" fill=\"none\"> " +
				"</svg:polygon> " +
				"</svg:symbol> " +
				"</svg:defs>" +
				"</svg:svg>", a.getFragment());
		assertEquals(MediaType.IMAGE, a.getType());
		assertEquals("http://dme.ait.ac.at/object/lissabon.jpg", a.getObjectUri());

		List<SemanticTag> tags = a.getTags();
		assertTrue(tags.size() == 2);
		
		HashMap<String, Object> tag = new HashMap<String, Object>();
		tag.put(MapKeys.TAG_URI, new URI("http://www.geonames.org/2267057/"));
		tag.put(MapKeys.TAG_LABEL, "Lisbon");
		tag.put(MapKeys.TAG_LANG, "en");
		tag.put(MapKeys.TAG_TYPE, "place");
		tag.put(MapKeys.TAG_RELATION, new SemanticRelation("http://geonames.org/geo#", "spatiallyContains"));
		// assertTrue(tags.contains(new SemanticTag(tag)));
		
		tag.clear();
		tag.put(MapKeys.TAG_URI, new URI("http://www.geonames.org/2264397/"));
		tag.put(MapKeys.TAG_LABEL, "Portugal");
		tag.put(MapKeys.TAG_LANG, "en");
		tag.put(MapKeys.TAG_TYPE, "place");
		tag.put(MapKeys.TAG_RELATION, new SemanticRelation("http://geonames.org/geo#", "spatiallyContains"));
		// assertTrue(tags.contains(new SemanticTag(tag)));
	}

	@Test
	public void testJSONSerialization() throws Exception {
		FormatHandler jsonFormat = new JSONFormatHandler();
		Annotation a = jsonFormat.parse(Data.ANNOTATION_JSON_UPDATE);
		
		String serialized = jsonFormat.serialize(a);
		assertTrue(serialized.contains("\"parent-id\" : \"\""));
		assertTrue(serialized.contains("\"root-id\" : \"\""));
		assertTrue(serialized.contains("\"title\" : \"Ponte 25 de Abril\""));
		assertTrue(serialized.contains("\"text\" : \"The 25 de Abril Bridge is a " +
				"suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
				"to the municipality of Almada on the left bank of the Tagus river. It " +
				"was inaugurated on August 6, 1966 and a train platform was added in 1999. " +
				"It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
				"their similarities and same construction company. With a total length of " +
				"2.277 m, it is the 19th largest suspension bridge in the world. The upper " +
				"platform carries six car lanes, the lower platform two train tracks. Until " +
				 "1974 the bridge was named Salazar Bridge.\""));
		assertTrue(serialized.contains("\"scope\" : \"PUBLIC\""));
		assertTrue(serialized.contains("\"last-modified\" : 1224043200000"));
		assertTrue(serialized.contains("\"created\" : 1224043200000"));
		assertTrue(serialized.contains("\"created-by\" : { \"user-name\" : \"rsimon\""));
		assertTrue(serialized.contains("\"fragment\" : \"" + 
				"<svg:svg xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" width=\\\"640px\\\" height=\\\"480px\\\" viewbox=\\\"0px 0px 640px 480px\\\"> " +
				  "<svg:defs xmlns:svg=\\\"http://www.w3.org/2000/svg\\\"> " +
					"<svg:symbol xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" id=\\\"Polygon\\\"> " +
					"<svg:polygon xmlns:svg=\\\"http://www.w3.org/2000/svg\\\" " +
					"points=\\\"0,24 45,22 45,32 49,32 49,23 190,20 285,19 193,0 119,17 48,5\\\" stroke=\\\"rgb(229,0,0)\\\" " +
					"stroke-width=\\\"2\\\" fill=\\\"none\\\"> " +
					"</svg:polygon> " +
					"</svg:symbol> " +
				  "</svg:defs>" +
				"</svg:svg>" +
			  "\""));
		assertTrue(serialized.contains("\"media-type\" : \"IMAGE\""));
		assertTrue(serialized.contains("\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\""));

		Pattern p = Pattern.compile("\"tags\" : \\[((.|\n)*?)\\]");
		Matcher m = p.matcher(serialized);
		assertTrue(m.find());
		
		String[] tags = m.group(1).split("\\} , \\{");
		assertTrue(tags.length == 2);

		/*
		assertTrue(tags[0].contains("\"uri\" : \"http://www.geonames.org/2267057/\""));
		assertTrue(tags[0].contains("\"label\" : \"Lisbon\""));
		assertTrue(tags[0].contains("\"type\" : \"place\""));
		assertTrue(tags[0].contains("\"lang\" : \"en\"")); 
		assertTrue(tags[0].contains("\"property\" : \"spatiallyContains\""));
		
		assertTrue(tags[1].contains("\"uri\" : \"http://www.geonames.org/2264397/\""));
		assertTrue(tags[1].contains("\"label\" : \"Portugal\""));
		assertTrue(tags[1].contains("\"type\" : \"place\""));
		assertTrue(tags[1].contains("\"lang\" : \"en\"")); 
		assertTrue(tags[1].contains("\"property\" : \"spatiallyContains\""));
		*/
	}
	
}
