package at.ait.dme.yuma.server.controller.opensearch;

import java.util.ArrayList;
import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.model.Annotation;

public class OpenSearchFormatHandlerTest {

	private static List<Annotation> annotations;
	
	@BeforeClass
	public static void setUp() throws Exception {	
		Setup.buildConfiguration();
		FormatHandler jsonFormat = new JSONFormatHandler();
		annotations = new ArrayList<Annotation>();
		annotations.add(jsonFormat.parse(Data.ANNOTATION_JSON_UPDATE));
	}
	
	@Test
	public void testOpenSearchSerialization() {
		FormatHandler osFormat = new OpenSearchFormatHandler(
				"lisbon",
				annotations.size(),
				0,
				20
		);
		String osRss = osFormat.serialize(annotations);
		System.out.println(osRss);
	}
	
}
