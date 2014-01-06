package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.util.XMLUtils;
import gr.ntua.ivml.mint.xml.transform.XMLFormatter;

import java.io.File;
import org.apache.commons.io.FileUtils;

public class XML2MappingJSON {
	public static void main(String[] args) {
		try {
			File file = new File("item.xml");
			String xml = FileUtils.readFileToString(file);

			Mappings handler = Mappings.templateFromXML(xml);
//			System.out.println(handler);		
			System.out.println(XMLFormatter.format(XMLUtils.toXML(handler)));
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}
}
