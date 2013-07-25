package at.ait.dme.yuma.server.controller.oac;

import static org.junit.Assert.assertEquals;

import org.custommonkey.xmlunit.XMLAssert;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.BeforeClass;
import org.junit.Test;

import at.ait.dme.yuma.server.bootstrap.Data;
import at.ait.dme.yuma.server.bootstrap.Setup;
import at.ait.dme.yuma.server.controller.json.JSONFormatHandler;
import at.ait.dme.yuma.server.controller.rdf.oac.OACFormatHandler;
import at.ait.dme.yuma.server.db.hibernate.HibernateAnnotationDB;
import at.ait.dme.yuma.server.exception.AnnotationDatabaseException;
import at.ait.dme.yuma.server.model.Annotation;

public class OACFormatHandlerTest {

	private HibernateAnnotationDB db;
	
	@BeforeClass
	public static void setUp() throws Exception {
		Setup.buildConfiguration();
		
		XMLUnit.setNormalizeWhitespace(true);
		XMLUnit.setNormalize(true);
	}

	@Test
	public void testLargeConstrainedAnnot() throws Exception {
		Annotation before = new JSONFormatHandler().parse(Data.getJsonTestAnnot());
		OACFormatHandler oacFormat = new OACFormatHandler();
		
		String serializedAnnotation = oacFormat.serialize(before);
		System.out.println(serializedAnnotation);
	}
	
	@Test
	public void testSerializationWithFragment() throws Exception {
		Annotation before = new JSONFormatHandler().parse(Data.ANNOTATION_JSON_UPDATE);
		OACFormatHandler oacFormat = new OACFormatHandler();
		
		String serializedAnnotation = oacFormat.serialize(before);
		System.out.println(serializedAnnotation);

		//TODO: xpath based comparison of "important" elements
		// title, created, modified, creator, label, #semanticTags, chars, constrains
	}
	
	@Test
	public void testSerializationWithoutFragment() throws Exception {
		Annotation before = new JSONFormatHandler().parse(Data.ANNOTATION_JSON_NOFRAGMENT);
		OACFormatHandler oacFormat = new OACFormatHandler();
		
		String serializedAnnotation = oacFormat.serialize(before);
		System.out.println(serializedAnnotation);
		
		XMLAssert.assertXMLEqual(serializedAnnotation, Data.ANNOTATION_OAC_NOFRAGMENT);
	}

	//@Test
	public void testReplySerializationWithoutFragment() throws Exception 
	{	
		String rootId = getParentAnnotationId();
		String replyAnnotationJSON = Data.reply(rootId, rootId);
		Annotation before = new JSONFormatHandler().parse(replyAnnotationJSON);
		
		OACFormatHandler oacFormat = new OACFormatHandler();
		String oacSerializedReplyAnnotation = oacFormat.serialize(before);
		System.out.println(oacSerializedReplyAnnotation);
		
		Annotation after = oacFormat.parse(oacSerializedReplyAnnotation);
		assertEquals(before, after);
		
		cleanUp(rootId);
	}
	
	//@Test
	public void testReplySerializationWithFragment() throws Exception {
		String rootId = getParentAnnotationId();
		String replyAnnotationWithFragmentJSON = Data.replyWithFragment(rootId, rootId);
		Annotation before = new JSONFormatHandler().parse(replyAnnotationWithFragmentJSON);

		OACFormatHandler oacFormat = new OACFormatHandler();
		String oacSerializedReplyAnnotationWithFragment = oacFormat.serialize(before);
		System.out.println(oacSerializedReplyAnnotationWithFragment);
		
		Annotation after = oacFormat.parse(oacSerializedReplyAnnotationWithFragment);
		assertEquals(before, after);
		
		cleanUp(rootId);
	}
	
	private String getParentAnnotationId() throws Exception {
		establishDbConnection();
		
		Annotation root = new JSONFormatHandler().parse(Data.ROOT_JSON);
		return db.createAnnotation(root);
	}
	
	private void establishDbConnection() throws AnnotationDatabaseException {
		db = new HibernateAnnotationDB();
		db.connect();
	}

	private void cleanUp(String annotationId) throws Exception {
		db.deleteAnnotation(annotationId);
		db.disconnect();
	}
}
