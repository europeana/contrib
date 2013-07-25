package at.ait.dme.yuma.server.bootstrap;

import java.io.IOException;
import java.io.InputStream;

import org.apache.wicket.util.io.IOUtils;

public class Data {
	
	public static final String ANNOTATION_JSON_ORIGINAL =
		"[{ \"parent-id\" : null , " +
		  "\"root-id\" : null , " +
		  "\"title\" : \"Ponte 25 de Abril\" , " +
		  "\"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
		  "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		  "and a train platform was added in 1999.\", " +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ,"+
		  "\"created\" : 1224043200000 , "+
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"fragment\" : \"" +
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
		  "\" , "+
		  "\"media-type\" : \"image\" , "+
		  "\"object-uri\" : \"http:\\/\\/upload.wikimedia.org\\/wikipedia\\/commons\\/4\\/49\\/Hirschvogel_Map_Austria.jpg\"" +
		"}]";
		
	public static final String ANNOTATION_JSON_UPDATE =
		"{ \"parent-id\" : \"\" , " +
		  "\"root-id\" : \"\" , " +
		  "\"title\" : \"Ponte 25 de Abril\" , "+
		  "\"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
		  "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		  "and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
		  "their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest " +
		  "suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. Until " +
		  "1974 the bridge was named Salazar Bridge.\", " +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ,"+
		  "\"created\" : 1224043200000 , "+
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"fragment\" : \"" +
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
		  "\" , "+
		  "\"media-type\" : \"image\" , "+
		  "\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\", " +
		  "\"tags\" : [ " +
		    "{ \"alt-labels\" : [ { \"val\" : \"Lissabon\" , \"lang\" : \"de\" } , { \"val\" : \"Lisboa\" }  ] , \"label\" : \"Lisbon\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"http://geonames.org/geo#\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : [ ] , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2267057/\"} ," +
		    "{ \"alt-labels\" : [ ] , \"label\" : \"Portugal\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"http://geonames.org/geo#\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : [ ] , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2264397/\"} " +
		  "]" +
		"}";
	
	public static final String ANNOTATION_OAC_UPDATE =
		"<rdf:RDF " +
		"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
		"xmlns:j.0=\"http://geonames.org/geo#\" " +
		"xmlns:j.1=\"http://www.w3.org/2008/content#\" " +
		"xmlns:j.2=\"http://purl.org/dc/terms/\" " +
		"xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
		"xmlns:oac=\"http://www.openannotation.org/ns/\" " +
		"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"> " + 
		"<rdf:Description rdf:about=\"http://localhost:8081/yuma-server/api/annotation/null\"> " +
		"<dc:title>Ponte 25 de Abril</dc:title> " +
		"<j.2:created>Wed Oct 15 06:00:00 CEST 2008</j.2:created> " +
		"<j.2:modified>Wed Oct 15 06:00:00 CEST 2008</j.2:modified> " +
		"<dc:creator>rsimon</dc:creator> " +
		"<oac:hasBody rdf:nodeID=\"A0\"/> " +
		"<oac:hasTarget rdf:nodeID=\"A1\"/> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/> " +
		"</rdf:Description> " +
		"<rdf:Description rdf:nodeID=\"A0\"> " +
		"<rdfs:label>The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, due to their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. Until 1974 the bridge was named Salazar Bridge.</rdfs:label> " +
		"<j.0:spatiallyContains>http://www.geonames.org/2264397/</j.0:spatiallyContains> " +
		"<j.0:spatiallyContains>http://www.geonames.org/2267057/</j.0:spatiallyContains> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Body\"/> " +
		"</rdf:Description> " +
		"<rdf:Description rdf:nodeID=\"A2\"> " +
		"<j.1:characterEncoding>utf-8</j.1:characterEncoding> " +
		"<j.1:chars>&lt;svg:svg xmlns:svg=\"http://www.w3.org/2000/svg\" width=\"640px\" height=\"480px\" viewbox=\"0px 0px 640px 480px\"&gt; &lt;svg:defs xmlns:svg=\"http://www.w3.org/2000/svg\"&gt; &lt;svg:symbol xmlns:svg=\"http://www.w3.org/2000/svg\" id=\"Polygon\"&gt; &lt;svg:polygon xmlns:svg=\"http://www.w3.org/2000/svg\" points=\"0,24 45,22 45,32 49,32 49,23 190,20 285,19 193,0 119,17 48,5\" stroke=\"rgb(229,0,0)\" stroke-width=\"2\" fill=\"none\"&gt; &lt;/svg:polygon&gt; &lt;/svg:symbol&gt; &lt;/svg:defs&gt;&lt;/svg:svg&gt;</j.1:chars> " +
		"<rdf:type rdf:resource=\"http://www.w3.org/2008/content#ContentAsText\"/> " +
		"<dc:format>image/svg+xml</dc:format> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/SvgConstraint\"/> " +
		"</rdf:Description> " +
		"<rdf:Description rdf:nodeID=\"A1\"> " +
		"<oac:constrainedBy rdf:nodeID=\"A2\"/> " +
		"<oac:constrains>http://dme.ait.ac.at/object/lissabon.jpg</oac:constrains> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/ConstrainedTarget\"/> " +
		"</rdf:Description> " +
		"</rdf:RDF>";
	
