package gr.ntua.ivml.mint.test;

import java.io.File;
import java.io.IOException;

import org.xml.sax.SAXException;

import gr.ntua.ivml.mint.persistent.XmlSchema;
import gr.ntua.ivml.mint.util.Config;
import gr.ntua.ivml.mint.util.StringUtils;
import gr.ntua.ivml.mint.xsd.SchemaValidator;

public class Validate {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			String schemaPath = "lido/lido-v1.0-proxy-partage.xsd";
			XmlSchema xmlSchema = new XmlSchema();
			xmlSchema.setXsd(schemaPath);
			xmlSchema.setDbID(new Long(0));
			
			File schema = new File(Config.getSchemaPath(schemaPath));			
			File file = new File("/tmp/xml2.xml");
			
			String contents = StringUtils.fileContents(file).toString();

			SchemaValidator.validateXSD(contents, xmlSchema);
			System.out.println("VALIDATION schema 1 OK!");

			SchemaValidator.validateXSD(contents, xmlSchema);
			System.out.println("VALIDATION schema 2 OK!");			
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
