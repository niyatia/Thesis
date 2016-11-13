package com.neu.arithmeticproblemsolver.featureextractor;

import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.ADDITION_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.EQUALS_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.IRRELEVANT_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.QUESTION_LABEL;
import static com.neu.arithmeticproblemsolver.featureextractor.PublicKeys.SUBTRACTION_LABEL;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.neu.arithmeticproblemsolver.ws4j.VerbSimilarity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@EqualsAndHashCode
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
	/** 1=addition, 0=subtraction */
	private double mVerbClass;

	private final int mQuestionIndex;
	private final String mSentenceText;
	private final int mSentenceIndex;
	private final Set<FeatureDependency> mFeatureDependencies;
	private final String mSyntacticPattern;
	private final String mLabel;
	
	/**
	 * Package-Protected Constructor 
	 * @param questionIndex: index of the question in the dataset.
	 * @param sentenceIndex: index of the sentence in the question.
	 * @param sentenceText; text of the sentene.
	 * @param featureDependecies: dependencies associated with this sentence.
	 */
	SentenceInstance (final int questionIndex, final int sentenceIndex, final String sentenceText,
			final Set<FeatureDependency> featureDependecies, final String syntacticPattern, final String label) {
		mQuestionIndex = questionIndex;
		mSentenceText = sentenceText;
		mFeatureDependencies = featureDependecies;
		mSentenceIndex = sentenceIndex;
		processFeatureDependencies();
		mSyntacticPattern = syntacticPattern;
		mLabel = label;
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
		mVerbClass = getVerbClassBasedOnSimilarity();
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
	
	private double getVerbClassBasedOnSimilarity() {
		double verbClass = -1.0;
		if (isHasACardinal()) {
    		int cardinalIndex = -1;
    		for (final FeatureDependency featureDependency: mFeatureDependencies) {			
    			if (featureDependency.getDepTag().equals("CD")) {
    				cardinalIndex = featureDependency.getDepIndex();
    				break;
    			} else if (featureDependency.getGovTag().equals("CD")) {
    				cardinalIndex = featureDependency.getGovIndex();
    				break;
    			} 			
    		}
    		
    		final Set<Integer> uniqueVerbIndices = new HashSet<>();
    		double depVerbClass = -1.0;
			double govVerbClass = -1.0;
    		for (final FeatureDependency featureDependency: mFeatureDependencies) {    			
    			if (!uniqueVerbIndices.contains(featureDependency.getDepIndex())
            			&& !Verb.valueOfNullable(featureDependency.getDepTag()).equals(Verb.NONE)
            			&& featureDependency.getDepIndex() < cardinalIndex) {
    				uniqueVerbIndices.add(featureDependency.getDepIndex());
    				final String verb = featureDependency.getDepWord();
        			depVerbClass = VerbSimilarity.getVerbClass(verb);
    			}
    			
    			if (!uniqueVerbIndices.contains(featureDependency.getGovIndex())
            			&& !Verb.valueOfNullable(featureDependency.getGovTag()).equals(Verb.NONE)
            			&& featureDependency.getGovIndex() < cardinalIndex) {
    				uniqueVerbIndices.add(featureDependency.getGovIndex());
    				final String verb = featureDependency.getGovWord();
        			govVerbClass = VerbSimilarity.getVerbClass(verb);
    			}
    		}
    		
    		if (depVerbClass == -1) {
    			verbClass = govVerbClass;
    		} else if (govVerbClass == -1) {
    			verbClass = depVerbClass;
    		} else {
    			verbClass = depVerbClass == 0.0 ? depVerbClass : govVerbClass; 
    		}
		}
		
		return verbClass;
	}
	
	@Override
	public String toString() {
		return "SentenceInstance [mSentenceIndex=" + mSentenceIndex + ", mSentenceText=" + mSentenceText + ", mDependencies=" + mFeatureDependencies + "]";
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SentenceInstance other = (SentenceInstance) obj;
		if (mFeatureDependencies == null) {
			if (other.mFeatureDependencies != null)
				return false;
		} else if (!mFeatureDependencies.equals(other.mFeatureDependencies))
			return false;
		if (mHasACardinal != other.mHasACardinal)
			return false;
		if (mHasADirectObject != other.mHasADirectObject)
			return false;
		if (mHasAExpletive != other.mHasAExpletive)
			return false;
		if (mHasASubject != other.mHasASubject)
			return false;
		if (mHasAWHAdverb != other.mHasAWHAdverb)
			return false;
		if (mHasAnActionVerb != other.mHasAnActionVerb)
			return false;
		if (mHasBaseFormVerb != other.mHasBaseFormVerb)
			return false;
		if (mHasComparativeAdverb != other.mHasComparativeAdverb)
			return false;
		if (mHasPastFormVerb != other.mHasPastFormVerb)
			return false;
		if (mHasSuperlativeAdverb != other.mHasSuperlativeAdverb)
			return false;
		if (mIsItAFirstSentence != other.mIsItAFirstSentence)
			return false;
		if (mIsItALastSentence != other.mIsItALastSentence)
			return false;
		if (mLabel == null) {
			if (other.mLabel != null)
				return false;
		} else if (!mLabel.equals(other.mLabel))
			return false;
		if (mQuestionIndex != other.mQuestionIndex)
			return false;
		if (mSentenceIndex != other.mSentenceIndex)
			return false;
		if (mSentenceText == null) {
			if (other.mSentenceText != null)
				return false;
		} else if (!mSentenceText.equals(other.mSentenceText))
			return false;
		if (mSyntacticPattern == null) {
			if (other.mSyntacticPattern != null)
				return false;
		} else if (!mSyntacticPattern.equals(other.mSyntacticPattern))
			return false;
		return true;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((mFeatureDependencies == null) ? 0 : mFeatureDependencies.hashCode());
		result = prime * result + (mHasACardinal ? 1231 : 1237);
		result = prime * result + (mHasADirectObject ? 1231 : 1237);
		result = prime * result + (mHasAExpletive ? 1231 : 1237);
		result = prime * result + (mHasASubject ? 1231 : 1237);
		result = prime * result + (mHasAWHAdverb ? 1231 : 1237);
		result = prime * result + (mHasAnActionVerb ? 1231 : 1237);
		result = prime * result + (mHasBaseFormVerb ? 1231 : 1237);
		result = prime * result + (mHasComparativeAdverb ? 1231 : 1237);
		result = prime * result + (mHasPastFormVerb ? 1231 : 1237);
		result = prime * result + (mHasSuperlativeAdverb ? 1231 : 1237);
		result = prime * result + (mIsItAFirstSentence ? 1231 : 1237);
		result = prime * result + (mIsItALastSentence ? 1231 : 1237);
		result = prime * result + ((mLabel == null) ? 0 : mLabel.hashCode());
		result = prime * result + mQuestionIndex;
		result = prime * result + mSentenceIndex;
		result = prime * result + ((mSentenceText == null) ? 0 : mSentenceText.hashCode());
		result = prime * result + ((mSyntacticPattern == null) ? 0 : mSyntacticPattern.hashCode());
		return result;
	}
	
	
}