	public static final String ANNOTATION_JSON_NOFRAGMENT =
		"{ \"parent-id\" : \"\" , " +
		  "\"root-id\" : \"\" , " +
		  "\"title\" : \"Ponte 25 de Abril\" , "+
		  "\"text\" : \"The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, " + 
		  "to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 " +
		  "and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, due to " +
		  "their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest " +
		  "suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. Until " +
		  "1974 the bridge was named Salazar Bridge.\", " +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ,"+
		  "\"created\" : 1224043200000 , "+
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"media-type\" : \"image\" , "+
		  "\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\", " +
		  "\"tags\" : [ " +
		    "{ \"alt-labels\" : [ { \"val\" : \"Lissabon\" , \"lang\" : \"de\" } , { \"val\" : \"Lisboa\" }  ] , \"label\" : \"Lisbon\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"http://geonames.org/geo#\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : [ ] , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2267057/\"} ," +
		    "{ \"alt-labels\" : [ ] , \"label\" : \"Portugal\" , \"type\" : \"place\" , \"relation\" : { \"namespace\" : \"http://geonames.org/geo#\" , \"property\" : \"spatiallyContains\" } , \"alt-descriptions\" : [ ] , \"lang\" : \"en\" , \"uri\" : \"http://www.geonames.org/2264397/\"} " +
		  "]" +
		"}";
	
	public static final String ANNOTATION_OAC_NOFRAGMENT = 
		"<rdf:RDF " +
		"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" " +
		"xmlns:j.0=\"http://geonames.org/geo#\" " +
		"xmlns:j.1=\"http://purl.org/dc/terms/\" " +
		"xmlns:dc=\"http://purl.org/dc/elements/1.1/\" " +
		"xmlns:oac=\"http://www.openannotation.org/ns/\" " +
		"xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\"> " + 
		"<rdf:Description rdf:about=\"http://localhost:8081/yuma-server/api/annotation/null\"> " +
		"<dc:title>Ponte 25 de Abril</dc:title> " +
		"<j.1:created>Wed Oct 15 06:00:00 CEST 2008</j.1:created> " +
		"<j.1:modified>Wed Oct 15 06:00:00 CEST 2008</j.1:modified> " +
		"<dc:creator>rsimon</dc:creator> " +
		"<oac:hasBody rdf:resource=\"http://localhost:8081/yuma-server/api/annotation/null#body\"/> " +
		"<oac:hasTarget rdf:resource=\"http://dme.ait.ac.at/object/lissabon.jpg\"/> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/> " +
		"</rdf:Description> " +
		"<rdf:Description rdf:about=\"http://dme.ait.ac.at/object/lissabon.jpg\"> " +
	    "<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Target\"/> " +
	    "</rdf:Description> " +
		"<rdf:Description rdf:about=\"http://localhost:8081/yuma-server/api/annotation/null#body\"> " +
		"<rdfs:label>The 25 de Abril Bridge is a suspension bridge connecting the city of Lisbon, capital of Portugal, to the municipality of Almada on the left bank of the Tagus river. It was inaugurated on August 6, 1966 and a train platform was added in 1999. It is often compared to the Golden Gate Bridge in San Francisco, USA, due to their similarities and same construction company. With a total length of 2.277 m, it is the 19th largest suspension bridge in the world. The upper platform carries six car lanes, the lower platform two train tracks. Until 1974 the bridge was named Salazar Bridge.</rdfs:label> " +
		"<j.0:spatiallyContains>http://www.geonames.org/2264397/</j.0:spatiallyContains> " +
		"<j.0:spatiallyContains>http://www.geonames.org/2267057/</j.0:spatiallyContains> " +
		"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Body\"/> " +
		"</rdf:Description> " +
		"</rdf:RDF>";
	
