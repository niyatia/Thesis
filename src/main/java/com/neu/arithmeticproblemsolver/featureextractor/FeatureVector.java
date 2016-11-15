package com.neu.arithmeticproblemsolver.featureextractor;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

import com.neu.arithmeticproblemsolver.ngramranker.RankedNGram;

public class FeatureVector {

	private static StringBuilder COLUMN_ORDER = null;
	
	private final boolean mConsiderNGramFeatures;
	private final boolean mConsiderWordFrequencyFeatures;
	
	private final boolean mIsItALastSentence;
	private final boolean mIsItAFirstSentence;
	private final boolean mHasACardinal;
	private final boolean mHasASubject;
	private final boolean mHasAnActionVerb;
	private final boolean mHasADirectObject;
	private final boolean mHasAWHAdverb;
	private final boolean mHasAExpletive;
	private final boolean mHasComparativeAdverb;
	private final boolean mHasSuperlativeAdverb;
	private final boolean mHasPastFormVerb;
	private final boolean mHasBaseFormVerb;
	//private final double mVerbClass;
	private final List<Double> mVerbSimilarities;

	private final List<Double> mFeatureVector;

	private final SentenceInstance mSentenceInstance;
	
	public FeatureVector(final SentenceInstance sentenceInstance,
			final boolean considerNGramFeatures,
			final boolean considerWordFrequencyFeatures) {
		mConsiderNGramFeatures = considerNGramFeatures;
		mConsiderWordFrequencyFeatures = considerWordFrequencyFeatures;

		mSentenceInstance = sentenceInstance;
		mIsItALastSentence = mSentenceInstance.isItALastSentence();
		mIsItAFirstSentence = mSentenceInstance.isItAFirstSentence();
		mHasACardinal = mSentenceInstance.isHasACardinal();
		mHasASubject = mSentenceInstance.isHasASubject();
		mHasAnActionVerb = mSentenceInstance.isHasAnActionVerb();
		mHasADirectObject = mSentenceInstance.isHasADirectObject();
		mHasAWHAdverb = mSentenceInstance.isHasAWHAdverb();
		mHasAExpletive = mSentenceInstance.isHasAExpletive();
		mHasComparativeAdverb = mSentenceInstance.isHasComparativeAdverb();
		mHasSuperlativeAdverb = mSentenceInstance.isHasSuperlativeAdverb();
		mHasPastFormVerb = mSentenceInstance.isHasPastFormVerb();
		mHasBaseFormVerb = mSentenceInstance.isHasBaseFormVerb();
		
		/** Verb Class based on Hosseini*/
		//mVerbClass = mSentenceInstance.getVerbClass();
		mVerbSimilarities = mSentenceInstance.getVerbSimilarities();
		mFeatureVector = prepareFeatureVector();
	}
	
	private int longestCommonSubSequence(final String stringOne, final String stringTwo) {
		
		final int strOneLength = stringOne.length();
		final int strTwoLength = stringTwo.length();
		final int[][] matrix = new int[strOneLength + 1][strTwoLength + 1];
		
		for (int i = 1; i <= strOneLength; i++) {
			for (int j = 1; j <= strTwoLength; j++) {
				if (stringOne.charAt(i - 1) == stringTwo.charAt(j - 1)) {
					matrix[i][j] = matrix[i-1][j-1] + 1;
				} else {
					matrix[i][j] = Math.max(matrix[i][j - 1], matrix[i-1][j]);
				}
			}
		}
		
		return matrix[strOneLength][strTwoLength];
	}
	
	private List<Double> prepareFeatureVector() {
		final List<Double> featureVector = new ArrayList<>();
		featureVector.add(mIsItAFirstSentence ? 1.0 : 0.0);
		featureVector.add(mIsItALastSentence ? 1.0 : 0.0);
		featureVector.add(mHasACardinal ? 1.0 : 0.0);
		featureVector.add(mHasASubject ? 1.0 : 0.0);
		featureVector.add(mHasAnActionVerb ? 1.0 : 0.0);
		featureVector.add(mHasADirectObject ? 1.0 : 0.0);
		featureVector.add(mHasAWHAdverb ? 1.0 : 0.0);
		featureVector.add(mHasAExpletive ? 1.0 : 0.0);
		featureVector.add(mHasComparativeAdverb ? 1.0 : 0.0);
		featureVector.add(mHasSuperlativeAdverb ? 1.0 : 0.0);
		featureVector.add(mHasPastFormVerb ? 1.0 : 0.0);
		featureVector.add(mHasBaseFormVerb ? 1.0 : 0.0);
		
		for (double verbSimilarity: mVerbSimilarities) {
			featureVector.add(verbSimilarity);
		}
		/** Verb Class based on Hosseini*/
		//featureVector.add(mVerbClass);
		
		if (mConsiderNGramFeatures) {
			final List<RankedNGram> nGrams = FeaturesUtilities.getTopNGrams();
			
			for (final RankedNGram rankedNGram: nGrams) {
				final double longestCommonSunSequenceScore = (double) longestCommonSubSequence(rankedNGram.getPattern(), mSentenceInstance.getSyntacticPattern());
				featureVector.add(longestCommonSunSequenceScore);
			}
		}
		return featureVector;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append(mSentenceInstance.getLabel() + ",");
		for (final Double feature: mFeatureVector) {
			final NumberFormat formatter = new DecimalFormat("0.00");
			sb.append(formatter.format(feature));
			sb.append(",");
		}
		sb.deleteCharAt(sb.lastIndexOf(","));
		sb.append("\n");
		return sb.toString();
	}
	
	
}
