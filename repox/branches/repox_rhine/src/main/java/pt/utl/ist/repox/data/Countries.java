package pt.utl.ist.repox.data;

import org.apache.log4j.Logger;
import pt.utl.ist.repox.util.RepoxContextUtil;

import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;


public class Countries {
	private static final Logger log = Logger.getLogger(Countries.class);

	private static Map<String, String> countryMap;

	public static Map<String, String> getCountries() {
		if(countryMap == null) {
			loadCountries();
		}

		return countryMap;
	}

	private static void loadCountries() {
		try {
			String countriesFilename = RepoxContextUtil.COUNTRIES_FILENAME;
			//File countriesFile = new File(Thread.currentThread().getContextClassLoader().getResource(countriesFilename).getFile());

            String configurationFile = URLDecoder.decode(Thread.currentThread().getContextClassLoader().getResource(countriesFilename).getFile(), "ISO-8859-1");
            File countriesFile = new File(configurationFile);

			countryMap = new LinkedHashMap<String, String>();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(countriesFile), Charset.forName("ISO-8859-1")));
			String currentLine = "";
			while((currentLine = reader.readLine()) != null) {
				String[] country = currentLine.split("\t");

				if(country.length != 2) {
					log.error("Error parsing countries file - not a pair CODE\tCOUNTRY: " + currentLine);
				}
				else {
					countryMap.put(country[1].trim().toUpperCase(), country[0].toLowerCase().trim());
				}
			}
			reader.close();
		}
		catch(IOException e) {
			log.error("Error loading countries from file: " + RepoxContextUtil.COUNTRIES_FILENAME, e);
		}
	}

	public static void main(String[] args) {
		for (String key : Countries.getCountries().keySet()) {
			System.out.println("[" + key + ";" + Countries.getCountries().get(key) + "]");

		}
	}
}
