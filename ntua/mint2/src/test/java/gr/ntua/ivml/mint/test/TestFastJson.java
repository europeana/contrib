package gr.ntua.ivml.mint.test;

import java.io.File;
import java.io.IOException;

import gr.ntua.ivml.mint.util.StringUtils;

//import net.minidev.json.parser.JSONParser;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

public class TestFastJson {
	private static final Logger log = Logger.getLogger(TestFastJson.class);

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		log.debug("Test");
		String file = "/tmp/Partage.mint";
		
		String contents;
		try {
			contents = StringUtils.fileContents(new File(file)).toString();

			log.debug("serialize old");
			//net.sf.json.JSONSerializer.toJSON(contents);
			log.debug("serialize old done");
			
			log.debug("serialize new");
//			JSONParser p = new JSONParser();
//			Object o = p.parse(contents);
//			System.out.println(o.getClass());
//			log.debug("serialize new done");


			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
