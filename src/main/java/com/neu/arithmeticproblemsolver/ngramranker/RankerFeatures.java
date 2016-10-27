package com.neu.arithmeticproblemsolver.ngramranker;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

public class RankerFeatures {

	public SortedSet<RankedNGram> extractTopKPatterns(final Set<String> patterns) {
		final SortedSet<RankedNGram> rankedNGrams = new TreeSet<>(getRankedNGramsComparator());
		
		for (final String pattern: patterns) {
			final int neighboringPOSScore = getNeighboringScore(pattern);
			final int tripletScore = getTripletScore(pattern);
			final int anPatternScore = getANPatternScore(pattern);
			final int maxPatternLimitScore = getMaxOccurenceLimitScore(pattern);
			final int frequencyRatioScore = getFrequencyCountScore(pattern);
			
			final int score = neighboringPOSScore + tripletScore + anPatternScore +
					maxPatternLimitScore + frequencyRatioScore;
			final RankedNGram rankedNGram = new RankedNGram(pattern, score);
			rankedNGrams.add(rankedNGram);
		}
		
		return rankedNGrams;
	}
	
	private int getNeighboringScore(final String pattern) {
		return 0;
	}
	
	private int getTripletScore(final String pattern) {
		return 0;
	}
	
	private int getANPatternScore(final String pattern) {
		return 0;
	}
	
	private int getMaxOccurenceLimitScore(final String pattern) {
		return 0;
	}
	
	private int getFrequencyCountScore(final String pattern) {
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
