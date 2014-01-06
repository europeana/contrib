package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.mapping.MappingSummary;
import gr.ntua.ivml.mint.mapping.model.Mappings;
import gr.ntua.ivml.mint.util.JSONUtils;
import gr.ntua.ivml.mint.util.XMLUtils;
import gr.ntua.ivml.mint.xml.transform.XMLFormatter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Collection;

import org.apache.commons.io.FileUtils;

public class XSLGeneration {
	private static final String DIRECTORY = "/tmp/partage/mappings";
	private static final String EXTENSION = ".mint";
	private static final String EXTENSION2 = ".xsl";

	public static void main(String[] args) {
		File mappingsDir = new File(DIRECTORY);
		if(mappingsDir.isDirectory()) {
			File[] files = mappingsDir.listFiles(new FilenameFilter() {
			    public boolean accept(File dir, String name) {
			        return name.toLowerCase().endsWith(EXTENSION);
			    }
			});
			
			for(File file: files) {
				try {
					Mappings mappings = new Mappings(file);
					Collection<String> xpaths = MappingSummary.getAllMappedXPaths(mappings);
					String root = XMLUtils.commonRoot(xpaths);
					
					gr.ntua.ivml.mint.mapping.old.XSLTGenerator generator = new gr.ntua.ivml.mint.mapping.old.XSLTGenerator();
					generator.setItemLevel(root);
					String xsl = generator.generateFromJSONObject(JSONUtils.toNetSfJSONObject(mappings.asJSONObject()));
					xsl = XMLFormatter.format(xsl);
					
					File out = new File(file.getAbsolutePath().replace(EXTENSION, EXTENSION2));
					FileUtils.writeStringToFile(out, xsl, "UTF-8");
				} catch (Exception e) {
					System.err.println("Problem in : " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
	}
}
