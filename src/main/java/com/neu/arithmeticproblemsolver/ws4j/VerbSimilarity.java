package com.neu.arithmeticproblemsolver.ws4j;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.cmu.lti.lexical_db.ILexicalDatabase;
import edu.cmu.lti.lexical_db.NictWordNet;
import edu.cmu.lti.ws4j.RelatednessCalculator;
import edu.cmu.lti.ws4j.impl.WuPalmer;
import edu.cmu.lti.ws4j.util.WS4JConfiguration;

public class VerbSimilarity {
	private static ILexicalDatabase db = new NictWordNet();
	private static final List<String> POSITIVE_VERBS = new ArrayList<>();
	private static final List<String> NEGATIVE_VERBS = new ArrayList<>();
	
	static {
		POSITIVE_VERBS.addAll(Arrays.asList("plant", "play", "immigrate", "attend", "sprint", "run", "rent", "hold", "score", "fix", "pick", "find", "record", "bear", "finish", "catch", "order", "add", "grow", "need", "want", "come", "require", "gather","hike","measure","adopt","drive","collect","weigh","shop","save","ride","result","see","pave","get","land","end","stand","win","make","receive","harvest","go","start","purchase","increase","produce","dye","earn","borrow","travel","walk"));
		NEGATIVE_VERBS.addAll(Arrays.asList("stack","put","give","pay","fill","feed","spend","eat","place","sell","pour"));
	}
	
	/*private static RelatednessCalculator[] rcs = {
			new HirstStOnge(db), new LeacockChodorow(db), new Lesk(db),  new WuPalmer(db), 
			new Resnik(db), new JiangConrath(db), new Lin(db), new Path(db)
	};*/
	
	private static double similarity( String word1, String word2 ) {
		double result = 0;
		WS4JConfiguration.getInstance().setMFS(false);	
		final RelatednessCalculator wup = new WuPalmer(db);
		result = wup.calcRelatednessOfWords(word1, word2);			
		//System.out.println( wup.getClass().getName()+"\t"+result );		
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
	
	public static void main(String[] args) {
		System.out.print(getVerbClass("found"));
	}
}