	public static final String ROOT_JSON =
		"{ \"parent-id\" : \"\" , " +
		  "\"root-id\" : \"\" , " +
		  "\"title\" : \"Ponte 25 de Abril\" , "+
		  "\"text\" : \"Root annotation!\" ," +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ," +
		  "\"created\" : 1224043200000 , " +
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"media-type\" : \"image\" , " +
		  "\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\"" +
		"}";
	
	private static final String REPLY_JSON =
		"{ \"parent-id\" : \"@parent@\" , " +
		  "\"root-id\" : \"@root@\" , " +
		  "\"title\" : \"RE: Ponte 25 de Abril\" , "+
		  "\"text\" : \"Interesting!\" ," +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ," +
		  "\"created\" : 1224043200000 , " +
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"media-type\" : \"image\" , " +
		  "\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\"" +
		"}";
	
	private static final String REPLY_JSON_WITH_FRAGMENT =
		"{ \"parent-id\" : \"@parent@\" , " +
		  "\"root-id\" : \"@root@\" , " +
		  "\"title\" : \"RE: Ponte 25 de Abril\" , "+
		  "\"text\" : \"Interesting!\" ," +
		  "\"scope\" : \"public\" , "+
		  "\"last-modified\" : 1224043200000 ," +
		  "\"created\" : 1224043200000 , " +
		  "\"created-by\" :  { \"user-name\" : \"rsimon\" } , " +
		  "\"fragment\" : \"" +
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
		  "\" , "+
		  "\"media-type\" : \"image\" , " +
		  "\"object-uri\" : \"http://dme.ait.ac.at/object/lissabon.jpg\"" +
		"}";
	
	public static final String PELAGIOS_JSON =
		"{ \"tags\":[ "+
			"{ \"description\":\"Corsica\", "+
				"\"relation\":null, "+
				"\"label\":\"Corsica\", "+
				"\"type\":\"PLACE\", "+
				"\"lang\":\"en\", "+
		        "\"uri\":\"http://pleiades.stoa.org/places/991339\""+
		    "}" +
		    "],"+
		    "\"text\":\"\", "+
		    "\"scope\":\"PUBLIC\", "+
		    "\"last-modified\":1300881749638, "+
		    "\"created-by\":{ "+
		    	"\"user-name\":\"guest\" "+
		    "}, "+
		    "\"media-type\":\"MAP\", "+
		    "\"object-uri\":\"http://dme.ait.ac.at/samples/maps/oenb/AC04248667.tif\", "+
		    "\"id\":\"479\", "+
		    "\"root-id\":null, "+
		    "\"parent-id\":null, "+
		    "\"title\":\"A Pelagios Test Annotation\", "+
		    "\"created\":1300881749638, "+
		    "\"fragment\":null "+
		"}";
	
	public static final String PELAGIOS_RDF =
		"<rdf:RDF "+
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" "+
			"xmlns:j.0=\"http://purl.org/dc/terms/\" "+
			"xmlns:dc=\"http://purl.org/dc/elements/1.1/\" "+
			"xmlns:oac=\"http://www.openannotation.org/ns/\"> "+
			"<rdf:Description rdf:about=\"http://dme.ait.ac.at/samples/maps/oenb/AC04248667.tif\"> "+
				"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Target\"/> "+
			"</rdf:Description> "+
			"<rdf:Description rdf:about=\"http://localhost:8081/yuma-server/api/annotation/479\"> "+
				"<dc:title>A Pelagios Test Annotation</dc:title> "+
			    "<j.0:created>Wed Mar 23 13:02:29 CET 2011</j.0:created> "+
			    "<j.0:modified>Wed Mar 23 13:02:29 CET 2011</j.0:modified> "+
				"<dc:creator>guest</dc:creator> "+
				"<oac:hasBody rdf:resource=\"http://pleiades.stoa.org/places/991339\"/> "+
				"<oac:hasTarget rdf:resource=\"http://dme.ait.ac.at/samples/maps/oenb/AC04248667.tif\"/> "+
				"<rdf:type rdf:resource=\"http://www.openannotation.org/ns/Annotation\"/> "+
			"</rdf:Description> "+
		"</rdf:RDF>";
	
	public static String getJsonTestAnnot() throws IOException {
		InputStream is = Data.class.getResourceAsStream("/testdata/408.json");
		return IOUtils.toString(is);
	}
		
	public static String reply(String root, String parent) {
		return REPLY_JSON
			.replace("@root@", root)
			.replace("@parent@", parent);
	}
	
	public static String replyWithFragment(String root, String parent) {
		return REPLY_JSON_WITH_FRAGMENT
			.replace("@root@", root)
			.replace("@parent@", parent);
	}

}
