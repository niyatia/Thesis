package com.neu.arithmeticproblemsolver.featureextractor;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors (prefix = "m")
public class SentenceInstance {
	
	private boolean mIsItALastSentence;
	private boolean mIsItAFirstSentence;
	private boolean mHasACardinal;
	private boolean mHasASubject;
	private boolean mHasAnActionVerb;
	private boolean mHasADirectObject;
	private boolean mHasAWHAdverb;
	private boolean mHasAExpletive;
	private boolean mHasComparativeAdverb;
	private boolean mHasSuperlativeAdverb;
	private boolean mHasPastFormVerb;
	private boolean mHasBaseFormVerb;

	private final int mQuestionIndex;
	private final String mSentenceText;
	private final int mSentenceIndex;
	private final Set<FeatureDependency> mFeatureDependencies;
	
	public SentenceInstance (final int questionIndex, final int sentenceIndex, final String sentenceText, final Set<FeatureDependency> featureDependecies) {
		mQuestionIndex = questionIndex;
		mSentenceText = sentenceText;
		mFeatureDependencies = featureDependecies;
		mSentenceIndex = sentenceIndex;
		processFeatureDependencies();
	}
	
	/**
	 * 
	 * 
	 */
	private void processFeatureDependencies() {
		//TODO
	}

	@Override
	public String toString() {
		return "SentenceInstance [mSentenceIndex=" + mSentenceIndex + ", mSentenceText=" + mSentenceText + ", mDependencies=" + mFeatureDependencies + "]";
	}
	
	
}
