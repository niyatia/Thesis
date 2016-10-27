package com.neu.arithmeticproblemsolver.featureextractor;

import edu.stanford.nlp.ling.IndexedWord;

public class FeaturesUtilities {

	/**
	 * Gets the String format for a IndexedWord(StanfordCoreNLP)
	 * @param indexedWord: either a dep or gov of a TypedDependency
	 * @return the word.
	 */
	public static String dependencyToWord(final IndexedWord indexedWord) {
		return indexedWord.backingLabel().getString(edu.stanford.nlp.ling.CoreAnnotations.ValueAnnotation.class);
	}
}
