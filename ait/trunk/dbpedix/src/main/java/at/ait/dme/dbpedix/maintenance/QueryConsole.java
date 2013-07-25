package at.ait.dme.dbpedix.maintenance;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.lucene.queryParser.ParseException;

import at.ait.dme.dbpedix.DBpediaIndexReader;
import at.ait.dme.dbpedix.DBpediaResource;

/**
 * An executable class that implements a basic console which can be used
 * to run test queries against the index. Type your query in the form
 * 
 *     query:lang
 *  
 * E.g. 'vienna:en' will search for 'vienna' in the English-language labels.
 * 'wien:de' will search in the German-language labels. If you omit the language 
 * postfix (i.e. type only 'vienna'), the console will assume English as
 * the default language.
 * 
 * @author Rainer Simon <rainer.simon@ait.ac.at>
 */
public class QueryConsole {

	public static void main(String[] args) throws IOException, ParseException {
		DBpediaIndexReader index = DBpediaIndexReader.getInstance();
			
		String cmd = "";
		String query, lang;
		BufferedReader in = new BufferedReader( new InputStreamReader(System.in));
		while (true) {
			System.out.print("> ");
			cmd = in.readLine();
			if (cmd.toLowerCase().equals("quit"))
				break;
			
			if (cmd.lastIndexOf(':') > -1) {
				query = cmd.substring(0, cmd.lastIndexOf(':'));
				lang = cmd.substring(cmd.lastIndexOf(':') + 1).toLowerCase();
			} else {
				query = cmd;
				lang = "en";
			}
						
			for (DBpediaResource r : index.findResources(query, lang, 15)) {
				System.out.println(r.toString());
				System.out.println("*");
			}
		}
		index.close();
	}
	
}
