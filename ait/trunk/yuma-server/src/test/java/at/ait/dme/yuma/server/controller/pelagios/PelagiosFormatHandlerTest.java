package at.ait.dme.yuma.server.controller.pelagios;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.SerializationLanguage;
import at.ait.dme.yuma.server.controller.rdf.pelagios.PelagiosFormatHandler;
import at.ait.dme.yuma.server.model.Annotation;

public class PelagiosFormatHandlerTest {

	@BeforeClass
	public static void setUp() throws Exception {
		Setup.buildConfiguration();
		
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setNormalize(true);
	}

	@Test
	public void pelagios2oac() throws Exception {
		Annotation before = new JSONFormatHandler().parse(Data.PELAGIOS_JSON);
		PelagiosFormatHandler pelagiosFormat = new PelagiosFormatHandler(SerializationLanguage.RDF_XML);
		
		String serializedAnnotation = pelagiosFormat.serialize(before);
		XMLAssert.assertXMLEqual(serializedAnnotation, Data.PELAGIOS_RDF);
	}
	
}
