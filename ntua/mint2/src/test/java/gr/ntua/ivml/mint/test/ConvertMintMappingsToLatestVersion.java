package gr.ntua.ivml.mint.test;

import gr.ntua.ivml.mint.mapping.MappingConverter;
import gr.ntua.ivml.mint.mapping.model.Mappings;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

/**
 * Convert all mapping files with extension EXTENSION in DIRECTORY to latest version by using MappingConverter.
 * Results are saved with extension EXTENSION2. 
 * @author Fotis Xenikoudakis
 *
 */
public class ConvertMintMappingsToLatestVersion {
	private static final String DIRECTORY = "/tmp/partage/mappings";
	private static final String EXTENSION = ".mint";
	private static final String EXTENSION2 = ".mint2";

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
					MappingConverter.upgradeToLatest(mappings);
					
					File upgraded = new File(file.getAbsolutePath().replace(EXTENSION, EXTENSION2));
					FileUtils.writeStringToFile(upgraded, mappings.toString(2), "UTF-8");
				} catch (Exception e) {
					System.err.println("Problem in : " + file.getAbsolutePath());
					e.printStackTrace();
				}
			}
		}
	}
}
