package at.ait.dme.yuma.server.gizmos;

import java.io.IOException;

import org.apache.log4j.Logger;

import de.spieleck.app.cngram.NGramProfiles;
import de.spieleck.app.cngram.NGramProfiles.RankResult;
import de.spieleck.app.cngram.NGramProfiles.Ranker;

/**
 * Language guesser.
 * 
 * @author Rainer Simon
 */
public class LanguageGuesser {
	
	/**
	 * The singleton instance
	 */
	private static LanguageGuesser instance = null;
	
	/**
	 * N-Gram profiles
	 */
	private NGramProfiles profiles = null;
	
	/**
	 * Certainty threshold
	 */
	private double threshold = 0.5;
	
	/**
	 * The logger
	 */
	private Logger log = Logger.getLogger(LanguageGuesser.class);
	
	/**
	 * Private constructur
	 */
	private LanguageGuesser() {		
		try {
			profiles = new NGramProfiles();
		} catch (IOException e) {
			log.error("Could not initialize language guesser: " + e.getMessage());
		}
	}

	public static LanguageGuesser getInstance() {
		if (instance == null)
			instance = new LanguageGuesser();
		
		return instance;
	}
	
	public void setThreshold(double threshold) {
		this.threshold = threshold;
	}
	
	public String guess(String text) {
		if (profiles == null)
			return null;
		
		Ranker ranker = profiles.getRanker();
		ranker.account(text);
		RankResult result = ranker.getRankResult();
		
		if (result.getScore(0) > threshold)
			return result.getName(0).toUpperCase();

		return null; 
	}
	
}
