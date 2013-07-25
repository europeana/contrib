package at.ait.dme.yuma.server.controller.lemo;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.controller.FormatHandler;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.controller.rdf.lemo.LEMOFormatHandler;
import at.ait.dme.yuma.server.model.Annotation;

public class LEMOXMLFormatHandlerTest {
	
	@BeforeClass
	public static void setUp() throws Exception {
		Setup.buildConfiguration();
	}
	
	@Test
	public void testLEMOSerialization() throws Exception {
		Annotation before = new JSONFormatHandler().parse(Data.ANNOTATION_JSON_UPDATE);
		
		FormatHandler lemoFormat = new LEMOFormatHandler(SerializationLanguage.RDF_XML);
		String s = lemoFormat.serialize(before);
		System.out.println(s);

		Annotation after = lemoFormat.parse(s);

		assertEquals(before.getTitle(), after.getTitle());
		assertEquals(before.getText(), after.getText());
		
		System.out.println(before.getCreatedBy() +", "+ after.getCreatedBy());
		//assertEquals(before.getCreatedBy(), after.getCreatedBy());
	}

}
