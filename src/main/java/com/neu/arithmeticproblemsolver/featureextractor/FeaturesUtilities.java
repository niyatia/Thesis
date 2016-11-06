package com.neu.arithmeticproblemsolver.featureextractor;

import java.util.ArrayList;
import java.util.List;

import com.neu.arithmeticproblemsolver.ngramranker.RankedNGram;

import edu.stanford.nlp.ling.IndexedWord;

public class FeaturesUtilities {

	private static List<RankedNGram> TOP_NGRAMS;
	/**
	 * Gets the String format for a IndexedWord(StanfordCoreNLP)
	 * @param indexedWord: either a dep or gov of a TypedDependency
	 * @return the word.
	 */
	public static String dependencyToWord(final IndexedWord indexedWord) {
		return indexedWord.backingLabel().getString(edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation.class);
	}

	public static void setTopNGrams(final List<RankedNGram> topNGrams) {
		TOP_NGRAMS = topNGrams;
	}
	
	public static List<RankedNGram> getTopNGrams() {
		return TOP_NGRAMS;
	}
}
