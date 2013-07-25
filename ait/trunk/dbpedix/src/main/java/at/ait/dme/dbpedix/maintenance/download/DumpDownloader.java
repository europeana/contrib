package at.ait.dme.dbpedix.maintenance.download;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.compress.compressors.bzip2.BZip2CompressorInputStream;
import org.apache.log4j.Logger;

import at.ait.dme.dbpedix.config.Config;

/**
 * A utility class for downloading DBpedia dump files. 
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class DumpDownloader {
	
	private static String baseURL = Config.getInstance().getDBpediaDownloadBaseURL();
	
	private Logger log = Logger.getLogger(this.getClass());
	
	private File dumpDir = new File(Config.getInstance().getDumpDirectory());

	/**
	 * Utility method: returns the URL for the DBpedia label file in the 
	 * specified language
	 */
	public static String getLabelsURL(String lang) {
		return getDownloadURL("/labels_", lang);
	}
	
	/**
	 * Utility method: returns the URL for the DBpedia short abstracts
	 * file in the specified language
	 */
	public static String getShortAbstractsURL(String lang) {
		return getDownloadURL("/short_abstracts_", lang);
	}
	
	/**
	 * Utility method: returns the URL for the DBpedia geo-
	 * coordinates file in the specified language
	 */
	public static String getGeoCoordinatesURL(String lang) {
		return getDownloadURL("/geo_coordinates_", lang);
	}
	
	/**
	 * Utility method: returns the URL for the DBpedia label file in the 
	 * specified language
	 */
	private static String getDownloadURL(String filename, String lang) {
		return baseURL + lang.toLowerCase() + filename + lang.toLowerCase() + ".nt.bz2"; 
	}
	
	/**
	 * Downloads the file from the specified URL.
	 * @param url the URL
	 * @return the file
	 * @throws IOException if anything goes wrong retrieving/saving the file
	 */
	public File downloadFile(String url) throws IOException {
		log.info("Downloading  " + url);
		try {
			URL downloadURL = new URL(url);
			URLConnection connection = downloadURL.openConnection();
			InputStream is = connection.getInputStream();
			
			String filename = getFilename(url);
			log.info("Saving to file " + filename);
			File archive = new File(dumpDir, filename);
			writeToFile(is, archive);
			log.info("Complete.");
			return archive;
		} catch (MalformedURLException e) {
			// Should never happen, except in cases of misconfiguration!
			throw new RuntimeException();
		}
	}
	
	private String getFilename(String url) {
		return url.substring(url.lastIndexOf('/') + 1);
	}
	
	/**
	 * bzip2-uncompresses the specified file. The name of the result
	 * file will be the same as the original archive filename, with
	 * the extension removed. I.e. 'labels_en.nt.bz2' will be uncompressed
	 * to 'labels_en.nt'.
	 * @param file the bzip2 archive
	 * @return the uncompressed file
	 * @throws IOException if anyhting goes wrong with reading/writing files
	 */
	public File uncompressFile(File file) throws IOException {
		log.info("Uncompressing  " + file.getName());
		BZip2CompressorInputStream is = 
			new BZip2CompressorInputStream(new BufferedInputStream(new FileInputStream(file)));
		
		String f = file.getAbsolutePath();
		f = f.substring(0, f.lastIndexOf('.'));
		
		File txt = new File(f);
		writeToFile(is, txt);
		log.info("Complete.");
		return txt;
	}
	
	private void writeToFile(InputStream is, File file) throws IOException {
		OutputStream out = new FileOutputStream(file);
		byte buffer[] = new byte[1024];
		int len;
		while ((len = is.read(buffer)) > 0) {
			out.write(buffer, 0, len);
		}
		out.close();
		is.close();	
	}

}
