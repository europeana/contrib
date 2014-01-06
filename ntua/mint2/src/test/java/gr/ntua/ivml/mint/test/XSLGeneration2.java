package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.xml.transform.XMLFormatter;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import net.minidev.json.parser.ParseException;

import org.apache.commons.io.FileUtils;

public class XSLGeneration2 {
	private static final String DIRECTORY = "/tmp/partage/mappings";
	private static final String EXTENSION = ".mint2";
	private static final String EXTENSION2 = ".xsl2";

	public static void main(String[] args) throws IOException {
		doDirectory(DIRECTORY);
//		doFile(new File(DIRECTORY + "/1334-MAK Mapping 5.mint2"), true);		
//		doFile(new File(DIRECTORY + "/1247-m3.mint2"), true);		
	}

	public static void doDirectory(String directory) {
		File mappingsDir = new File(directory);
		if(mappingsDir.isDirectory()) {
			File[] files = mappingsDir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(EXTENSION);
			    }
			});
			
			for(File file: files) {
				try {
					doFile(file, false);
				} catch (Exception e) {
					System.err.println("Problem in : " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
	}
	
	public static void doFile(File file, boolean force) throws IOException, ParseException {
		System.out.println(file.getAbsolutePath());
		File out = new File(file.getAbsolutePath().replace(EXTENSION, EXTENSION2));
		
		if(force || !out.exists()) {
			Mappings mappings = new Mappings(file);
			
			gr.ntua.ivml.mint.xml.transform.XSLTGenerator generator = new gr.ntua.ivml.mint.xml.transform.XSLTGenerator();
			generator.setOption(gr.ntua.ivml.mint.xml.transform.XSLTGenerator.OPTION_ADD_COMMENTS, false);
			generator.setOption(gr.ntua.ivml.mint.xml.transform.XSLTGenerator.OPTION_COMPUTE_ITEM_XPATH_IF_NOT_SET, true);
			String xsl = generator.generateFromMappings(mappings);
			xsl = XMLFormatter.format(xsl);
			
			FileUtils.writeStringToFile(out, xsl, "UTF-8");
		}
	}
}
