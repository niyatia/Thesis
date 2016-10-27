package com.neu.arithmeticproblemsolver.featureextractor;

import java.util.Arrays;
import java.util.HashSet;
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
	
	/**
	 * Package-Protected Constructor 
	 * @param questionIndex: index of the question in the dataset.
	 * @param sentenceIndex: index of the sentence in the question.
	 * @param sentenceText; text of the sentene.
	 * @param featureDependecies: dependencies associated with this sentence.
	 */
	SentenceInstance (final int questionIndex, final int sentenceIndex,
			final String sentenceText, final Set<FeatureDependency> featureDependecies) {
		mQuestionIndex = questionIndex;
		mSentenceText = sentenceText;
		mFeatureDependencies = featureDependecies;
		mSentenceIndex = sentenceIndex;
		processFeatureDependencies();
	}
	
	/**
	 * extracts the feature values from the feature dependencies.
	 */
	private void processFeatureDependencies() {
		//TODO
		mHasACardinal = hasAPartsOfSpeechAndRelations(true, "CD", "dobj");
		mHasASubject = hasAPartsOfSpeechAndRelations(true, "nsubj");
		mHasADirectObject = hasAPartsOfSpeechAndRelations(true, "dobj");
		mHasAWHAdverb = hasAPartsOfSpeechAndRelations(true, "WRB");
		mHasAExpletive = hasAPartsOfSpeechAndRelations(true, "EX");
		mHasComparativeAdverb = hasAPartsOfSpeechAndRelations(true, "RBR");
		mHasSuperlativeAdverb = hasAPartsOfSpeechAndRelations(true, "RBS");
		mHasPastFormVerb = hasAPartsOfSpeechAndRelations(true, "VBD");
		mHasBaseFormVerb = hasAPartsOfSpeechAndRelations(true, "VB");
		
		mIsItAFirstSentence = isFirstSentence();
	}

	private boolean isFirstSentence() {
		return mSentenceIndex == 1;
	}
	
	private boolean hasAPartsOfSpeechAndRelations(final boolean allTagsAreMust, final String... requiredTagsAndRelations) {
		
		final Set<String> tagsAndRelations = new HashSet<>();
		tagsAndRelations.addAll(Arrays.asList(requiredTagsAndRelations));
		
		final Set<String> foundTagsAndRelations = new HashSet<>();
		
		for (final FeatureDependency featureDependency: mFeatureDependencies) {
			final String relation = featureDependency.getRelation();
			final String dep = featureDependency.getDepTag();
			final String gov = featureDependency.getGovTag();
			
			if (tagsAndRelations.contains(relation)) {
				foundTagsAndRelations.add(relation);
			} else if (tagsAndRelations.contains(dep)) {
				foundTagsAndRelations.add(dep);
			} else if (tagsAndRelations.contains(gov)) {
				foundTagsAndRelations.add(gov);
			}		
		}
				
		return ((allTagsAreMust && foundTagsAndRelations.size() == tagsAndRelations.size()) ||
				(!allTagsAreMust && foundTagsAndRelations.size() > 0));
	}
	
	@Override
	public String toString() {
		return "SentenceInstance [mSentenceIndex=" + mSentenceIndex + ", mSentenceText=" + mSentenceText + ", mDependencies=" + mFeatureDependencies + "]";
	}
	
	
}
