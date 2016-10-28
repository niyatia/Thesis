package com.neu.arithmeticproblemsolver.ngramranker;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RankerFeatures {

	private static final String NVN_TRIPLET = "NVN";
	private static final String EVN_TRIPLET = "EVN";
	private static final String AN_TRIPLET = "AN";

	public SortedSet<RankedNGram> extractTopKPatterns(final Set<String> patterns) {
		final SortedSet<RankedNGram> rankedNGrams = new TreeSet<>(getRankedNGramsComparator());
		
		for (final String pattern: patterns) {
			final float neighboringPOSScore = getNeighboringScore(pattern);
			final float tripletScore = getTripletScore(pattern);
			final float anPatternScore = getANPatternScore(pattern);
			final float maxPatternLimitScore = getMaxOccurenceLimitScore(pattern);
			final float frequencyRatioScore = getFrequencyCountScore(pattern);
			
			final float score = neighboringPOSScore + tripletScore + anPatternScore +
					maxPatternLimitScore + frequencyRatioScore;
			final RankedNGram rankedNGram = new RankedNGram(pattern, score);
			rankedNGrams.add(rankedNGram);
		}
		
		return rankedNGrams;
	}
	
	/**
	 * Gets the score based on the neighboring parts of speech in the pattern. 
	 * The score is higher if the neighboring parts of speech are different in the pattern.
	 * @param pattern the pattern
	 * @return the score based on the neighboring parts of speech in the pattern.
	 */
	private float getNeighboringScore(final String pattern) {
		final char[] partsOfSpeech = pattern.toCharArray();
		final int noOfPartsOfSpeech = partsOfSpeech.length;
		if (noOfPartsOfSpeech <= 1) {
			return 0;
		}
		
		float score = 0;
		for (int counter = 0; counter < noOfPartsOfSpeech; counter++) {
			final char currentChar = partsOfSpeech[counter];
			if (counter == 0) {
				final char nextChar = partsOfSpeech[counter + 1];
				if (currentChar != nextChar) {
					score++;
				}
			} else if (counter == noOfPartsOfSpeech - 1) {
				final char prevChar = partsOfSpeech[counter - 1];
				if (currentChar != prevChar) {
					score++;
				}
			} else {
				final char prevChar = partsOfSpeech[counter - 1];
				final char nextChar = partsOfSpeech[counter + 1];
				if (currentChar != prevChar) {
					score += 0.5;
				}
				if (currentChar != nextChar) {
					score += 0.5;
				}
			}
		}
		
		return score;
	}
	
	private float getTripletScore(final String pattern) {
		float score = 0;
		if (pattern.length() < 3) {
			return score;
		}
		int firstNounIndex = pattern.indexOf("N");
		int expletiveIndex = pattern.indexOf("E");
		int lastNounIndex = pattern.lastIndexOf("N");
		
		
		return 0;
	}
	
	private float getANPatternScore(final String pattern) {
		float score = 0;
		if (pattern.contains(AN_TRIPLET)) {
			score = 1;
		}
		return score;
	}
	
	private float getMaxOccurenceLimitScore(final String pattern) {
		return 0;
	}
	
	private float getFrequencyCountScore(final String pattern) {
		return 0;
	}
	
	private Comparator<RankedNGram> getRankedNGramsComparator() {
		return new Comparator<RankedNGram>() {
			@Override
			public int compare(RankedNGram o1, RankedNGram o2) {
				return o1.getScore() - o2.getScore();
			}
			
		};
	}
}
