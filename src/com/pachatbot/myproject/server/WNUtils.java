package com.pachatbot.myproject.server;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.IIndexWord;
import edu.mit.jwi.item.IWord;
import edu.mit.jwi.item.IWordID;
import edu.mit.jwi.item.POS;
import edu.mit.jwi.morph.WordnetStemmer;

public abstract class WNUtils {

	private static IDictionary dict;
	
	static {
		try {
			// Construct the URL to the WordNet dictionary directory
			String wnhome = System.getenv("WNHOME");
			String path = wnhome + File.separator + "dict"; 
			URL url = new URL("file", null, path);
			
			// Construct the dictionary object and open it
			dict = new Dictionary(url); 
			dict.open();
			
			// Display debug message
			if (dict.isOpen()) 
				System.err.println("[INFO] WordNet data loaded successfully from " + path);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param lemma
	 * @param pos
	 * @return
	 */
	public static String getGlossof(String lemma, POS pos) {
		
		WordnetStemmer stemmer = new WordnetStemmer(dict);
		List<String> stems = stemmer.findStems(lemma, pos);
		
		for (String stem : stems) {
			
			// look up first sense of the word
			IIndexWord idxWord = dict.getIndexWord(stem, pos); 
			if (idxWord != null) {
				IWordID wordID = idxWord.getWordIDs().get(0);
				IWord word = dict.getWord(wordID);
				return word.getSynset().getGloss();
			}
		}
		return "not found '" + lemma + "' as " + pos;
	}
	
	
}
