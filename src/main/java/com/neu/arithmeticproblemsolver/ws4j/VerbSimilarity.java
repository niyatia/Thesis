package com.neu.arithmeticproblemsolver.ws4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class VerbSimilarity {
	private static final NictWordNet db;
	public static final List<String> POSITIVE_VERBS;
	public static final List<String> NEGATIVE_VERBS;	
    private static Map<String, String> LEMMA_MAP;
    private static final RelatednessCalculator WUP;
	
    static {
		//POSITIVE_VERBS.addAll(Arrays.asList("plant", "play", "immigrate", "attend", "sprint", "run", "rent", "hold", "score", "fix", "pick", "find", "record", "bear", "finish", "catch", "order", "add", "grow", "need", "want", "come", "require", "gather","hike","measure","adopt","drive","collect","weigh","shop","save","ride","result","see","pave","get","land","end","stand","win","make","receive","harvest","go","start","purchase","increase","produce","dye","earn","borrow","travel","walk"));
		//NEGATIVE_VERBS.addAll(Arrays.asList("stack","put","give","pay","fill","feed","spend","eat","place","sell","pour"));
    	
    	//POSITIVE_VERBS = Arrays.asList("score","order","weigh","go","place","serve","buy","plant","use","add","walk","get","make","be","start","find","grow","pick","be","have");
    	//NEGATIVE_VERBS = Arrays.asList("delete","sell","lose","take","give”,”eat”,”spent","threw","leave","do","lose","share","cut","borrow","want","damage”,”remove”,”cost","wait","grade");
    	
    	POSITIVE_VERBS = Arrays.asList("score","order","go","place","serve","buy","plant","add","make","find","grow","pick","have");
    	NEGATIVE_VERBS = Arrays.asList("delete","sell","lose","give","eat","spent","threw","leave","share","cut","damage","remove","cost");
		
		callService("initialize");
		LEMMA_MAP = new HashMap<>();
		WS4JConfiguration.getInstance().setMFS(false);	
		db = new NictWordNet();
		WUP = new WuPalmer(db);
	}
	
	/*private static RelatednessCalculator[] rcs = {
			new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
	};*/
	
	private static double similarity(final String word1, final String word2 ) {
		double result = WUP.calcRelatednessOfWords(word1, word2);
		if (result == Double.MAX_VALUE) {
			result  = 1;
		} else if (result == Double.MIN_VALUE) {
			result = 0;
		}
//		final NumberFormat formatter = new DecimalFormat("0.00");
//		System.out.println(formatter.format(result));
		return result;
	}
	
	public static double getVerbClass(final String verb) {
		
		double maxPositiveSimilarity = Double.MIN_VALUE;
		double maxNegativeSimilarity = Double.MIN_VALUE;
		
		for (final String positiveVerb: POSITIVE_VERBS) {
			final double similarity = similarity(verb, positiveVerb);
			//System.out.println("Pos:" + positiveVerb + ":" + similarity);
			maxPositiveSimilarity = Math.max(similarity, maxPositiveSimilarity);
		}
		
		for (final String negativeVerb: NEGATIVE_VERBS) {
			final double similarity = similarity(verb, negativeVerb);
			//System.out.println("Neg:" + negativeVerb + ":" + similarity);
			maxNegativeSimilarity = Math.max(similarity, maxNegativeSimilarity);
		}
		
		return maxPositiveSimilarity > maxNegativeSimilarity ? 1.0 : 0.0;
	}
	
	public static List<Double> getVerbSimilarity(final String verb) {
		final String methodWithParams = "lemmatize/" + verb;
		final String verbLemma = LEMMA_MAP.containsKey(verb) ? LEMMA_MAP.get(verb) : callService(methodWithParams);
		
		if (!LEMMA_MAP.containsKey(verb)) {
			LEMMA_MAP.put(verb, verbLemma);
		}
		
		final List<Double> verbSimilarities = new ArrayList<>();
		for (final String positiveVerb : POSITIVE_VERBS) {
			final double similarity = similarity(verbLemma, positiveVerb);
			verbSimilarities.add(similarity);
		}
		
		for (final String negativeVerb : NEGATIVE_VERBS) {
			final double similarity = similarity(verbLemma, negativeVerb);
			verbSimilarities.add(similarity);
		}
		
		return verbSimilarities;
		
	}
	
	public static String callService(final String methodWithParams) {
		String response = null;
		 try {
				final URL url = new URL("http://127.0.0.1:5000/" + methodWithParams);
				final HttpURLConnection conn = (HttpURLConnection) url.openConnection();
				conn.setRequestMethod("GET");
				
				if (conn.getResponseCode() != 200) {
					throw new RuntimeException("Failed : HTTP error code : "
							+ conn.getResponseCode());
				}

				final BufferedReader br = new BufferedReader(new InputStreamReader(
					(conn.getInputStream())));

				response = br.readLine();
				conn.disconnect();

			  } catch (final MalformedURLException e) {
				  e.printStackTrace();
			  } catch (final IOException e) {
				  e.printStackTrace();
			  }
		 
		 return response;
	}
	public static void main(String[] args) {
		//System.out.print(getVerbClass("found"));
		System.out.println(getVerbSimilarity("found"));

	}
}
